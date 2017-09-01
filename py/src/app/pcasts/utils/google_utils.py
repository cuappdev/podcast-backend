import requests

def get_me(access_token):
  base_uri = \
      'https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token='
  uri = '{}{}'.format(base_uri, access_token)
  return requests.get(uri).json()
