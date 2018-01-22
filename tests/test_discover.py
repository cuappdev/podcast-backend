import sys
from flask import json
from tests.test_case import *
from app import constants # pylint: disable=C0413

class DiscoverTestCase(TestCase):

  def test_series_for_topic(self):
    # This test is dependent on podcast-ml being alive. The only functionality
    # really tested is the dao, which can't be mocked, so I don't know if this
    # test is actually useful.
    response = self.app.get('api/v1/discover/series/topic/1309/')
    series = json.loads(response.data)['data']['series']
    self.assertEquals(len(series), 10)
