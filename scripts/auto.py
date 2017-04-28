import requests as re
import sys
import os

BASE_URL = 'http://localhost:8080/api/v1'

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
  headers = { 'Authorization' : 'Bearer {}'.format(token) }
  result = _get('/search/episodes/{}?offset=0&max=10'.format(query), headers)
  return result.json()['data']['episodes']

if __name__ == '__main__':
  # Grab an episode
  token = grab_token(sys.argv[1])
  eps = query_episodes(token, 'Pl')
  ep_id = eps[0]['id']



  
