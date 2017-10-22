from sqlalchemy.sql.expression import func
from app.pcasts.dao import episodes_dao
from . import *

def get_series(series_id, user_id):
  series = Series.query.filter(Series.id == series_id).first()
  series.is_subscribed = is_subscribed_by_user(series_id, user_id)
  most_recent_episode = Episode.query.filter(Episode.series_id == series_id) \
    .order_by(Episode.created_at.desc()).limit(1).first()
  series.last_updated = most_recent_episode.created_at
  return series

def get_multiple_series(series_ids, user_id):
  if not series_ids:
    return []
  series = Series.query.filter(Series.id.in_(series_ids)) \
    .order_by(Series.id.asc()).all()
  last_updated_result = Episode.query \
    .with_entities(Episode.series_id, func.max(Episode.created_at)) \
    .filter(Episode.series_id.in_(series_ids)) \
    .group_by(Episode.series_id) \
    .order_by(Episode.series_id.asc()).all()
  for s, (_, last_updated) in zip(series, last_updated_result):
    s.is_subscribed = is_subscribed_by_user(s.id, user_id)
    s.last_updated = last_updated
  return series

def clear_all_subscriber_counts():
  series = Series.query.filter(Series.subscribers_count > 0).all()
  for s in series:
    s.subscribers_count = 0
  db_utils.commit_models(series)

def is_subscribed_by_user(series_id, user_id):
  optional_series = Subscription.query \
    .filter(Subscription.series_id == series_id,
            Subscription.user_id == user_id) \
    .first()
  return optional_series is not None

def search_series(search_name, offset, max_search, user_id):
  possible_series_ids = [
      tup[0] for tup in
      Series.query.\
      with_entities(Series.id).\
      filter(Series.title.like(search_name + '%')).\
      offset(offset).\
      limit(max_search).\
      all()
  ]

  return get_multiple_series(possible_series_ids, user_id)

def get_top_series(offset, max_search, user_id):
  found_series_ids = [
      tup[0] for tup in
      Series.query.\
      with_entities(Series.subscribers_count).\
      order_by(Series.subscribers_count.desc()).\
      offset(offet).\
      limit(max_search).\
      all()
  ]

  return get_multiple_series(found_series_ids, user_id)
