import sys
from flask import json
from tests.test_case import *
from app import constants # pylint: disable=C0413
from app import config
import mock
from app.pcasts.dao import episodes_dao, series_dao, recommendations_dao

series_ids = [s.id for s in Series.query.limit(10).all()]
episode_ids = [e.id for e in Episode.query.limit(10).all()]

# This method will be used by the mock to replace requests.get
def mocked_requests_get(*args, **kwargs):
    class MockResponse(object):
        def __init__(self, json_data, status_code):
            self.json_data = json_data
            self.status_code = status_code

        def json(self):
            return self.json_data
    if 'api/v1/series/' in args[0]:
        return MockResponse({
            "data": {
                "series_ids":
                    series_ids
                }
            }, 200)
    elif 'api/v1/episodes/' in args[0]:
        return MockResponse({
            "data": {
                "episode_ids":
                    episode_ids
                }
            }, 200)
    return MockResponse(None, 404)

class DiscoverTestCase(TestCase):

  def setUp(self):
    self.ML_value = config.ML_ENABLED
    super(DiscoverTestCase, self).setUp()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    SeriesForTopic.query.delete()
    db_session_commit()

  def tearDown(self):
    config.ML_ENABLED = self.ML_value
    super(DiscoverTestCase, self).tearDown()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  def test_ml_down(self):
    response = self.user1.get('api/v1/discover/episodes/user/')
    data = json.loads(response.data)
    self.assertFalse(data['success'])

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_series_for_user(self, mock_get):
    config.ML_ENABLED = True
    response = self.user1.get('api/v1/discover/series/user/?offset=0&max=1')
    series = json.loads(response.data)['data']['series']
    ids = [int(show['id']) for show in series]
    self.assertEquals(series_ids, ids)

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_episodes_for_user(self, mock_get):
    config.ML_ENABLED = True
    response = self.user1.get('api/v1/discover/episodes/user/?offset=0&max=1')
    episodes = json.loads(response.data)['data']['episodes']
    ids = [int(ep['id']) for ep in episodes]
    self.assertEquals(episode_ids, ids)

  def test_series_for_topic(self):
    sids = ','.join([str(sid) for sid in series_ids])
    sft1 = SeriesForTopic(topic_id=1323, series_list=sids)
    sft2 = SeriesForTopic(topic_id=1, series_list=sids)
    db_utils.commit_models([sft1, sft2])
    response = self.user1.get('api/v1/discover/series/topic/1323/' +
                              '?offset=0&max=10')
    series = json.loads(response.data)['data']['series']
    for show in series:
      self.assertIsNotNone(show['id'])
      self.assertIsNotNone(show['is_subscribed'])
    response = self.user1.get('api/v1/discover/series/topic/all/' +
                              '?offset=0&max=10')
    series = json.loads(response.data)['data']['series']
    for show in series:
      self.assertIsNotNone(show['id'])
      self.assertIsNotNone(show['is_subscribed'])

    response = self.user1.get('api/v1/discover/series/topic/all/' +
                              '?offset=2&max=5')
    series = json.loads(response.data)['data']['series']
    self.assertEquals(len(series), 5)

    response = self.user1.get('api/v1/discover/series/topic/all/' +
                              '?offset=0&max=50')
    series = json.loads(response.data)['data']['series']
    self.assertEquals(len(series), 10)

    response = self.user1.get('api/v1/discover/series/topic/all/' +
                              '?offset=12&max=10')
    series = json.loads(response.data)['data']['series']
    self.assertEquals(len(series), 0)

    response = self.user1.get('api/v1/discover/series/topic/all/' +
                              '?offset=8&max=40')
    series = [s['id'] for s in json.loads(response.data)['data']['series']]
    self.assertEquals(series_ids[8:], series)


  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_episodes_for_topic(self, mock_get):
    config.ML_ENABLED = True
    response = self.user1.get('api/v1/discover/episodes/topic/1323/' +
                              '?offset=0&max=10')
    episodes = json.loads(response.data)['data']['episodes']
    ids = [int(ep['id']) for ep in episodes]
    self.assertEquals(episode_ids, ids)

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_episodes_for_subtopic(self, mock_get):
    config.ML_ENABLED = True
    response = self.user1.get('api/v1/discover/episodes/topic/1443/' +
                              '?offset=0&max=10')
    episodes = json.loads(response.data)['data']['episodes']
    ids = [int(ep['id']) for ep in episodes]
    self.assertEquals(episode_ids, ids)

  # No ML endpoints
  def test_series_for_user_no_ml(self):
    sids = ','.join([str(sid) for sid in series_ids])
    sft1 = SeriesForTopic(topic_id=1323, series_list=sids)
    sft2 = SeriesForTopic(topic_id=1, series_list=sids)
    db_utils.commit_models([sft1, sft2])
    response = self.user1.get('api/v1/discover/series/user/' +
                              '?offset=0&max=10')
    series = json.loads(response.data)['data']['series']
    for show in series:
      self.assertIsNotNone(show['id'])
      self.assertIsNotNone(show['is_subscribed'])

  def test_episodes_for_user_no_ml(self):
    config.ML_ENABLED = False
    episode_title1 = '749 Filter Therapy'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id
    episode_title2 = '05-12-11 Brett Beer'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user2.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id2))

    response1 = self.user2.get('api/v1/discover/episodes/user/?offset=0&max=1')
    response2 = self.user2.get('api/v1/discover/episodes/user/?offset=1&max=1')
    episode1 = json.loads(response1.data)['data']['episodes'][0]
    episode2 = json.loads(response2.data)['data']['episodes'][0]

    self.assertTrue(int(episode1['id']) == int(episode_id1))
    self.assertTrue(int(episode2['id']) == int(episode_id2))
    self.assertTrue(episode1['recommendations_count'] >= \
        episode2['recommendations_count'])
    self.assertTrue(episode1['is_recommended'])
    self.assertFalse(episode2['is_recommended'])

  def test_episodes_for_topic_no_ml(self):
    config.ML_ENABLED = False
    # Testing topic: Games and Hobbies
    episode_title1 = '749 Filter Therapy'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id
    episode_title2 = '05-12-11 Brett Beer'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user2.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id2))
    request = self.user1.get('api/v1/discover/episodes/topic/1323/'+
                             '?offset=0&max=25')
    data = json.loads(request.data)['data']
    self.assertTrue(data['episodes'][0]['id'] == episode_id1)
    self.assertTrue(data['episodes'][0]['is_recommended'])
    self.assertFalse(data['episodes'][0]['is_bookmarked'])
    self.assertTrue(data['episodes'][0]['current_progress'] is None)
    self.assertTrue(data['episodes'][1]['id'] == episode_id2)
    self.assertFalse(data['episodes'][2]['is_recommended'])
    self.assertFalse(data['episodes'][2]['is_bookmarked'])
    self.assertTrue(data['episodes'][2]['current_progress'] is None)

  def test_episodes_for_subtopic_no_ml(self):
    config.ML_ENABLED = False
    # Testing subtopics(Philosophy)
    episode_title1 = '#1: Paul Dini'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id
    episode_title2 = 'High income vs High net worth where do you rank?'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user2.post('api/v1/recommendations/{}/'.format(episode_id1))
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id2))
    request = self.user1.get('api/v1/discover/episodes/topic/1443/'+
                             '?offset=0&max=25')
    data = json.loads(request.data)['data']

    self.assertTrue(data['episodes'][0]['id'] == episode_id1)
    self.assertTrue(data['episodes'][0]['is_recommended'])
    self.assertFalse(data['episodes'][0]['is_bookmarked'])
    self.assertTrue(data['episodes'][0]['current_progress'] is None)
    self.assertTrue(data['episodes'][1]['id'] == episode_id2)
    self.assertFalse(data['episodes'][2]['is_recommended'])
    self.assertFalse(data['episodes'][2]['is_bookmarked'])
    self.assertTrue(data['episodes'][2]['current_progress'] is None)

  def test_series_for_topic_invalid(self):
    config.ML_ENABLED = False
    request = self.user1.get('api/v1/discover/series/topic/-1/' +
                             '?offset=0&max=25')
    data = json.loads(request.data)
    self.assertFalse(data['success'])
    self.assertEquals(str(data['data']['errors'][0]), "No topic id -1 exists")

  def test_episodes_for_topic_invalid(self):
    config.ML_ENABLED = False
    request = self.user1.get('api/v1/discover/episodes/topic/-1/' +
                             '?offset=0&max=25')
    data = json.loads(request.data)
    self.assertFalse(data['success'])
    self.assertEquals(str(data['data']['errors'][0]), "Invalid topic id -1")
