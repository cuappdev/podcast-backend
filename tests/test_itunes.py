from flask import json
from tests.test_case import *
from app.pcasts.dao import series_dao

class iTunesTestCase(TestCase):

  def _clean_up(self, data):
    new_series_ids = data.get('data').get('new_series_ids')
    for new_series_id in new_series_ids:
      series_dao.remove_series(new_series_id)

  def _itunes_smoke_test(self, query):
    response = self.app.post('api/v1/search/itunes/{}/'.format(query))
    data = json.loads(response.data)
    # There should definitely be non-zero results
    series = data.get('data', dict()).get('series', [])
    self.assertTrue(len(series) > 0)
    self._clean_up(data)

  def test_search_itunes_1(self):
    self._itunes_smoke_test('programming')

  def test_search_itunes_2(self):
    self._itunes_smoke_test('a%20piece')

  def test_search_itunes_3(self):
    self._itunes_smoke_test('The%20Ben%20and')

  def test_search_itunes_4(self):
    self._itunes_smoke_test('Some')
