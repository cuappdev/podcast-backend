import json
import shutil
import pickle
import config
from datetime import datetime
from time import time
from . import *

def populate_series(last_updated):
  new_series = Series.query.filter(Series.updated_at > last_updated).all()

  with open('{}/data/pcasts_series.json'.\
            format(constants.ELASTICSEARCH_PATH), 'a') as f:
    for s in new_series:
      series_json = series_schema.dump(s).data
      series_json['index_type'] = 'series'
      series_json['document_id'] = series_json['id']
      json.dump(series_json, f)
      print >>f, ''

def populate_episodes(last_updated):
  new_episodes = Episode.query.filter(Episode.updated_at > last_updated).all()

  with open('{}/data/pcasts_episode.json'.\
            format(constants.ELASTICSEARCH_PATH), 'a') as f:
    for e in new_episodes:
      episode_json = episode_schema.dump(e).data
      episode_json['index_type'] = 'episodes'
      episode_json['document_id'] = episode_json['id']
      json.dump(episode_json, f)
      print >>f, ''

def populate_users(last_updated):
  new_users = User.query.filter(User.updated_at > last_updated).all()

  with open('{}/data/pcasts_user.json'.\
            format(constants.ELASTICSEARCH_PATH), 'a') as f:
    for u in new_users:
      user_json = user_schema.dump(u).data
      user_json['index_type'] = 'users'
      json.dump(user_json, f)
      print >>f, ''

def get_last_updated():
  try:
    d = pickle.load(open(constants.ELASTICSEARCH_PICKLE_PATH))
    last_updated = datetime.datetime.\
        strptime(d[constants.ELASTICSEARCH_PICKLE_KEY], "%Y-%m-%d %H:%M:%S.%f")
  except IOError:
    last_updated = datetime.datetime.min
  return last_updated

def set_last_updated(last_updated):
  d = {constants.ELASTICSEARCH_PICKLE_KEY: str(last_updated)}
  pickle.dump(d, open(constants.ELASTICSEARCH_PICKLE_PATH, "wb"))

def populate():
  last_updated = get_last_updated()
  new_last_updated = datetime.datetime.now()
  try:
    shutil.rmtree('{}/data'.format(constants.ELASTICSEARCH_PATH))
    os.mkdir('{}/data'.format(constants.ELASTICSEARCH_PATH))
  except OSError:
    os.mkdir('{}/data'.format(constants.ELASTICSEARCH_PATH))

  populate_series(last_updated)
  populate_episodes(last_updated)
  populate_users(last_updated)

  os.chdir(constants.ELASTICSEARCH_PATH)
  os.system('cat ./data/*.json | {}/bin/logstash -f logstash.conf'.\
      format(config.LOGSTASH_PATH))
  set_last_updated(new_last_updated)
