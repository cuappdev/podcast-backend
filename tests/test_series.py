import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import series_dao
from app import constants # pylint: disable=C0413

class SeriesTestCase(TestCase):

  def test_get_series_by_id(self):
    series_id = '73329271'
    series = self.app.get('api/v1/series/{}/'.format(series_id))
    result_data = json.loads(series.data)
    self.assertIsNotNone(result_data['data']['series'])
