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

  def test_get_user_by_id(self):
    user1 = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    user2 = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID2).first()

    response = self.app.get('api/v1/users/{}/'.format(user1.id))
    data = json.loads(response.data)
    self.assertEquals(data['data']['user']['id'], user1.id)
    self.assertEquals(
        data['data']['user']['google_id'],
        constants.TEST_USER_GOOGLE_ID1
    )

    response = self.app.get('api/v1/users/{}/'.format(user2.id))
    data = json.loads(response.data)
    self.assertEquals(data['data']['user']['id'], user2.id)
    self.assertEquals(
        data['data']['user']['google_id'],
        constants.TEST_USER_GOOGLE_ID2
    )

  def test_change_user_name(self):

    old_name = 'temp-default_google_id1'
    new_name = 'bob'
    response = self.app.post('api/v1/users/{}/?username={}' \
        .format(old_name, new_name))
    response_data = json.loads(response.data)['data']
    self.assertEquals("bob", response_data['user']['username'])

    search_old = users_dao.search_users(old_name, 0, 0)
    self.assertEquals(0, len(search_old))
    self.assertEquals(2, users_dao.get_number_users())

    #Test non existing username old name
    db_before_query = users_dao.get_all_users()
    old_name = 'non_existing_username'
    new_name = 'bob'
    response = self.app.post('api/v1/users/{}/?username={}' \
        .format(old_name, new_name))
    response_data = json.loads(response.data)['data']
    error_string = "The current user name is Invalid"
    self.assertEquals(error_string, response_data['errors'][0])

    db_after_query = users_dao.get_all_users()
    self.assertEquals(2, users_dao.get_number_users())
    self.assertEquals(len(db_before_query), len(db_after_query))
    self.assertEquals(db_before_query, db_after_query)

    #Test username already in use
    db_before_query = users_dao.get_all_users()
    old_name = 'bob'
    new_name = 'temp-default_google_id2'
    response = self.app.post('api/v1/users/{}/?username={}' \
        .format(old_name, new_name))
    response_data = json.loads(response.data)['data']
    error_string = "Username temp-default_google_id2 already in use"
    self.assertEquals(error_string, response_data['errors'][0])

    db_after_query = users_dao.get_all_users()
    self.assertEquals(2, users_dao.get_number_users())
    self.assertEquals(len(db_before_query), len(db_after_query))
    self.assertEquals(db_before_query, db_after_query)

    #Test empty new_name
    db_before_query = users_dao.get_all_users()
    old_name = 'bob'
    new_name = ''
    response = self.app.post('api/v1/users/{}/?username={}' \
        .format(old_name, new_name))
    response_data = json.loads(response.data)['data']
    error_string = "The new username  is invalid"
    self.assertEquals(error_string, response_data['errors'][0])

    db_after_query = users_dao.get_all_users()
    self.assertEquals(2, users_dao.get_number_users())
    self.assertEquals(len(db_before_query), len(db_after_query))
    self.assertEquals(db_before_query, db_after_query)
