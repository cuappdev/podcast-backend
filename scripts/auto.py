import requests as re
import pprint
import time
import sys
import os

pp = pprint.PrettyPrinter()

BASE_URL = 'http://localhost:8080/api/v1'
EPISODES = ['947194144:1459321200', '1074070278:1354478400', '79497721:1276558260', '261086208:1211130802']

def _get(endpoint, headers={}):
  """
  GET endpoint wrapper
  """
  return re.get(BASE_URL + endpoint, headers=headers)

def _post(endpoint, headers={}):
  """
  POST endpoint wrapper
  """
  return re.post(BASE_URL + endpoint, headers=headers)

def _delete(endpoint, headers={}):
  """
  DELETe endpoint wrapper
  """
  return re.delete(BASE_URL + endpoint, headers=headers)

def _head(token):
  return { 'Authorization' : 'Bearer {}'.format(token) }

def grab_token(token):
  """
  Grab session token from server to make requests
  """
  result = _post('/users/google_sign_in?id_token={}'.format(token))
  return result.json()['data']['user']['session']['sessionToken']

def query_episodes(token, query):
  """
  Query episodes
  """
  result = _get('/search/episodes/{}?offset=0&max=10'.format(query), _head(token))
  return result.json()['data']['episodes']

def create_bookmark(token, episode_id):
  """
  Create a bookmark for episode represented by `episode_id`
  """
  result = _post('/bookmarks/{}'.format(episode_id), _head(token))
  return result.json()

def get_bookmarks(token):
  """
  Get a user's bookmarks
  """
  return _get('/bookmarks', _head(token)).json()

def delete_bookmark(token, episode_id):
  """
  Delete a bookmark for episode represented by `episode_id`
  """
  result = _delete('/bookmarks/{}'.format(episode_id), _head(token))
  return result.json()

if __name__ == '__main__':

  # Token
  token = grab_token(sys.argv[1])

  # Create bookmarks
  for lol in EPISODES:
    create_bookmark(token, lol)

  time.sleep(5)

  # Print bookmarks
  pp.pprint(get_bookmarks(token))

  # Delete bookmarks
  for lol in EPISODES:
    delete_bookmark(token, lol)
