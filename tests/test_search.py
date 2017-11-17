import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao, users_dao, series_dao
from app import constants # pylint: disable=C0413

class SearchTestCase(TestCase):

  def test_search_all(self):
    no_result_title = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(no_result_title, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['users']))
    self.assertEquals(0, len(no_result_data['data']['episodes']))
    self.assertEquals(0, len(no_result_data['data']['series']))

    # Full query
    some_result_title = 'te'
    search_results = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(some_result_title, 0, 1000))
    some_result_data = json.loads(search_results.data)
    self.assertEquals(3, len(some_result_data['data']['users']))
    self.assertFalse(some_result_data['data']['users'][0]['is_following'])
    self.assertEquals(1000, len(some_result_data['data']['episodes']))
    self.assertEquals(125, len(some_result_data['data']['series']))

    some_result_title = 'de'
    search_results = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(some_result_title, 0, 1000))
    some_result_data = json.loads(search_results.data)
    self.assertEquals(3, len(some_result_data['data']['users']))
    self.assertFalse(some_result_data['data']['users'][0]['is_following'])
    self.assertEquals(1000, len(some_result_data['data']['episodes']))
    self.assertEquals(84, len(some_result_data['data']['series']))

    # Partially Empty query
    two_empty_result_title = 'tat'
    te_result = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(two_empty_result_title, 0, 1000))
    te_result = json.loads(te_result.data)
    self.assertEquals(0, len(te_result['data']['users']))
    self.assertEquals(1000, len(te_result['data']['episodes']))
    self.assertEquals(18, len(te_result['data']['series']))

    # offset
    some_result_title = 'te'
    offset_results = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(some_result_title, 1, 2))
    normal_results = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(some_result_title, 0, 1000))
    offset_result_data = json.loads(offset_results.data)
    normal_result_data = json.loads(normal_results.data)
    self.assertEquals(2, len(offset_result_data['data']['users']))
    self.assertEquals(2, len(offset_result_data['data']['episodes']))
    self.assertEquals(2, len(offset_result_data['data']['series']))
    self.assertEquals(normal_result_data['data']['users'][1]['username'], \
        offset_result_data['data']['users'][0]['username'])
    self.assertEquals(normal_result_data['data']['episodes'][1]['title'], \
    offset_result_data['data']['episodes'][0]['title'])
    self.assertEquals(normal_result_data['data']['series'][1]['title'], \
        offset_result_data['data']['series'][0]['title'])

    # Limit
    some_result_title = 'te'
    search_results = self.app.get('api/v1/search/all/{}/?offset={}&max={}'\
        .format(some_result_title, 0, 3))
    limited_result_data = json.loads(search_results.data)
    self.assertEquals(3, len(limited_result_data['data']['users']))
    self.assertEquals(3, len(limited_result_data['data']['episodes']))
    self.assertEquals(3, len(limited_result_data['data']['series']))

  def test_search_episode_returns_booleans(self):
    title = 'Junk'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(title, 0, 1000))
    result_data = json.loads(search_results.data)
    self.assertTrue(len(result_data['data']['episodes']) > 0)
    self.assertIsNotNone(result_data['data']['episodes'][0]['is_bookmarked'])
    self.assertIsNotNone(result_data['data']['episodes'][0]['is_recommended'])

  def test_search_episode(self):
    no_result_title = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(no_result_title, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['episodes']))

    many_result_title = 'Happy'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(many_result_title, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(260, len(many_result_data['data']['episodes']))

    # Test limit
    ten_result_title = 'newer'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(ten_result_title, 0, 4))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals(4, len(ten_result_data['data']['episodes']))

    # Test offset
    offset_result_title = 'big d'
    normal_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
        .format(offset_result_title, 0, 10))
    offset_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
        .format(offset_result_title, 2, 10))
    offset_results_data = json.loads(offset_results.data)
    normal_result_data = json.loads(normal_results.data)
    self.assertEquals(offset_results_data['data']['episodes'][0]['title'], \
        normal_result_data['data']['episodes'][2]['title'])

  def test_search_series_returns_booleans(self):
    title = 'Jud'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(title, 0, 1000))
    result_data = json.loads(search_results.data)
    self.assertEquals(2, len(result_data['data']['series']))
    self.assertIsNotNone(result_data['data']['series'][0]['is_subscribed'])

  def test_search_series(self):
    no_result_title = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(no_result_title, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['series']))

    many_result_title = 'Jud'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(many_result_title, 0, 1000))
    many_result_title = json.loads(search_results.data)
    self.assertEquals(2, len(many_result_title['data']['series']))

    # Test limit
    ten_result_title = 'a'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(ten_result_title, 0, 4))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals(4, len(ten_result_data['data']['series']))

    # Test offset
    ten_result_title = 'Cl'
    offset_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
        .format(ten_result_title, 2, 10))
    normal_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
        .format(ten_result_title, 0, 10))
    normal_result_data = json.loads(normal_results.data)
    offset_result_data = json.loads(offset_results.data)
    self.assertEquals(offset_result_data['data']['series'][0]['title'], \
        normal_result_data['data']['series'][2]['title'])

  def test_search_user(self):
    no_result_username = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(no_result_username, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['users']))

    one_result_username = 'temp-google-default_google_id2'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(one_result_username, 0, 1000))
    one_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(one_result_data['data']['users']))
    self.assertFalse(one_result_data['data']['users'][0]['is_following'])

    many_result_username = 'temp-google-default_google_id'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(many_result_username, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(3, len(many_result_data['data']['users']))

    first_name = 'default_first_name1'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(first_name, 0, 1000))
    single_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(single_result_data['data']['users']))

    first_name = 'default_first_name'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(first_name, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(3, len(many_result_data['data']['users']))

    last_name = 'default_last_name1'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(last_name, 0, 1000))
    single_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(single_result_data['data']['users']))

    last_name = 'default_last_name'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(last_name, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(3, len(many_result_data['data']['users']))

    # Test limit
    two_result_username = 'temp-google-default_google_id'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(two_result_username, 0, 1))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(ten_result_data['data']['users']))

    # Test offset
    two_result_username = 'temp-google-default_google_id'
    normal_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
        .format(two_result_username, 0, 10))
    offset_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
        .format(two_result_username, 1, 10))
    normal_result_data = json.loads(normal_results.data)
    offset_result_data = json.loads(offset_results.data)
    self.assertEquals(offset_result_data['data']['users'][0]['username'], \
        normal_result_data['data']['users'][1]['username'])
