import sys
from flask import json
from tests.test_case import *
from app import constants # pylint: disable=C0413
import mock

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

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_series_for_topic(self, mock_get):
    response = self.user1.get('api/v1/discover/series/topic/1/?offset=0&max=1')
    series = json.loads(response.data)['data']['series']
    ids = [int(ser['id']) for ser in series]
    self.assertEquals(series_ids, ids)

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_series_for_user(self, mock_get):
    response = self.user1.get('api/v1/discover/series/user/?offset=0&max=1')
    series = json.loads(response.data)['data']['series']
    ids = [int(ser['id']) for ser in series]
    self.assertEquals(series_ids, ids)

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_episodes_for_topic(self, mock_get):
    response = self.user1.get('api/v1/discover/episodes/topic/3/?offset=0&max=1')
    episodes = json.loads(response.data)['data']['episodes']
    ids = [int(ep['id']) for ep in episodes]
    self.assertEquals(episode_ids, ids)

  @mock.patch('requests.get', side_effect=mocked_requests_get)
  def test_episodes_for_user(self, mock_get):
    response = self.user1.get('api/v1/discover/episodes/user/?offset=0&max=1')
    episodes = json.loads(response.data)['data']['episodes']
    ids = [int(ep['id']) for ep in episodes]
    self.assertEquals(episode_ids, ids)

  def test_ml_down(self):
    response = self.user1.get('api/v1/discover/episodes/user/')
    data = json.loads(response.data)
    self.assertFalse(data['success'])
