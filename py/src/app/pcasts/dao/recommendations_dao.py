from app.pcasts.dao import episodes_dao
from . import *

def create_recommendation(episode_id, user):
  optional_episode = episodes_dao.get_episode(episode_id, user.id)
  if optional_episode:
    recommendation = Recommendation(episode_id=episode_id, user_id=user.id)
    recommendation.episode = optional_episode
    optional_episode.recommendations_count += 1
    return db_utils.commit_model(recommendation)
  else:
    raise Exception("Invalid episode_id provided")

def delete_recommendation(episode_id, user):
  optional_recommendation = Recommendation.query \
  .filter(Recommendation.episode_id == episode_id,
          Recommendation.user_id == user.id).first()
  if optional_recommendation:
    episode = episodes_dao.get_episode(episode_id, user.id)
    optional_recommendation.episode = episode
    episode.recommendations_count -= 1
    return db_utils.delete_model(optional_recommendation)
  else:
    raise Exception('Specified recommendation does not exist')

def get_user_recommendations(user_id):
  recommendations = (
      Recommendation.query.filter(Recommendation.user_id == user_id).all()
  )
  episodes = episodes_dao.get_episodes([r.episode_id for r in recommendations],
                                       user.id)
  for r, e in zip(recommendations, episodes):
    r.episode = e
  return recommendations

def get_episode_recommendations(episode_id, max_recs, offset):
  recommendations = (
      Recommendation.query.filter(Recommendation.episode_id == episode_id)
      .limit(max_recs)
      .offset(offset)
      .all()
  )
  episodes = episodes_dao.get_episodes([r.episode_id for r in recommendations],
                                       user.id)
  for r, e in zip(recommendations, episodes):
    r.episode = e
  return recommendations
