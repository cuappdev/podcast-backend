import os
import requests
from . import *

def get_series_for_topic(topic_id, user_id):
  response = \
    requests.get('{}/api/v1/series/topic/{}'
                 .format(os.environ['PODCAST_ML_URL'], topic_id),
                 headers={'api_key': os.environ['PODCAST_ML_API_KEY']}).json()
  return series_dao.get_multiple_series(response['data']['series_ids'], user_id)

def get_episodes_for_topic(topic_id, user_id):
  response = \
    requests.get('{}/api/v1/episodes/topic/{}'
                 .format(os.environ['PODCAST_ML_URL'], topic_id),
                 headers={'api_key': os.environ['PODCAST_ML_API_KEY']}).json()

  return episodes_dao.get_episodes(response['data']['episode_ids'], user_id)

def get_series_for_user(user_id):
  response = \
    requests.get('{}/api/v1/series/user/{}'
                 .format(os.environ['PODCAST_ML_URL'], user_id),
                 headers={'api_key': os.environ['PODCAST_ML_API_KEY']}).json()

  return series_dao.get_multiple_series(response['data']['series_ids'], user_id)

def get_episodes_for_user(user_id):
  response = \
    requests.get('{}/api/v1/episodes/user/{}'
                 .format(os.environ['PODCAST_ML_URL'], user_id),
                 headers={'api_key': os.environ['PODCAST_ML_API_KEY']}).json()

  return episodes_dao.get_episodes(response['data']['episode_ids'], user_id)
