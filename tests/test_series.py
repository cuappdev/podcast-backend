import sys
import json
from tests.test_case import *
from app.pcasts.dao import series_dao
from app import constants # pylint: disable=C0413

class SeriesTestCase(TestCase):

  def test_get_series_by_id(self):
    user = User.query \
       .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    series_id = '1211520413'
    response = self.app.get('api/v1/series/{}/'.format(series_id))
    result = json.loads(response.data)['data']['series']
    expected_series = series_dao.get_series(series_id, user.id)
    self.assertEqual(result['id'], expected_series.id)
