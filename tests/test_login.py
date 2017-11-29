import sys
import config
from flask import json
from tests import api_utils
from app.pcasts.dao import users_dao
from tests.test_case import *
from app import constants # pylint: disable=C0413

class LoginTestCase(TestCase):

  def setUp(self):
    super(LoginTestCase, self).setUp()
    Session.query.delete()
    db_session_commit()

  def test_facebook_login(self):
    a_access_token = api_utils.get_facebook_app_access_token()
    u_access_token = api_utils.create_facebook_user(a_access_token, 'User One')
    payload = {
        'access_token': u_access_token
    }
    response = self.app.post('api/v1/users/facebook_sign_in/', \
        data=json.dumps(payload))
    response_data = json.loads(response.data)['data']
    self.assertTrue(response_data['is_new_user'])
    self.assertTrue(response_data['user']['facebook_id'] != "null")
    self.assertEquals(constants.NUM_TEST_USERS + 1, \
        users_dao.get_number_users())

    # Bad login
    bad_token = 'bad token'
    payload = {
        'access_token': bad_token
    }
    response = self.app.post('api/v1/users/facebook_sign_in/',\
        data=json.dumps(payload))
    response = json.loads(response.data)
    self.assertFalse(response['success'])
    self.assertEquals(constants.NUM_TEST_USERS + 1, \
        users_dao.get_number_users())

  def test_google_login(self):
    #Impossible without API key
    pass

  def tearDown(self):
    super(LoginTestCase, self).tearDown()
    Session.query.delete()
    db_session_commit()
