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
    super(DiscoverTestCase, self).setUp()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  def tearDown(self):
    super(DiscoverTestCase, self).tearDown()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_series_for_user(self, mock_get):
    response = self.user1.get('api/v1/discover/series/user/')
    series = json.loads(response.data)['data']['series']
    ids = [int(ser['id']) for ser in series]
    self.assertEquals(series_ids, ids)

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_episodes_for_user(self, mock_get):
    if config.ML_ENABLED:
      response = self.user1.get('api/v1/discover/episodes/user/')
      episodes = json.loads(response.data)['data']['episodes']
      ids = [int(ep['id']) for ep in episodes]
      self.assertEquals(episode_ids, ids)

  def test_ml_down(self):
    response = self.user1.get('api/v1/discover/episodes/user/')
    data = json.loads(response.data)
    self.assertFalse(data['success'])

  def test_episodes_for_topic_no_ml(self):
    if config.ML_ENABLED:
      response = self.user1.get('api/v1/discover/episodes/topic/1304/')
      episodes = json.loads(response.data)['data']['episodes']
      ids = [int(ep['id']) for ep in episodes]
      self.assertEquals(episode_ids, ids)
    else:
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
      self.assertTrue(data['episodes'][1]['id'] == episode_id2)


  def test_episodes_for_subtopic_no_ml(self):
    if config.ML_ENABLED:
      pass
    else:
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
      self.assertTrue(data['episodes'][1]['id'] == episode_id2)

  # Tests for disabled ML
  def test_series_for_topic_no_ml(self):
    if config.ML_ENABLED:
      response = self.user1.get('api/v1/discover/series/topic/1304/')
      series = json.loads(response.data)['data']['series']
      ids = [int(ep['id']) for ep in series]
      self.assertEquals(episode_ids, ids)
    else:
      #Topic: Games and Hobbies
      series_id1 = '73329429'
      series_id2 = '75092679'
      self.user1.post('api/v1/subscriptions/{}/'.format(series_id1))
      self.user2.post('api/v1/subscriptions/{}/'.format(series_id1))
      self.user1.post('api/v1/subscriptions/{}/'.format(series_id2))
      request = self.user1.get('api/v1/discover/series/topic/1323/' +
                               '?offset=0&max=25')
      data = json.loads(request.data)['data']
      self.assertTrue(int(data['series'][0]['id']) == int(series_id1))
      self.assertTrue(int(data['series'][1]['id']) == int(series_id2))

  def test_series_for_subtopic_no_ml(self):
    if config.ML_ENABLED:
      pass
    else:
      # Testing subtopics(Philosophy)
      series_id1 = '532661418'
      series_id2 = '896417058'
      self.user1.post('api/v1/subscriptions/{}/'.format(series_id1))
      self.user2.post('api/v1/subscriptions/{}/'.format(series_id1))
      self.user1.post('api/v1/subscriptions/{}/'.format(series_id2))
      request = self.user1.get('api/v1/discover/series/topic/1443/' +
                               '?offset=0&max=25')
      data = json.loads(request.data)['data']
      self.assertTrue(int(data['series'][0]['id']) == int(series_id1))
      self.assertTrue(int(data['series'][1]['id']) == int(series_id2))
