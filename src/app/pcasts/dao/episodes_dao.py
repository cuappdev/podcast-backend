from . import *

def get_episodes(episode_ids, user_id):
  if not episode_ids:
    return []
  episodes = Episode.query.filter(Episode.id.in_(episode_ids)).all()
  for e in episodes:
    e.is_recommended = is_recommended_by_user(e.id, user_id)
    e.is_bookmarked = is_bookmarked_by_user(e.id, user_id)
  return episodes

def get_episode(episode_id, user_id):
  episode = Episode.query.filter(Episode.id == episode_id).first()
  episode.is_recommended = is_recommended_by_user(episode_id, user_id)
  episode.is_bookmarked = is_bookmarked_by_user(episode_id, user_id)
  return episode

def get_episode_by_title(title, user_id):
  episode = Episode.query.filter(Episode.title == title).first()
  episode.is_recommended = is_recommended_by_user(episode.id, user_id)
  episode.is_bookmarked = is_bookmarked_by_user(episode.id, user_id)
  return episode

def get_episodes_by_series(series_id, offset, max_search):
  episodes = Episode.query.filter(Episode.series_id == \
      series_id).order_by(Episode.pub_date.desc())\
      .offset(offset).limit(max_search).all()
  return episodes

def get_episodes_maxtime(user_id, series_ids, maxdatetime, page_size):
  episodes = Episode.query \
    .filter(Episode.series_id.in_(series_ids),
            Episode.created_at <= maxdatetime) \
    .order_by(Episode.created_at.desc()) \
    .limit(page_size) \
    .all()
  for e in episodes:
    e.is_recommended = is_recommended_by_user(e.id, user_id)
    e.is_bookmarked = is_bookmarked_by_user(e.id, user_id)
  return episodes

def clear_all_recommendations_counts():
  episodes = Episode.query.filter(Episode.recommendations_count > 0).all()
  for e in episodes:
    e.recommendations_count = 0
  db_utils.commit_models(episodes)

def is_bookmarked_by_user(episode_id, user_id):
  optional_bookmark = Bookmark.query \
    .filter(Bookmark.episode_id == episode_id, Bookmark.user_id == user_id) \
    .first()
  return optional_bookmark is not None

def is_recommended_by_user(episode_id, user_id):
  optional_recommendation = Recommendation.query \
    .filter(Recommendation.episode_id == episode_id,
            Recommendation.user_id == user_id) \
    .first()
  return optional_recommendation is not None

def search_episode(search_name, offset, max_search):
  possible_episodes = Episode.query.filter \
      (Episode.title.like(search_name+'%')) \
      .offset(offset).limit(max_search).all()
  return possible_episodes
