from . import *

def get_episodes(episode_ids, user_id):
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

def clear_all_recommendations_counts():
  episodes = Episode.query.filter(Episode.recommendations_count > 0).all()
  for e in episodes:
    e.recommendations_count = 0
  db_utils.commit_models(episodes)

def is_bookmarked_by_user(episode_id, user_id):
  optional_bookmark = Bookmark.query \
    .filter(Bookmark.episode_id == episode_id and Bookmark.user_id == user_id) \
    .first()
  return optional_bookmark is not None

def is_recommended_by_user(episode_id, user_id):
  optional_recommendation = Recommendation.query \
    .filter(Recommendation.episode_id == episode_id and
            Recommendation.user_id == user_id) \
    .first()
  return optional_recommendation is not None
