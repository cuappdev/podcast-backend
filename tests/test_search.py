import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao, users_dao, series_dao
from app import constants # pylint: disable=C0413

class SearchTestCase(TestCase):

  def setUp(self):
    super(SearchTestCase, self).setUp()

  def test_search_episode(self):
    no_result_title = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(no_result_title, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['episodes']))

    one_result_title = 'Junk'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(one_result_title, 0, 1000))
    one_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(one_result_data['data']['episodes']))

    many_result_title = 'Happy'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(many_result_title, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(102, len(many_result_data['data']['episodes']))

    ##Test limit
    ten_result_title = 'newer'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(ten_result_title, 0, 4))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals(4, len(ten_result_data['data']['episodes']))

    ##Test offset
    ten_result_title = 'big d'
    search_results = self.app.get('api/v1/search/episodes/{}/?offset={}&max={}'\
         .format(ten_result_title, 2, 10))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals("Big Dare", \
        ten_result_data['data']['episodes'][0]['title'])

  def test_search_series(self):
    no_result_title = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(no_result_title, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['series']))

    one_result_title = 'Jud'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(one_result_title, 0, 1000))
    one_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(one_result_data['data']['series']))

    many_result_title = 'a'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(many_result_title, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(32, len(many_result_data['data']['series']))

    ##Test limit
    ten_result_title = 'a'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(ten_result_title, 0, 4))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals(4, len(ten_result_data['data']['series']))

    ##Test offset
    ten_result_title = 'Cl'
    search_results = self.app.get('api/v1/search/series/{}/?offset={}&max={}'\
         .format(ten_result_title, 2, 10))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals("Clever", \
        ten_result_data['data']['series'][0]['title'])


  def test_search_user(self):
    no_result_username = 'ABCDEFGHIJKL'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(no_result_username, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['users']))

    one_result_username = 'temp-default_google_id1'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(one_result_username, 0, 1000))
    one_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(one_result_data['data']['users']))

    many_result_username = 'temp-default_google_id'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(many_result_username, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(2, len(many_result_data['data']['users']))

    ##Test limit
    two_result_username = 'temp-default_google_id'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(two_result_username, 0, 1))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(ten_result_data['data']['users']))

    ##Test offset
    two_result_username = 'temp-default_google_id'
    search_results = self.app.get('api/v1/search/users/{}/?offset={}&max={}'\
         .format(two_result_username, 1, 10))
    ten_result_data = json.loads(search_results.data)
    self.assertEquals("temp-default_google_id2", \
        ten_result_data['data']['users'][0]['username'])
