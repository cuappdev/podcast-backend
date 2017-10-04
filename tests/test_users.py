from flask import json
from tests.test_case import *
from app import constants
from app.pcasts.dao import users_dao
from app.pcasts.controllers import get_user_by_id_controller

class UsersTestCase(TestCase):

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
