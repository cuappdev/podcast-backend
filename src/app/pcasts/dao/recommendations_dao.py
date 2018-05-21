from app.pcasts.dao import episodes_dao
from . import *

def create_or_update_recommendation(episode_id, user, blurb=None):
  optional_episode = episodes_dao.get_episode(episode_id, user.id)
  if optional_episode:
    recommendation = Recommendation.query\
      .filter(Recommendation.episode_id == episode_id,
              Recommendation.user_id == user.id).first()
    if recommendation:
      recommendation.blurb = blurb
      recommendation.episode = optional_episode
    else:
      recommendation = \
        Recommendation(episode_id=episode_id, user_id=user.id, blurb=blurb)
      optional_episode.is_recommended = True
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
    episode.is_recommended = False
    optional_recommendation.episode = episode
    episode.recommendations_count -= 1
    return db_utils.delete_model(optional_recommendation)
  else:
    raise Exception('Specified recommendation does not exist')

def get_user_recommendations(caller_user_id, requested_user_id):
  recommendations = (
      Recommendation.query.\
      filter(Recommendation.user_id == requested_user_id).\
      order_by(Recommendation.created_at.desc()).
      all()
  )
  episodes = episodes_dao.get_episodes([r.episode_id for r in recommendations],
                                       caller_user_id)
  episode_id_to_episode = {e.id:e for e in episodes}
  for r in recommendations:
    r.episode = episode_id_to_episode[r.episode_id]
  return recommendations

def get_episode_recommendations(episode_id, user_id, max_recs, offset):
  recommendations = (
      Recommendation.query.filter(Recommendation.episode_id == episode_id)
      .limit(max_recs)
      .offset(offset)
      .all()
  )
  episodes = episodes_dao.get_episodes([r.episode_id for r in recommendations],
                                       user_id)
  episode_id_to_episode = {e.id:e for e in episodes}
  for r in recommendations:
    r.episode = episode_id_to_episode[r.episode_id]
  return recommendations
