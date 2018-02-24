import os
import requests
from . import *
from app import constants
from app import config
from app.pcasts.utils import topic_utils

def request_podcast_ml(url):
  try:
    return requests.get(os.environ['PODCAST_ML_URL'] + url,
                        headers={'api_key': os.environ['PODCAST_ML_API_KEY']}) \
                   .json()
  except requests.exceptions.ConnectionError:
    raise Exception('Could not connect to podcast-ml')

def get_series_for_topic(topic_id, user_id, offset, max_search):
  if config.ML_ENABLED:
    response = request_podcast_ml('/api/v1/series/topic/{}'.format(topic_id))
    return series_dao.get_multiple_series(response['data']['series_ids'], \
        user_id)
  else:
    topic_id = int(topic_id)
    series = []
    if topic_id in topic_utils.topic_id_offset:
      topic_id = topic_utils.translate_topic_id(topic_id)
      series = Series.query.\
          filter(((Series.topic_id.op('&')(topic_id))) == topic_id).\
          order_by(Series.subscribers_count.desc()).\
          offset(offset).\
          limit(max_search).\
          all()
    if topic_id in topic_utils.subtopic_id_offset:
      subtopic_id = topic_utils.translate_subtopic_id(topic_id)
      series = Series.query.\
          filter((Series.subtopic_id.op('&')(subtopic_id)) == subtopic_id).\
          order_by(Series.subscribers_count.desc()).\
          offset(offset).\
          limit(max_search).\
          all()
    return series


def get_episodes_for_topic(topic_id, user_id, offset, max_search):
  if config.ML_ENABLED:
    response = request_podcast_ml('/api/v1/episodes/topic/{}'.format(topic_id))
    return episodes_dao.get_episodes(response['data']['episode_ids'], user_id)
  else:
    episodes = []
    topic_id = int(topic_id)
    if topic_id in topic_utils.topic_id_offset:
      topic_id = topic_utils.translate_topic_id(topic_id)
      episodes = Episode.query.join(Series). \
          filter((Series.topic_id.op('&')(topic_id)) == topic_id).\
          filter(Episode.series_id == Series.id).\
          order_by(Episode.recommendations_count.desc()).\
          offset(offset).\
          limit(max_search).\
          all()
    if topic_id in topic_utils.subtopic_id_offset:
      subtopic_id = topic_utils.translate_subtopic_id(topic_id)
      episodes = Episode.query.join(Series). \
          filter((Series.subtopic_id.op('&')(subtopic_id)) == subtopic_id).\
          filter(Episode.series_id == Series.id).\
          order_by(Episode.recommendations_count.desc()).\
          offset(offset).\
          limit(max_search).\
          all()
    return episodes

def get_series_for_user(user_id):
  response = request_podcast_ml('/api/v1/series/user/')
  return series_dao.get_multiple_series(response['data']['series_ids'], user_id)

def get_episodes_for_user(user_id):
  response = request_podcast_ml('/api/v1/episodes/user/')
  return episodes_dao.get_episodes(response['data']['episode_ids'], user_id)
