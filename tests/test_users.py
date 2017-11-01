from flask import json
from tests.test_case import *
from app import constants
from app.pcasts.dao import users_dao
from app.pcasts.controllers import get_user_by_id_controller

class UsersTestCase(TestCase):

  def setUp(self):
    User.query.delete()
    super(UsersTestCase, self).setUp()
    db_session_commit()

  def tearDown(self):
    User.query.delete()
    super(UsersTestCase, self).tearDown()
    db_session_commit()

  def test_change_user_name(self):
    # Test with valid parameters
    old_name = 'temp-default_google_id1'
    new_name = 'bob'
    response = self.app.post('api/v1/users/change_username/?username={}' \
        .format(new_name))
    response_data = json.loads(response.data)['data']
    self.assertEquals("bob", response_data['user']['username'])

    search_old = users_dao.search_users(old_name, 0, 2)
    self.assertEquals(0, len(search_old))
    self.assertEquals(3, users_dao.get_number_users())

    # Test empty new_name
    db_before_query = users_dao.get_all_users()
    old_name = 'bob'
    new_name = ''

    response = self.app.post('api/v1/users/change_username/?username={}' \
        .format(new_name))
    response_data = json.loads(response.data)['data']
    error_string = "Username length must be greater than 0"
    self.assertEquals(error_string, response_data['errors'][0])

    db_after_query = users_dao.get_all_users()
    self.assertEquals(3, users_dao.get_number_users())
    self.assertEquals(len(db_before_query), len(db_after_query))
    self.assertEquals(db_before_query, db_after_query)

    # Test changing to username already in use
    db_before_query = users_dao.get_all_users()
    # Want to use users after the rollback due to failure
    db_session_expunge_all()
    existing_user = User.query.\
      filter(User.google_id == constants.TEST_USER_GOOGLE_ID2).\
      first()
    new_name = existing_user.username
    response = self.app.post('api/v1/users/change_username/?username={}' \
        .format(new_name))
    response_data = json.loads(response.data)['data']
    error_string = "Failure to complete DB transaction"
    self.assertEquals(error_string, response_data['errors'][0])
    db_after_query = users_dao.get_all_users()

    self.assertEquals(3, users_dao.get_number_users())
    self.assertEquals(len(db_before_query), len(db_after_query))
    self.assertEquals(db_before_query, db_after_query)
