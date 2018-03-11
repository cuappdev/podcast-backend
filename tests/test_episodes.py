import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao, series_dao, users_dao
from app import constants

class EpisodeTestCase(TestCase):

  def setUp(self):
    super(EpisodeTestCase, self).setUp()
    Recommendation.query.delete()
    Bookmark.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    db_session_commit()

  def tearDown(self):
    super(EpisodeTestCase, self).tearDown()
    Recommendation.query.delete()
    Bookmark.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    db_session_commit()

  def test_discover_episodes(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id
    self.user1.post('api/v1/recommendations/{}/'.format(episode_id1))
    response = \
      self.user1.get('api/v1/discover/episodes/?offset={}&max={}'.format(0, 2))
    episode_results = json.loads(response.data)['data']['episodes']
    self.assertEquals(episode_id1, episode_results[0]['id'])

  def test_get_episode_by_series(self):
    no_result_id = '123'
    search_results = self.user1.get(\
        'api/v1/podcasts/episodes/by_series/{}/?offset={}&max={}'\
        .format(no_result_id, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['episodes']))

    one_result_title = '258109223'
    one_result_series = series_dao.get_series(one_result_title, self.user1.uid)
    search_results = self.user1.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(one_result_title, 0, 1000))
    one_result_data = json.loads(search_results.data)
    self.assertEquals(1, len(one_result_data['data']['episodes']))

    # Check to see if image url's are embedded
    self.assertEquals(
        one_result_data['data']['episodes'][0]['series']['image_url_lg'],
        one_result_series.image_url_lg
    )
    self.assertEquals(
        one_result_data['data']['episodes'][0]['series']['image_url_sm'],
        one_result_series.image_url_sm
    )

    # Check for title
    self.assertEquals(
        one_result_data['data']['episodes'][0]['series']['title'],
        one_result_series.title
    )

    many_result_title = '78775671'
    search_results = self.user1.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(10, len(many_result_data['data']['episodes']))

    # Enforce ordering and id
    maxDate = many_result_data['data']['episodes'][0]['pub_date']
    for episode in many_result_data['data']['episodes']:
      self.assertEquals(episode['series']['id'], int(many_result_title))
      self.assertTrue(episode['pub_date'] <= maxDate)
      maxDate = episode['pub_date']

    # Test limit
    many_result_title = '78775671'
    search_results = self.user1.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 0, 4))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(4, len(many_result_data['data']['episodes']))

    # Enforce ordering and id
    maxDate = many_result_data['data']['episodes'][0]['pub_date']
    for episode in many_result_data['data']['episodes']:
      self.assertEquals(episode['series']['id'], int(many_result_title))
      self.assertTrue(episode['pub_date'] <= maxDate)
      maxDate = episode['pub_date']

    # Test offset
    many_result_title = '78775671'
    offset_results = self.user1.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 2, 10))
    normal_results = self.user1.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 0, 10))
    normal_result_data = json.loads(normal_results.data)
    offset_result_data = json.loads(offset_results.data)
    self.assertEquals(offset_result_data['data']['episodes'][0]['title'], \
        normal_result_data['data']['episodes'][2]['title'])

    # Enforce ordering and id
    maxDate = offset_result_data['data']['episodes'][0]['pub_date']
    for episode in offset_result_data['data']['episodes']:
      self.assertEquals(episode['series']['id'], int(many_result_title))
      self.assertTrue(episode['pub_date'] <= maxDate)
      maxDate = episode['pub_date']
