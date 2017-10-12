import datetime
from app.pcasts.dao import series_dao, episodes_dao
from . import *

def get_user_subscriptions(user_id):
  subscriptions = \
    Subscription.query.filter(Subscription.user_id == user_id).all()
  series = \
    series_dao.get_multiple_series([s.series_id for s in subscriptions],
                                   user_id)
  for sub, ser in zip(subscriptions, series):
    sub.series = ser

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
  for sub, ser in zip(subscriptions, series):
    sub.series = ser

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
    Subscription.query.filter(Subscription.series_id == series_id).first()

  if maybe_subscription:
    maybe_subscription.series = \
      series_dao.get_series(maybe_subscription.series_id, user_id)
    maybe_subscription.series.subscribers_count -= 1
    return db_utils.delete_model(maybe_subscription)
  else:
    raise Exception("Specified subscription does not exist")

def get_new_subscribed_episodes(user_id, maxtime, page_size):
  subscriptions = get_user_subscriptions(user_id)
  series_ids = [s.series_id for s in subscriptions]
  maxdatetime = datetime.datetime.fromtimestamp(int(maxtime))
  return episodes_dao.get_episodes_maxtime(
      user_id,
      series_ids,
      maxdatetime,
      page_size
  )
