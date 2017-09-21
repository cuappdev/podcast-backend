from . import *

def get_series(series_id):
  return Series.query.filter(Series.id == series_id).first()

def get_multiple_series(series_ids):
   series = Series.query.filter(Series.id.in_(series_ids)).all()
   return series

def clear_all_subscriber_counts():
  series = Series.query.filter(Series.subscribers_count > 0).all()
  for s in series:
    s.subscribers_count = 0
  db_utils.commit_models(series)
