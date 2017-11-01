from flask import json
from tests.test_case import *
from app import constants
from app.pcasts.dao import users_dao

# Custom tests to be run by hand and not nosetests
class AccountsTestCase(TestCase):

  def setUp(self):
    self.runtests = False
    User.query.delete()
    super(AccountsTestCase, self).setUp()
    db_session_commit()
    ##### Your tokens here #####
    self.google_token = None
    self.facebook_token = None
    self.bad_token = "bad_token"

  def tearDown(self):
    super(AccountsTestCase, self).tearDown()
    User.query.delete()
    db_session_commit()

  def test_login_google(self):
    if not self.runtests:
      return

  def test_login_facebook(self):
    if not self.runtests:
      return

    ##Proper login
    response = self.app.post('api/v1/users/facebook_sign_in/?access_token={}' \
        .format(self.facebook_token))
    response_data = json.loads(response.data)['data']
    self.assertEquals(response_data['is_new_user'], True)
    self.assertTrue(response_data['user']['facebook_id'] != "null")
    self.assertEquals(3, users_dao.get_number_users())

    #Bad login
    response = self.app.post('api/v1/users/facebook_sign_in/?access_token={}' \
        .format(self.bad_token))
    response = json.loads(response.data)
    self.assertFalse(response['success'])
    self.assertEquals(3, users_dao.get_number_users())


  def test_merge_accounts(self):
    if not self.runtests:
      return
    ##Add an account for an invalid platform:
    response = self.app.post('api/v1/users/merge/?access_token={}&platform={}'\
        .format(self.facebook_token, "fauxbook"))
    response = json.loads(response.data)
    self.assertFalse(response['success'])

    ##Add an account for facebook with existing google account
    response = self.app.post('api/v1/users/merge/?access_token={}&platform={}'\
        .format(self.facebook_token, "facebook"))
    response_data = json.loads(response.data)["data"]
    self.assertEquals(2, users_dao.get_number_users())
    self.assertTrue(response_data['user']['facebook_id'] != "null")
