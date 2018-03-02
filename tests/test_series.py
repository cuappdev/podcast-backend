import sys
import json
from tests.test_case import *
from app.pcasts.dao import series_dao, subscriptions_dao
from app import constants

class SeriesTestCase(TestCase):

  def setUp(self):
    super(SeriesTestCase, self).setUp()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  def tearDown(self):
    super(SeriesTestCase, self).tearDown()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  def test_get_series_by_id(self):
    series_id = '1211520413'
    response = self.user1.get('api/v1/series/{}/'.format(series_id))
    result = json.loads(response.data)['data']['series']
    expected_series = series_dao.get_series(series_id, self.user1.uid)
    self.assertEqual(result['id'], expected_series.id)

  def test_last_updated(self):
    # Single
    series_id = '1211520413'
    response = self.user1.get('api/v1/series/{}/'.format(series_id))
    result = json.loads(response.data)['data']['series']
    self.assertEqual(result['last_updated'], u'2017-03-06T19:09:31+00:00')

    # Multiple
    search_endpoint = \
      'api/v1/search/series/Laugh%20with%20Kirill%20&%20Kevin/?offset=0&max=1'
    response = self.user1.get(search_endpoint)
    result = json.loads(response.data)['data']['series']
    self.assertEqual(1, len(result))
    single_result = result[0]
    self.assertEqual(single_result['last_updated'], u'2017-03-06T19:09:31+00:00')

  def test_series_refresh(self):
    # Null out topic ids
    series = Series.query.filter().all()
    for show in series:
      show.topic_id = None
      show.subtopic_id = None
    db_utils.db_session_commit()
    series = Series.query.filter().all()
    for show in series:
      self.assertTrue(show.topic_id is None)
      self.assertTrue(show.subtopic_id is None)
    series_dao.refresh_series()
    # Check that topic ids are back
    series = Series.query.filter().all()
    for show in series:
      self.assertTrue(show.topic_id is not None)
      self.assertTrue(show.subtopic_id is not None)
