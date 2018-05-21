import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao, users_dao, recommendations_dao
from app import constants

class RecommendationsTestCase(TestCase):

  def setUp(self):
    super(RecommendationsTestCase, self).setUp()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    db_session_commit()

  def tearDown(self):
    super(RecommendationsTestCase, self).tearDown()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    db_session_commit()

  def test_create_recommendations(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id1).first()
    self.assertIsNone(recommended)

    response = self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id1).first()
    self.assertEquals(recommended.episode_id, int(episode_id1))
    self.assertIsNone(recommended.blurb)
    data = json.loads(response.data)['data']
    self.assertEquals(data['recommendation']['episode']['title'],
                      episode_title1)
    self.assertTrue(data['recommendation']['episode']['is_recommended'])

    bdata = json.dumps({'blurb': 'update'})
    response = self.user1.post('api/v1/recommendations/{}/'
                               .format(episode_id1), data=bdata)
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id1).first()
    self.assertEquals(recommended.blurb, "update")
    data = json.loads(response.data)['data']
    self.assertEquals(data['recommendation']['episode']['title'],
                      episode_title1)
    self.assertIsNotNone(data['recommendation']['episode']['is_recommended'])

    bdata = json.dumps({'blurb': None})
    self.user1.post('api/v1/recommendations/{}/'
                    .format(episode_id1), data=bdata)
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id1).first()
    self.assertIsNone(recommended.blurb)

    bdata = json.dumps({'blurb': 'test blurb'})
    self.user1.post('api/v1/recommendations/{}/'
                    .format(episode_id2), data=bdata)
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id2).first()
    self.assertEquals(recommended.episode_id, int(episode_id2))
    self.assertEquals(recommended.blurb, "test blurb")

    self.user1.post('api/v1/recommendations/{}/'
                    .format(episode_id2))
    recommended = Recommendation.query.\
        filter(Recommendation.episode_id == episode_id2).first()
    self.assertEquals(recommended.episode_id, int(episode_id2))
    self.assertIsNone(recommended.blurb)

    recommendations = Recommendation.query.all()
    self.assertEquals(len(recommendations), 2)

  def test_get_user_recommendations(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    test_user_id = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    response = self.user1.get('api/v1/recommendations/users/{}/'.
                              format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 0)

    bdata = json.dumps({'blurb': 'test'})
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user1.post('api/v1/recommendations/{}/'
                    .format(episode_id2), data=bdata)

    response = self.user1.get('api/v1/recommendations/users/{}/'
                              .format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 2)
    self.assertEquals(data['data']['recommendations'][1]['blurb'], 'test')

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

  def test_get_user_recommendations2(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    user_2 = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID2)

    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_title2 = 'Battle of the camera drones'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    recommendations_dao.create_or_update_recommendation(episode_id1, user_2)
    recommendations_dao.create_or_update_recommendation(episode_id2, user_2)
    recommendations_dao.create_or_update_recommendation(episode_id1, user)
    response = self.user1.get('api/v1/recommendations/users/{}/'
                              .format(user_2.id))
    data = json.loads(response.data)

    self.assertEquals(len(data['data']['recommendations']), 2)
    self.assertEquals(data['data']['recommendations'][0]['episode']\
        ['is_recommended'], True)
    self.assertEquals(data['data']['recommendations'][0]['episode']\
        ['recommendations_count'], 2)
    self.assertEquals(data['data']['recommendations'][1]['episode']\
        ['is_recommended'], False)
    self.assertEquals(data['data']['recommendations'][1]['episode']\
        ['recommendations_count'], 1)

  def test_get_recommendations(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    test_user_id = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/recommendations/{}/?offset=0&max=5'
                              .format(episode_id1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 1)
    self.assertEquals(data['data']['recommendations'][0]['user']['id'],
                      test_user_id)

    response = self.user1.get('api/v1/recommendations/{}/?offset=0&max=5'
                              .format(episode_id2))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 1)
    self.assertEquals(data['data']['recommendations'][0]['user']['id'],
                      test_user_id)

  def test_delete_recommendations(self):
    test_user_id = self.user1.uid
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/recommendations/users/{}/'
                              .format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['recommendations']), 2)

    response = self.user1.delete('api/v1/recommendations/{}/'
                                 .format(episode_id1))
    data = json.loads(response.data)['data']
    self.assertEquals(data['recommendation']['episode']['title'],
                      episode_title1)
    self.assertIsNotNone(
        data['recommendation']['episode']['is_recommended'])
    self.user1.delete('api/v1/recommendations/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/recommendations/users/{}/'
                              .format(test_user_id))
    data = json.loads(response.data)['data']
    self.assertEquals(len(data['recommendations']), 0)

    self.assertRaises(
        Exception,
        self.user1.delete('api/v1/recommendations/{}/'.format(episode_id1)),
    )

    self.assertRaises(
        Exception,
        self.user1.delete('api/v1/recommendations/{}/'.format(episode_id2)),
    )

  def test_is_recommendations(self):
    episode_title = 'Colombians to deliver their verdict on peace accord'
    episode_id = episodes_dao.\
        get_episode_by_title(episode_title, self.user1.uid).id

    episode = episodes_dao.get_episode(episode_id, self.user1.uid)
    self.assertFalse(episode.is_recommended)

    self.user1.post('api/v1/recommendations/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, self.user1.uid)
    self.assertTrue(episode.is_recommended)

    response = self.user1.get('api/v1/recommendations/{}/?offset=0&max=5'
                              .format(episode_id))
    data = json.loads(response.data)
    self.assertTrue(
        data['data']['recommendations'][0]['episode']['is_recommended']
    )

    self.user1.delete('api/v1/recommendations/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, self.user1.uid)
    self.assertFalse(episode.is_recommended)
