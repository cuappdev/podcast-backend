import os
import requests
from . import *

def request_podcast_ml(url):
  try:
    return requests.get(os.environ['PODCAST_ML_URL'] + url,
                        headers={'api_key': os.environ['PODCAST_ML_API_KEY']}) \
                   .json()
  except requests.exceptions.ConnectionError:
    raise Exception('Could not connect to podcast-ml')

def get_series_for_topic(topic_id, user_id, offset, max_num):
  response = request_podcast_ml('/api/v1/series/topic/{}/?offset={}&max={}'
                                .format(topic_id, offset, max_num))
  return series_dao.get_multiple_series(response['data']['series_ids'], user_id)

def get_episodes_for_topic(topic_id, user_id, offset, max_num):
  response = request_podcast_ml('/api/v1/episodes/topic/{}/?offset={}&max={}'
                                .format(topic_id, offset, max_num))
  return episodes_dao.get_episodes(response['data']['episode_ids'], user_id)

def get_series_for_user(user_id, offset, max_num):
  response = request_podcast_ml('/api/v1/series/user/?offset={}&max={}'
                                .format(offset, max_num))
  return series_dao.get_multiple_series(response['data']['series_ids'], user_id)

def get_episodes_for_user(user_id, offset, max_num):
  response = request_podcast_ml('/api/v1/episodes/user/?offset={}&max={}'
                                .format(offset, max_num))
  return episodes_dao.get_episodes(response['data']['episode_ids'], user_id)
