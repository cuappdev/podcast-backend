from . import *

def get_series(series_id):
  return Series.query.filter(Series.id == series_id).first()
