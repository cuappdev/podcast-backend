from app.pcasts.dao import episodes_dao
from . import *

def create_or_update_listening_history(episode_id, user):
  optional_episode = episodes_dao.get_episode(episode_id, user.id)
  if optional_episode:
    optional_listening_history = ListeningHistory.\
      query.\
      filter(
          ListeningHistory.episode_id == episode_id,
          ListeningHistory.user_id == user.id
      ).first()

    if optional_listening_history:
      optional_listening_history.update_listening_time()
      db_utils.db_session_commit()
      return optional_listening_history

    listening_history = ListeningHistory(episode_id=episode_id, user_id=user.id)
    return db_utils.commit_model(listening_history)
  else:
    raise Exception('Invalid episode_id provided')

def delete_listening_history(episode_id, user):
  optional_listening_history = ListeningHistory.query.\
    filter(
        ListeningHistory.user_id == user.id,
        ListeningHistory.episode_id == episode_id
    ).first()
  if optional_listening_history:
    return db_utils.delete_model(optional_listening_history)
  else:
    raise Exception('Specified history does not exist')

def clear_listening_history(user):
  listening_histories = \
    ListeningHistory.query.filter(ListeningHistory.user_id == user.id).all()
  db_utils.delete_models(listening_histories)

def get_listening_history(user, max_hs, offset):
  listening_histories = ListeningHistory.\
    query.\
    filter(ListeningHistory.user_id == user.id).\
    order_by(ListeningHistory.listening_time.desc()).\
    limit(max_hs).\
    offset(offset).\
    all()

  episodes = \
    episodes_dao.get_episodes([h.episode_id for h in listening_histories],
                              user.id)

  for h, e in zip(listening_histories, episodes):
    h.episode = e

  return listening_histories
