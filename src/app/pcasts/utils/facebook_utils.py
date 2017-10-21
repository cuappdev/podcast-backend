import requests

def get_me(access_token):
  base_uri = 'https://graph.facebook.com/me?access_token='
  uri = '{}{}'.format(base_uri, access_token)
  return requests.get(uri).json()
