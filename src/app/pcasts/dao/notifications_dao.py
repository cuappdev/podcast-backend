import json
from . import *
from app.pcasts.dao import series_dao, episodes_dao

def new_episode_notifications(user_id, max_episodes, offset):
  subscriptions = get_subscribed_series_for_new_episodes(user_id)
  series_ids = [s.series_id for s in subscriptions]
  episodes = episodes_dao.get_episodes_by_many_series(user_id, series_ids,  \
      offset, max_episodes)
  # Populate series data
  series = series_dao.get_multiple_series(series_ids, user_id)
  series_map = {s.id: s for s in series}
  for e in episodes:
    e.series = series_map[e.series_id]
    e.unread_notifcation = is_unread_notification(user_id, e.id)
  return episodes

def is_unread_notification(user_id, episode_id):
  read = ReadNewEpisodeNotification.query. \
      filter(ReadNewEpisodeNotification.user_id == user_id,\
             ReadNewEpisodeNotification.episode_id == episode_id).first()
  return read is None

def get_subscribed_series_for_new_episodes(user_id):
  return Subscription.query.filter(Subscription.user_id == user_id and \
      Subscription.subscribed_new_episodes == True).all()

def update_new_episode_has_read(user_id, episode_ids):
  series_ids = episodes_dao.get_series_ids_from_episodes(episode_ids)
  subscriptions = subscriptions_dao.get_user_subscriptions_for_series(user_id,\
      series_ids)

  series_to_subscription = {}
  for subscription in subscriptions:
    series_to_subscription[subscription.series_id] = subscription.id

  episode_to_subscription = {}
  for episode_id in episode_ids:
    episode = Episode.query.filter(Episode.id == episode_id).first()
    episode_to_subscription[episode.id] = \
        series_to_subscription[episode.series_id]

  read_notifications = []
  for episode_id in episode_ids:
    read_notifications.append(ReadNewEpisodeNotification(user_id=user_id, \
        episode_id=episode_id, subscription_id=episode_to_subscription[episode_id]))
  db_utils.commit_models(read_notifications)

def create_new_episode_notification(user_id, series_id):
  maybe_subscription = Subscription.query. \
      filter(Subscription.series_id == series_id).first()
  if maybe_subscription is None:
    raise Exception("You are not subscribed to this series")
  maybe_subscription.subscribed_new_episodes = True
  db_utils.commit_model(maybe_subscription)
