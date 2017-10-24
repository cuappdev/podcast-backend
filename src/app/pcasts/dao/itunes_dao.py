import podcasts.itunes as itunes
from app.pcasts.dao import series_dao
from . import *

def search_from_itunes(query, user_id):
  searched_series = itunes.search_podcast_series(query)
  # Found series in the DB
  found_series = series_dao.\
    get_multiple_series([s.id for s in searched_series], user_id)
  found_series_ids_set = set([s.id for s in found_series])
  # Novel series + their feeds
  new_searched_series = \
    [s for s in searched_series if int(s.id) not in found_series_ids_set]
  feeds = itunes.get_feeds_from_many_series(new_searched_series)
  # Store full series + eps per feed + grab resultant series
  new_series = [
      series_dao.store_series_and_episodes_from_feed(feed)
      for feed in feeds
  ]
  # Sorted, synthesized result
  return \
    sorted(found_series + new_series, key=lambda s: s.id), \
    [s.id for s in new_searched_series]
