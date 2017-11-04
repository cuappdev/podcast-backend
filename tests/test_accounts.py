from flask import json
from tests import api_utils
from tests.test_case import *
from app import constants, config
from app.pcasts.dao import users_dao

class AccountsTestCase(TestCase):

  def setUp(self):
    User.query.delete()
    super(AccountsTestCase, self).setUp()
    db_session_commit()

  def test_merge_accounts(self):
    a_access_token = api_utils.get_facebook_app_access_token()
    u_access_token = api_utils.create_facebook_user(a_access_token, 'User One')

    # Add an account for an invalid platform:
    response = self.app.post('api/v1/users/merge/?access_token={}&platform={}'\
        .format(u_access_token, "fauxbook"))
    response = json.loads(response.data)
    self.assertFalse(response['success'])

    # Add an account for facebook with existing google account
    response = self.app.post('api/v1/users/merge/?access_token={}&platform={}'\
        .format(u_access_token, "facebook"))
    response_data = json.loads(response.data)["data"]
    self.assertEquals(config.NUM_TEST_USERS, users_dao.get_number_users())
    self.assertTrue(response_data['user']['facebook_id'] != "null")

  def tearDown(self):
    super(AccountsTestCase, self).tearDown()
    User.query.delete()
    db_session_commit()
