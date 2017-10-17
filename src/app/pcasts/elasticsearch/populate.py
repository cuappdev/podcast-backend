import pickle
from datetime import datetime
from time import time
from . import *

def populate_series(last_updated):
  new_series = Series.query.filter(Series.updated_at > last_updated).all()
  serialized_series = [series_schema.dump(s).data for s in new_series]
  actions = [
      {
          "_index": "series-index",
          "_type": "series",
          "_id": s['id'],
          "_source": s
      }
      for s in serialized_series
  ]
  helpers.bulk(es, actions)

def populate_episodes(last_updated):
  new_episodes = Episode.query.filter(Episode.updated_at > last_updated).all()
  serialized_episodes = [episode_schema.dump(e).data for e in new_episodes]
  actions = [
      {
          "_index": "episodes-index",
          "_type": "episode",
          "_id": e['id'],
          "_source": e
      }
      for e in serialized_episodes
  ]
  helpers.bulk(es, actions)

def populate_users(last_updated):
  new_users = User.query.filter(User.updated_at > last_updated).all()
  serialized_users = [user_schema.dump(u).data for u in new_users]
  actions = [
      {
          "_index": "users-index",
          "_type": "episode",
          "_id": u['id'],
          "_source": u
      }
      for u in serialized_users
  ]
  helpers.bulk(es, actions)

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
  populate_series(last_updated)
  populate_episodes(last_updated)
  populate_users(last_updated)
  set_last_updated(new_last_updated)
