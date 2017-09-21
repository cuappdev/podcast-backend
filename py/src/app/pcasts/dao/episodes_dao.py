from . import *

def get_episodes(episode_ids):
  return Episode.query.filter(Episode.id.in_(episode_ids)).all()

def get_episode(episode_id):
  return Episode.query.filter(Episode.id == episode_id).first()

def clear_all_recommendations_counts():
  episodes = Episode.query.filter(Episode.recommendations_count > 0).all()
  for e in episodes:
    e.recommendations_count = 0
  db_utils.commit_models(episodes)
