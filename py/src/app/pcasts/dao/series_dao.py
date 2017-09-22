from . import *

def get_series(series_id, user_id):
  series = Series.query.filter(Series.id == series_id).first()
  series.is_subscribed = is_subscribed_by_user(series_id, user_id)
  return series

def get_multiple_series(series_ids, user_id):
  series = Series.query.filter(Series.id.in_(series_ids)).all()
  for s in series:
    s.is_subscribed = is_subscribed_by_user(s.id, user_id)
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
