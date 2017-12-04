import json
import config
import requests
from tests.api_utils import *
from app import constants
from app.pcasts.models._all import *
from app.pcasts.utils.db_utils import *
from app.pcasts.dao.sessions_dao import *
from app.pcasts.dao.users_dao import *


class TestUser(object):

  goog_user_count = 0
  default_users = []

  # Creates fb_user(graph api) or binds itself to test goog account
  def __init__(self, **kwargs):
    self.tokens = {} #Maps a platform to it's user_access token
    self.app_tokens = {}
    self.name = kwargs.get('name', '')
    self.test_client = kwargs.get('test_client')
    self.uid = kwargs.get('uid', None)
    self.session_token = kwargs.get('session_token', None)
    self.platform = kwargs.get('platform', constants.GOOGLE)
    app_access_token = kwargs.get('app_access_token', '')
    if self.platform == constants.GOOGLE:
      self.goog_user_count = TestUser.goog_user_count
      TestUser.goog_user_count += 1
    else:
      self.app_tokens[constants.FACEBOOK] = app_access_token
      access_token = create_facebook_user(app_access_token, self.name)
      self.tokens[constants.FACEBOOK] = access_token

    if kwargs.get('login', True):
      self.login()

  def login(self):
    if self.platform == constants.GOOGLE:
      commit_model(TestUser.default_users[self.goog_user_count])
      self.user = get_user_by_google_id \
          (TestUser.default_users[self.goog_user_count].google_id)
      self.uid = self.user.id
      self.name = '{} {}'.format(self.user.first_name, self.user.last_name)
      session = get_or_create_session_and_activate(self.uid)
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

  def post(self, url, data=None):
    header = {"Authorization": "Bearer {}".format(self.session_token)}
    if data is None:
      response = self.test_client.post(url, headers=header)
    else:
      response = self.test_client.post(url, headers=header, data=data)
    return response

  def get(self, url, data=None):
    header = {"Authorization": "Bearer {}".format(self.session_token)}
    if data is None:
      response = self.test_client.get(url, headers=header)
    else:
      response = self.test_client.get(url, headers=header, data=data)
    return response

  def delete(self, url, data=None):
    header = {"Authorization": "Bearer {}".format(self.session_token)}
    if data is None:
      response = self.test_client.delete(url, headers=header)
    else:
      response = self.test_client.delete(url, headers=header, data=data)
    return response

def initTestUser():
  TestUser.goog_user_count = 0
  TestUser.default_users = [
      User(
          google_id=constants.TEST_USER_GOOGLE_ID1,
          email='default_email1',
          first_name='default_first_name1',
          last_name='default_last_name1',
          image_url='',
          followers_count=0,
          followings_count=0,
      ),
      User(
          google_id=constants.TEST_USER_GOOGLE_ID2,
          email='default_email2',
          first_name='default_first_name2',
          last_name='default_last_name2',
          image_url='',
          followers_count=0,
          followings_count=0,
      ),
      User(
          google_id=constants.TEST_USER_GOOGLE_ID3,
          email='default_email3',
          first_name='default_first_name3',
          last_name='default_last_name3',
          image_url='',
          followers_count=0,
          followings_count=0,
      )
  ]
