import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao, users_dao
from app import constants # pylint: disable=C0413

class RecommendationsTestCase(TestCase):

  def setUp(self):
    super(RecommendationsTestCase, self).setUp()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    db_session_commit()

  def test_create_recommendations(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.get_episode_by_title(episode_title1, user.id).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.get_episode_by_title(episode_title2, user.id).id

    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id1).first()
    self.assertIsNone(recommended)

    self.app.post('api/v1/recommendations/{}/'.format(episode_id1))
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id1).first()
    self.assertEquals(recommended.episode_id, int(episode_id1))

    self.assertRaises(Exception, self.app.post(),
                      'api/v1/recommendations/{}/'.format(episode_id1))

    self.app.post('api/v1/recommendations/{}/'.format(episode_id2))
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id2).first()
    self.assertEquals(recommended.episode_id, int(episode_id2))

    recommendations = Recommendation.query.all()
    self.assertEquals(len(recommendations), 2)

  def test_get_user_recommendations(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.get_episode_by_title(episode_title1, user.id).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.get_episode_by_title(episode_title2, user.id).id

    test_user_id = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    response = self.app.get('api/v1/recommendations/users/{}/'.
                            format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 0)

    self.app.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.app.post('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.app.get('api/v1/recommendations/users/{}/'
                            .format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 2)

    self.assertEquals(len(data['data']['recommendations']), 2)
    self.assertTrue(
        data['data']['recommendations'][0]['episode']['id'] == int(episode_id1)
        or
        data['data']['recommendations'][1]['episode']['id'] == int(episode_id1)
    )
    self.assertTrue(
        data['data']['recommendations'][0]['episode']['id'] == int(episode_id2)
        or
        data['data']['recommendations'][1]['episode']['id'] == int(episode_id2)
    )

  def test_get_recommendations(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.get_episode_by_title(episode_title1, user.id).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.get_episode_by_title(episode_title2, user.id).id

    test_user_id = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    self.app.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.app.post('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.app.get('api/v1/recommendations/{}/?offset=0&max=5'
                            .format(episode_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 1)
    self.assertEquals(data['data']['recommendations'][0]['user']['id'],
                      test_user_id)

    response = self.app.get('api/v1/recommendations/{}/?offset=0&max=5'
                            .format(episode_id2))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 1)
    self.assertEquals(data['data']['recommendations'][0]['user']['id'],
                      test_user_id)

  def test_delete_recommendations(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    test_user_id = user.id
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.get_episode_by_title(episode_title1, user.id).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.get_episode_by_title(episode_title2, user.id).id

    self.app.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.app.post('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.app.get('api/v1/recommendations/users/{}/'
                            .format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 2)

    self.app.delete('api/v1/recommendations/{}/'.format(episode_id1))
    self.app.delete('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.app.get('api/v1/recommendations/users/{}/'
                            .format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 0)

    self.assertRaises(Exception, self.app.delete(),
                      'api/v1/recommendations/{}/'.format(episode_id1))

    self.assertRaises(Exception, self.app.delete(),
                      'api/v1/recommendations/{}/'.format(episode_id2))

  def test_is_recommendations(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()

    episode_title = 'Colombians to deliver their verdict on peace accord'
    episode_id = episodes_dao.get_episode_by_title(episode_title, user.id).id

    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertFalse(episode.is_recommended)

    self.app.post('api/v1/recommendations/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertTrue(episode.is_recommended)

    response = self.app.get('api/v1/recommendations/{}/?offset=0&max=5'
                            .format(episode_id))
    data = json.loads(response.data)
    self.assertTrue(
        data['data']['recommendations'][0]['episode']['is_recommended']
    )

    self.app.delete('api/v1/recommendations/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertFalse(episode.is_recommended)
