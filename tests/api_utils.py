import os
import sys
import json
import config
import requests
from app import app # pylint: disable=C0413
from app import constants # pylint: disable=C0413

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
        config.FACEBOOK_API_PERMISSIONS, access_token)
    response = requests.get(uri).json()
    return response['access_token']

def create_google_user():
  pass
