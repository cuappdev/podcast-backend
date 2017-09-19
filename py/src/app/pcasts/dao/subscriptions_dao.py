from app.pcasts.dao import series_dao
from . import *

def get_user_subscriptions(user_id):
  subscriptions = \
    Subscription.query.filter(Subscription.user_id == user_id).all()
  attach_series(subscriptions)

  return subscriptions

def get_series_subscriptions(series_id, max_subs, offset):
  subscriptions = (
      Subscription.query.filter(Subscription.series_id == series_id)
      .limit(max_subs)
      .offset(offset)
      .all()
  )
  attach_series(subscriptions)

  return subscriptions

def create_subscription(user_id, series_id):
  maybe_series = series_dao.get_series(series_id)
  if maybe_series:
    subscription = Subscription(
        user_id=user_id,
        series_id=series_id,
        series=maybe_series
    )
  else:
    raise Exception("Invalid series_id provided")

  return db_utils.commit_model(subscription)

def delete_subscription(user_id, series_id):
  maybe_subscription = \
    Subscription.query.filter(Subscription.series_id == series_id).first()

  if maybe_subscription:
    attach_series([maybe_subscription])
    return db_utils.delete_model(maybe_subscription)
  else:
    raise Exception("Specified subscription does not exist")

def attach_series(subscriptions):
  for subscription in subscriptions:
    subscription.series = series_dao.get_series(subscription.series_id)

def get_new_subscribed_episodes(user_id, maxtime, page_size):
  subscriptions = get_user_subscriptions(user_id)
  subscription_ids = [s.id for s in subscriptions]
  return Episode.query \
    .filter(Episode.series_id.in_(subscription_ids) and
            Episode.created_at < maxtime) \
    .order_by(Episode.created_at.desc()) \
    .limit(page_size) \
    .all()
