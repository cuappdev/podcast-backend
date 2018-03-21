import os
import sys
import json
import config
import requests
from app.pcasts.dao import users_dao
from app import app
from app import constants

def get_facebook_app_access_token():
  base_uri = 'https://graph.facebook.com/oauth/access_token?client_id={}' \
      +'&client_secret={}&grant_type=client_credentials'
  uri = base_uri.format(config.FACEBOOK_APP_ID, config.FACEBOOK_APP_SECRET)
  response = requests.get(uri).json()
  return response['access_token']

def create_facebook_user(access_token, username):
  base_uri = 'https://graph.facebook.com/{}/accounts/test-users?' +\
      'installed={}&name={}&permissions={}&method=post&access_token={}'
  uri = base_uri.format(config.FACEBOOK_APP_ID, 'true', username, \
      constants.FACEBOOK_API_PERMISSIONS, access_token)
  response = requests.get(uri).json()
  return response['access_token'], response['id']

def delete_facebook_user(fb_id, access_token):
  base_uri = 'https://graph.facebook.com/{}?method=delete&access_token={}'
  uri = base_uri.format(fb_id, access_token)
  response = requests.delete(uri).json()

def create_google_user():
  raise NotImplementedError

def create_facebook_friendship(fb_user1, fb_user2):
  user1 = users_dao.get_user_by_id(fb_user1.uid, fb_user1.uid)
  user2 = users_dao.get_user_by_id(fb_user2.uid, fb_user2.uid)

  base_uri = 'https://graph.facebook.com/{}/friends/{}?' +\
      'access_token={}'
  uri1 = base_uri.format(user1.facebook_id, user2.facebook_id,\
      fb_user1.tokens[constants.FACEBOOK])
  uri2 = base_uri.format(user2.facebook_id, user1.facebook_id,\
      fb_user2.tokens[constants.FACEBOOK])
  requests.post(uri1)
  requests.post(uri2)
