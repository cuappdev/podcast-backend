import sys
from flask import json
from tests.test_case import *
from app import constants # pylint: disable=C0413

class SeriesForTopicTestCase(TestCase):

  def test_series_for_topic(self):
    response = self.app.get('api/v1/series/topic/1309/')
    print response
    series = json.loads(response.data)['data']['series']
    self.assertEquals(len(series), 10)
    print series
