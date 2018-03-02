from datetime import datetime as dt
from sqlalchemy.sql.expression import func
from app.pcasts.dao import episodes_dao
from . import *

def store_series_and_episodes_from_feed(feed):
  series_dict = feed.get('series')
  new_series = Series(
      id=series_dict.get('id'),
      title=series_dict.get('title'),
      country=series_dict.get('country'),
      author=series_dict.get('author'),
      image_url_lg=series_dict.get('image_url_lg'),
      image_url_sm=series_dict.get('image_url_sm'),
      feed_url=series_dict.get('feed_url'),
      genres=series_dict.get('genres')
  )
  models_to_commit = [new_series]
  for episode_dict in feed.get('episodes'):
    pub_date = None if episode_dict.get('pub_date') is None else \
      dt.fromtimestamp(episode_dict.get('pub_date'))
    ep = Episode(
        title=episode_dict.get('title'),
        author=episode_dict.get('author'),
        summary=episode_dict.get('summary'),
        pub_date=pub_date,
        duration=episode_dict.get('duration'),
        audio_url=episode_dict.get('audio_url'),
        tags=episode_dict.get('tags'),
        series_id=new_series.id)
    models_to_commit.append(ep)
  results = db_utils.commit_models(models_to_commit)
  return results[0] # return the resultant series

def remove_series(series_id):
  return Series.query.filter(Series.id == series_id).delete()

def get_series(series_id, user_id):
  series = Series.query.filter(Series.id == series_id).first()
  series.is_subscribed = is_subscribed_by_user(series_id, user_id)
  most_recent_episode_pub_date = Episode.query.\
    with_entities(func.max(Episode.pub_date)).\
    filter(Episode.series_id == series_id).first()[0]
  series.last_updated = most_recent_episode_pub_date
  return series

def get_multiple_series(series_ids, user_id):
  # returns series in ascending order
  if not series_ids:
    return []
  series = Series.query.filter(Series.id.in_(series_ids)) \
    .order_by(Series.id.asc()).all()
  last_updated_result = Episode.query \
    .with_entities(Episode.series_id, func.max(Episode.pub_date)) \
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
      filter(Series.title.like('%' + search_name + '%')).\
      offset(offset).\
      limit(max_search).\
      all()
  ]

  return get_multiple_series(possible_series_ids, user_id)

def get_top_series_by_subscribers(offset, max_search, user_id):
  found_series_ids = [
      tup[0] for tup in
      Series.query.\
      with_entities(Series.id, Series.subscribers_count).\
      order_by(Series.subscribers_count.desc()).\
      offset(offset).\
      limit(max_search).\
      all()
  ]
  found_series = get_multiple_series(found_series_ids, user_id)
  return order_by_ids(found_series_ids, found_series)

#XXX: Temporary fix until we can get cleaner devops
def refresh_series():
  series = Series.query.filter(Series.topic_id is not None).all()
  if len(series) != 0:
    for show in series:
      show.topic_id, show.subtopic_id = \
          topic_utils.get_topic_ids(show.genres.split(";"))
    db_utils.db_session_commit()
