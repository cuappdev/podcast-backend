import sys
from datetime import datetime
from flask import json
from tests.test_case import *
from app.pcasts.elasticsearch import populate, interface
from app.pcasts.dao import users_dao, series_dao, episodes_dao

class ElasticsearchTestCase(TestCase):

  def setUp(self):
    pass

  def test_populate(self):
    if os.environ['ELASTICSEARCH_ENABLED'] == 'True':
      populate.populate()

  def test_query(self):
    if os.environ['ELASTICSEARCH_ENABLED'] == 'True':
      user_id = users_dao.\
          get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

      series_results = interface.search_series('The', 0, 10)
      self.assertEquals(10, len(series_results))
      for series_id in series_results:
        series_title = series_dao.get_series(series_id, user_id).title
        self.assertNotEqual(series_title.lower().find('the'), -1)

      series_results_offset = interface.search_series('The', 10, 10)
      self.assertEquals(10, len(series_results))
      for series_id in series_results_offset:
        series_title = series_dao.get_series(series_id, user_id).title
        self.assertNotEqual(series_title.lower().find('the'), -1)
        self.assertTrue(series_id not in series_results)

      episode_results = interface.search_episodes('The', 0, 10)
      self.assertEquals(10, len(episode_results))
      for episode_id in episode_results:
        episode_title = episodes_dao.get_episode(episode_id, user_id).title
        self.assertNotEqual(episode_title.lower().find('the'), -1)

      episode_results_offset = interface.search_episodes('The', 10, 10)
      self.assertEquals(10, len(episode_results))
      for episode_id in episode_results_offset:
        episode_title = episodes_dao.get_episode(episode_id, user_id).title
        self.assertTrue(episode_title.lower().find('the') != -1)
        self.assertTrue(episode_id not in episode_results)
