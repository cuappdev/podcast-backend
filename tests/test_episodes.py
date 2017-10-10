import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao, series_dao, users_dao
from app import constants # pylint: disable=C0413

class EpisodeTestCase(TestCase):

  def setUp(self):
    super(EpisodeTestCase, self).setUp()

  def test_get_episode_by_series(self):
    test_user_id = users_dao.\
      get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    no_result_id = '123'
    search_results = self.app.get(\
        'api/v1/podcasts/episodes/by_series/{}/?offset={}&max={}'\
        .format(no_result_id, 0, 1000))
    no_result_data = json.loads(search_results.data)
    self.assertEquals(0, len(no_result_data['data']['episodes']))

    one_result_title = '258109223'
    one_result_series = series_dao.get_series(one_result_title, test_user_id)
    search_results = self.app.get('api/v1/podcasts/episodes/by_series/\
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

    many_result_title = '78775671'
    search_results = self.app.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 0, 1000))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(10, len(many_result_data['data']['episodes']))
    ##Enforce ordering and id
    maxDate = many_result_data['data']['episodes'][0]['pub_date']
    for episode in many_result_data['data']['episodes']:
      self.assertEquals(episode['series']['id'], int(many_result_title))
      self.assertTrue(episode['pub_date'] <= maxDate)
      maxDate = episode['pub_date']

    ##Test limit
    many_result_title = '78775671'
    search_results = self.app.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 0, 4))
    many_result_data = json.loads(search_results.data)
    self.assertEquals(4, len(many_result_data['data']['episodes']))
    ##Enforce ordering and id
    maxDate = many_result_data['data']['episodes'][0]['pub_date']
    for episode in many_result_data['data']['episodes']:
      self.assertEquals(episode['series']['id'], int(many_result_title))
      self.assertTrue(episode['pub_date'] <= maxDate)
      maxDate = episode['pub_date']

    ##Test offset
    many_result_title = '78775671'
    offset_results = self.app.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 2, 10))
    normal_results = self.app.get('api/v1/podcasts/episodes/by_series/\
        {}/?offset={}&max={}'.format(many_result_title, 0, 10))
    normal_result_data = json.loads(normal_results.data)
    offset_result_data = json.loads(offset_results.data)
    self.assertEquals(offset_result_data['data']['episodes'][0]['title'], \
        normal_result_data['data']['episodes'][2]['title'])
    ##Enforce ordering and id
    maxDate = offset_result_data['data']['episodes'][0]['pub_date']
    for episode in offset_result_data['data']['episodes']:
      self.assertEquals(episode['series']['id'], int(many_result_title))
      self.assertTrue(episode['pub_date'] <= maxDate)
      maxDate = episode['pub_date']
