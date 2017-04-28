import requests as re
import pprint
import time
import sys
import os

pp = pprint.PrettyPrinter()

BASE_URL = 'http://localhost:8080/api/v1'
EPISODES = ['947194144:1459321200', '1074070278:1354478400', '79497721:1276558260', '261086208:1211130802']
SERIES   = [int(e.split(':')[0]) for e in EPISODES]

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
  DELETE endpoint wrapper
  """
  return re.delete(BASE_URL + endpoint, headers=headers)

def _head(token):
  return { 'Authorization' : 'Bearer {}'.format(token) }

# FLAG - Sign In

def sign_in(token):
  """
  Sign in as a user
  """
  result = _post('/users/google_sign_in?id_token={}'.format(token))
  return result.json()

def grab_token(token):
  """
  Grab session token
  """
  return sign_in(token)['data']['user']['session']['sessionToken']

# FLAG - User

def change_username(token, username):
  """
  Change a user's username
  """
  result = _post('/users/change_username?username={}'.format(username), _head(token))
  return result.json()

# FLAG - Search

def query_all(token, query):
  """
  Query all
  """
  result = _get('/search/all/{}?offset=0&max=10'.format(query), _head(token))
  return result.json()

def query_users(token, query):
  """
  Query users
  """
  result = _get('/search/users/{}?offset=0&max=10'.format(query), _head(token))
  return result.json()

def query_series(token, query):
  """
  Query series
  """
  result = _get('/search/series/{}?offset=0&max=10'.format(query), _head(token))
  return result.json()

def query_episodes(token, query):
  """
  Query episodes
  """
  result = _get('/search/episodes/{}?offset=0&max=10'.format(query), _head(token))
  return result.json()['data']['episodes']

# FLAG - Bookmarks

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

# FLAG - Recommendations

def create_recommendation(token, episode_id):
  """
  Create a recommendation for episode represented by `episode_id`
  """
  result = _post('/recommendations/{}'.format(episode_id), _head(token))
  return result.json()

def get_recommendations(token, episode_id):
  """
  Get recommendations of a episode represented by `episode_id`
  """
  result = _get('/recommendations/{}?offset=0&max=10'.format(episode_id), _head(token))
  return result.json()

def get_user_recommendations(token, user_id):
  """
  Get recommendations of a user represented by `user_id`
  """
  result = _get('/recommendations/users/{}'.format(user_id), _head(token))
  return result.json()

def delete_recommendation(token, episode_id):
  """
  Delete a recommendation for episode represented by `episode_id`
  """
  result = _delete('/recommendations/{}'.format(episode_id), _head(token))
  return result.json()

# FLAG - Subscriptions

def create_subscription(token, series_id):
  """
  Create a subscription for a series represented by `series_id`
  """
  result = _post('/subscriptions/{}'.format(series_id), _head(token))
  return result.json()

def delete_subscription(token, series_id):
  """
  Delete a subscription for a series represented by `series_id`
  """
  result = _delete('/subscriptions/{}'.format(series_id), _head(token))
  return result.json()

def get_subscriptions(token, series_id):
  """
  Get subscriptions for a series represented by `series_id`
  """
  result = _get('/subscriptions/{}?offset=0&max=10'.format(series_id), _head(token))
  return result.json()

def get_user_subscriptions(token, user_id):
  """
  Get subscriptions of a user represented by `user_id`
  """
  result = _get('/subscriptions/users/{}'.format(user_id), _head(token))
  return result.json()

# FLAG - Integration Tests

def create_get_delete_bookmarks(google_token):
  """
  Full life-cycle of a bookmark
  """
  # Token
  token = grab_token(google_token)

  # Create bookmarks
  for e_id in EPISODES:
    pp.pprint(create_bookmark(token, e_id))

  time.sleep(5)

  # Print bookmarks
  pp.pprint(get_bookmarks(token))

  # Delete bookmarks
  for e_id in EPISODES:
    pp.pprint(delete_bookmark(token, e_id))

def create_get_get_by_user_delete_recommendations(google_token):
  """
  Full life-cycle of a recommendation
  """
  # Grab user creds
  user    = sign_in(google_token)['data']['user']
  user_id = user['id']
  token   = user['session']['sessionToken']

  # Create recommendations
  for e_id in EPISODES:
    pp.pprint(create_recommendation(token, e_id))

  time.sleep(5)

  # Print user's recommendations
  print user['firstName'] + '\'s recommendations'
  pp.pprint(get_user_recommendations(token, user_id))

  # Print each episodes' recommendations
  for e_id in EPISODES:
    print e_id + '\'s recommendations'
    pp.pprint(get_recommendations(token, e_id))

  # Delete recommendations
  for e_id in EPISODES:
    pp.pprint(delete_recommendation(token, e_id))

def create_get_get_by_user_delete_subscriptions(google_token):
  """
  Full life-cycle of a subscription
  """
  # Grab user creds
  user = sign_in(google_token)['data']['user']
  user_id = user['id']
  token = user['session']['sessionToken']

  # Create subscriptions
  for s_id in SERIES:
    pp.pprint(create_subscription(token, s_id))

  time.sleep(5)

  # Print user's subscriptions
  print user['firstName'] + '\'s subscriptions'
  pp.pprint(get_user_subscriptions(token, user_id))

  # Print each series' subscriptions
  for s_id in SERIES:
    print str(s_id) + '\'s subscriptions'
    pp.pprint(get_subscriptions(token, s_id))

  # Delete subscriptions
  for s_id in SERIES:
    pp.pprint(delete_subscription(token, s_id))

if __name__ == '__main__':
  create_get_get_by_user_delete_recommendations(sys.argv[1])
  create_get_delete_bookmarks(sys.argv[1])
