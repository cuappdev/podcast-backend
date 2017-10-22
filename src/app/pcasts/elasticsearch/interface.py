from . import *

def search_series(term, offset, max_size):
  query = Search(using=es, index='series-index').\
      query('match', title=term)[offset:offset+max_size]
  results = query.execute()
  series_ids = [s['id'] for s in results]
  return series_ids

def search_episodes(term, offset, max_size):
  query = Search(using=es, index='episodes-index').\
      query('match', title=term)[offset:offset+max_size]
  results = query.execute()
  episode_ids = [e['id'] for e in results]
  return episode_ids

def search_users(term, offset, max_size):
  query = Search(using=es, index='Users-index').\
      query('match', username=term)[offset:offset+max_size]
  results = query.execute()
  user_ids = [u['id'] for u in results]
  return user_ids
