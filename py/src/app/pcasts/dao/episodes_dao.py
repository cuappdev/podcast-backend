from . import *

def get_episodes(episode_ids):
  return Episode.query.filter(Episode.id.in_(episode_ids)).all()

def get_episode(episode_id):
  return Episode.query.filter(Episode.id == episode_id).first()
