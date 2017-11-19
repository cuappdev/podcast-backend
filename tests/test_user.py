import json
import requests
import config
from tests.api_utils import *
from app import constants
from app.pcasts.models._all import * # pylint: disable=C0413
from app.pcasts.utils.db_utils import * # pylint: disable=C0413
from app.pcasts.dao.sessions_dao import *
from app.pcasts.dao.users_dao import *


class TestUser():

  goog_user_count = 0
  default_users = []

  # Creates fb_user(graph api) or binds itself to test goog account
  def __init__(self, test_client, name, app_access_token=None, \
    platform=constants.GOOGLE, login=True):
    self.tokens = {} #Maps a platform to it's user_access token
    self.app_tokens = {}
    self.name = name
    self.test_client = test_client
    self.uid = None
    self.session_token = None

    if platform == constants.GOOGLE:
      self.goog_user_count = TestUser.goog_user_count
      user = TestUser.default_users[TestUser.goog_user_count]
      user.first_name = name.split()[0]
      user.last_name = name.split()[1]
      TestUser.goog_user_count += 1
    else:
      self.app_tokens[constants.FACEBOOK] = app_access_token
      access_token = create_facebook_user(app_access_token, name)
      self.tokens[constants.FACEBOOK] = access_token

    if login:
      self.login(platform)

  def login(self, platform):
    if platform == constants.GOOGLE:
      commit_model(TestUser.default_users[self.goog_user_count])
      user = get_user_by_google_id \
          (TestUser.default_users[self.goog_user_count].google_id)
      self.uid = user.id
      session = get_or_create_session_and_activate(user.id)
      self.session_token = session.session_token
    else:
      payload = {
          'access_token': self.tokens[constants.FACEBOOK]
      }
      response = self.test_client.post('api/v1/users/facebook_sign_in/', \
          data=json.dumps(payload))
      response = json.loads(response.data)['data']
      self.uid = response['user']['id']
      self.session_token = response['session']['session_token']

  def post(self, url):
    header = {"Authorization": "Bearer {}".format(self.session_token)}
    response = self.test_client.post(url, headers=header)
    return json.loads(response.data)['data']

  def get(self, url):
    header = {"Authorization": "Bearer {}".format(self.session_token)}
    response = self.test_client.get(url, headers=header)
    return json.loads(response.data)['data']

  def delete(self, url):
    header = {"Authorization": "Bearer {}".format(self.session_token)}
    response = self.test_client.delete(url, headers=header)
    return json.loads(response.data)['data']

def initTestUser():
  TestUser.goog_user_count = 0
  TestUser.default_users = [
      User(
          google_id=constants.TEST_USER_GOOGLE_ID4,
          email='default_email4',
          first_name='default_first_name4',
          last_name='default_last_name4',
          image_url='',
          followers_count=0,
          followings_count=0,
      ),
      User(
          google_id=constants.TEST_USER_GOOGLE_ID5,
          email='default_email5',
          first_name='default_first_name5',
          last_name='default_last_name5',
          image_url='',
          followers_count=0,
          followings_count=0,
      ),
      User(
          google_id=constants.TEST_USER_GOOGLE_ID6,
          email='default_email6',
          first_name='default_first_name6',
          last_name='default_last_name6',
          image_url='',
          followers_count=0,
          followings_count=0,
      )
  ]
