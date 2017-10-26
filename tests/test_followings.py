from flask import json
from tests.test_case import *
from app import constants
from app.pcasts.dao import followings_dao, users_dao
from app.pcasts.controllers import \
    create_delete_following_controller, \
    get_user_followings_controller, \
    get_user_followers_controller

class FollowingsTestCase(TestCase):

  def setUp(self):
    super(FollowingsTestCase, self).setUp()
    Following.query.delete()
    db_session_commit()

  def test_create_following(self):
    test_user_id1 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id
    test_user_id2 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID2).id

    self.app.post('api/v1/followings/{}/'.format(test_user_id2))
    following = Following.query.\
        filter(Following.follower_id == test_user_id1).first()
    self.assertEquals(following.followed_id, test_user_id2)

    user1 = users_dao.get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1)
    user2 = users_dao.get_user_by_google_id(constants.TEST_USER_GOOGLE_ID2)
    self.assertEquals(int(user1.followers_count), 0)
    self.assertEquals(int(user1.followings_count), 1)
    self.assertEquals(int(user2.followers_count), 1)
    self.assertEquals(int(user2.followings_count), 0)

    self.assertRaises(Exception, self.app.post(),
                      'api/v1/followings/{}/'.format(test_user_id2))

  def test_get_user_followers_and_followings(self):
    test_user_id1 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id
    test_user_id2 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID2).id

    response = self.app.get('api/v1/followings/show/{}/'.format(test_user_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followings']), 0)

    self.app.post('api/v1/followings/{}/'.format(test_user_id2))

    response = self.app.get('api/v1/followings/show/{}/'.format(test_user_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followings']), 1)

    self.assertEquals(
        data['data']['followings'][0]['follower']['id'],
        test_user_id1
    )
    self.assertEquals(
        data['data']['followings'][0]['followed']['id'],
        test_user_id2
    )
    # From the point of view of user1, are we following user1 or user2
    self.assertFalse(
        data['data']['followings'][0]['follower']['is_following']
    )
    self.assertTrue(
        data['data']['followings'][0]['followed']['is_following']
    )

    response = self.app.get('api/v1/followers/show/{}/'.format(test_user_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followers']), 0)

    response = self.app.get('api/v1/followings/show/{}/'.format(test_user_id2))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followings']), 0)

    response = self.app.get('api/v1/followers/show/{}/'.format(test_user_id2))
    data = json.loads(response.data)

    self.assertEquals(len(data['data']['followers']), 1)
    self.assertEquals(
        data['data']['followers'][0]['follower']['id'],
        test_user_id1
    )
    self.assertEquals(
        data['data']['followers'][0]['followed']['id'],
        test_user_id2
    )
    # From the point of view of user1 are we following user1 and user2
    self.assertFalse(
        data['data']['followers'][0]['follower']['is_following']
    )
    self.assertTrue(
        data['data']['followers'][0]['followed']['is_following']
    )

  def test_delete_followings(self):
    test_user_id1 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id
    test_user_id2 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID2).id

    response = self.app.get('api/v1/followings/show/{}/'.format(test_user_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followings']), 0)

    self.app.post('api/v1/followings/{}/'.format(test_user_id2))

    response = self.app.get('api/v1/followings/show/{}/'.format(test_user_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followings']), 1)

    self.app.delete('api/v1/followings/{}/'.format(test_user_id2))

    response = self.app.get('api/v1/followings/show/{}/'.format(test_user_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['followings']), 0)

    self.assertRaises(Exception, self.app.delete(),
                      'api/v1/followings/{}/'.format(test_user_id2))

  def test_is_following(self):
    following = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    followed_id = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID2).first().id
    followed = users_dao.get_user_by_id(following.id, followed_id)
    self.assertFalse(followed.is_following)

    response = self.app.get('api/v1/users/{}/'.format(followed_id))
    data = json.loads(response.data)
    self.assertFalse(data['data']['user']['is_following'])

    self.app.post('api/v1/followings/{}/'.format(followed_id))
    followed = users_dao.get_user_by_id(following.id, followed_id)
    self.assertTrue(followed.is_following)

    response = self.app.get('api/v1/followings/show/{}/'.format(following.id))
    data = json.loads(response.data)
    self.assertTrue(data['data']['followings'][0]['followed']['is_following'])

    response = self.app.get('api/v1/users/{}/'.format(followed_id))
    data = json.loads(response.data)
    self.assertTrue(data['data']['user']['is_following'])

    self.app.delete('api/v1/followings/{}/'.format(followed_id))
    followed = users_dao.get_user_by_id(following.id, followed_id)
    self.assertFalse(followed.is_following)
