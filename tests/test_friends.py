import sys
from flask import json
from tests.test_case import *
from tests.test_user import *
from tests import api_utils
from app import constants # pylint: disable=C0413

class FriendsTestCase(TestCase):

  def setUp(self):
    super(FriendsTestCase, self).setUp()
    Following.query.delete()
    IgnoredUsers.query.delete()
    db_session_commit()

  def tearDown(self):
    super(FriendsTestCase, self).tearDown()
    Following.query.delete()
    IgnoredUsers.query.delete()
    db_session_commit()

  def test_get_friends(self):
    fb_app_token = api_utils.get_facebook_app_access_token()

    fb_user1 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB One')
    fb_user2 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Two')
    fb_user3 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Three')
    fb_user4 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Four')

    # No Friends
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(0, 10, 'true'))
    data = json.loads(response.data)['data']
    self.assertEquals(data['users'], [])

    # 1 Friend
    api_utils.create_facebook_friendship(fb_user1, fb_user2)
    response = fb_user2.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(0, 10, 'true'))
    data = json.loads(response.data)['data']

    self.assertTrue(len(data['users']) == 1)
    self.assertEquals(data['users'][0]['id'], fb_user1.uid)
    self.assertEquals(data['users'][0]['is_following'], False)

    # 3 Friends with different followings
    api_utils.create_facebook_friendship(fb_user1, fb_user3)
    api_utils.create_facebook_friendship(fb_user1, fb_user4)
    fb_user1.post('api/v1/followings/{}/'.format(fb_user2.uid))
    fb_user2.post('api/v1/followings/{}/'.format(fb_user3.uid))
    fb_user4.post('api/v1/followings/{}/'.format(fb_user3.uid))

    # return_following true (ordered by followings)
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(0, 10, 'true'))
    data = json.loads(response.data)['data']

    self.assertTrue(len(data['users']) == 3)
    self.assertEquals(data['users'][0]['id'], fb_user3.uid)
    self.assertEquals(data['users'][0]['is_following'], False)
    self.assertEquals(data['users'][1]['id'], fb_user2.uid)
    self.assertEquals(data['users'][1]['is_following'], True)
    self.assertEquals(data['users'][2]['id'], fb_user4.uid)
    self.assertEquals(data['users'][2]['is_following'], False)


    # return_following false (ordered by followings)
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(0, 10, 'false'))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 2)
    self.assertEquals(data['users'][0]['id'], fb_user3.uid)
    self.assertEquals(data['users'][0]['is_following'], False)
    self.assertEquals(data['users'][1]['id'], fb_user4.uid)
    self.assertEquals(data['users'][1]['is_following'], False)

    # test default value of return_following (true)
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}'.format(0, 10))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 3)
    self.assertEquals(data['users'][0]['id'], fb_user3.uid)
    self.assertEquals(data['users'][0]['is_following'], False)
    self.assertEquals(data['users'][1]['id'], fb_user2.uid)
    self.assertEquals(data['users'][1]['is_following'], True)
    self.assertEquals(data['users'][2]['id'], fb_user4.uid)
    self.assertEquals(data['users'][2]['is_following'], False)

    # Test limit and offset
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(0, 1, 'true'))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 1)
    self.assertEquals(data['users'][0]['id'], fb_user3.uid)

    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(2, 1, 'true'))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 1)
    self.assertEquals(data['users'][0]['id'], fb_user4.uid)

    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}&return_following={}'.format(1, 1, 'false'))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 1)
    self.assertEquals(data['users'][0]['id'], fb_user4.uid)

  def test_ignored_friends(self):
    fb_app_token = api_utils.get_facebook_app_access_token()

    fb_user1 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB One')
    fb_user2 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Two')
    fb_user3 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Three')

    api_utils.create_facebook_friendship(fb_user1, fb_user2)
    api_utils.create_facebook_friendship(fb_user1, fb_user3)

    # No ignored tested by regular get friends
    # First ignored user
    response = fb_user1.post('api/v1/users/facebook/friends/ignore/' + \
        '{}/'.format(fb_user2.fb_id))
    data = json.loads(response.data)
    self.assertTrue(data['success'])

    # Get friends with 1 ignored
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}'.format(0, 10))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 1)
    self.assertEquals(data['users'][0]['id'], fb_user3.uid)

    # Second ignored user
    response = fb_user1.post('api/v1/users/facebook/friends/ignore/' + \
        '{}/'.format(fb_user3.fb_id))
    data = json.loads(response.data)
    self.assertTrue(data['success'])

    # Get friends with 2 ignored
    response = fb_user1.get('api/v1/users/facebook/friends/' + \
            '?offset={}&max={}'.format(0, 10))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['users']) == 0)
