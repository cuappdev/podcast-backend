from app.pcasts.models._all import *
from app.pcasts.utils import db_utils

def order_by_ids(ids, models):
  assert len(ids) == len(models)
  id_to_idx = {m_id : idx for idx, m_id in enumerate(ids)}
  results = [None] * len(models)
  for m in models:
    results[id_to_idx[m.id]] = m
  return results
