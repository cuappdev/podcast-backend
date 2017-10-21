from flask import json
from tests.test_case import *
from app.pcasts.dao import series_dao

class iTunesTestCase(TestCase):

  def test_search_itunes(self):
    response = self.app.post('api/v1/search/itunes/programming/')
    data = json.loads(response.data)
    print data
    # There should definitely be non-zero results
    series = data.get('data', dict()).get('series', [])
    self.assertTrue(len(series) > 0)
    # Clean up
    new_series_ids = data.get('data').get('new_series_ids')
    for new_series_id in new_series_ids:
      series_dao.remove_series(new_series_id)
