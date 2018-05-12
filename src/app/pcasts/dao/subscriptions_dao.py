import datetime
from app.pcasts.dao import series_dao
from . import *

def get_user_subscriptions(their_id, my_id):
  subscriptions = \
    Subscription.query.filter(Subscription.user_id == their_id).all()
  series = \
    series_dao.get_multiple_series([s.series_id for s in subscriptions],
                                   my_id)
  series_id_to_series = {s.id:s for s in series}
  for sub in subscriptions:
    sub.series = series_id_to_series[sub.series_id]

  return subscriptions

def get_series_subscriptions(series_id, user_id, max_subs, offset):
  subscriptions = (
      Subscription.query.filter(Subscription.series_id == series_id)
      .limit(max_subs)
      .offset(offset)
      .all()
  )

  series = \
    series_dao.get_multiple_series([s.series_id for s in subscriptions],
                                   user_id)
  series_id_to_series = {s.id:s for s in series}
  for sub in subscriptions:
    sub.series = series_id_to_series[sub.series_id]

  return subscriptions

def create_subscription(user_id, series_id):
  maybe_series = series_dao.get_series(series_id, user_id)
  if maybe_series:
    subscription = Subscription(
        user_id=user_id,
        series_id=series_id,
        series=maybe_series
    )
    maybe_series.subscribers_count += 1
  else:
    raise Exception("Invalid series_id provided")

  return db_utils.commit_model(subscription)

def delete_subscription(user_id, series_id):
  maybe_subscription = \
    Subscription.query.filter(Subscription.series_id == series_id,
                              Subscription.user_id == user_id).first()

  if maybe_subscription:
    maybe_subscription.series = \
      series_dao.get_series(maybe_subscription.series_id, user_id)
    maybe_subscription.series.subscribers_count -= 1
    return db_utils.delete_model(maybe_subscription)
  else:
    raise Exception("Specified subscription does not exist")

def get_user_subscriptions_for_series(user_id, series_ids):
  return Subscription.query.filter(Subscription.user_id == user_id,\
    Subscription.series_id.in_(series_ids)).all()
