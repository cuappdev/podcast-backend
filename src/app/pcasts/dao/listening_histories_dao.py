from app.pcasts.dao import episodes_dao
from . import *

def create_or_update_listening_histories(episode_update_info_map, user):
  episode_ids = episode_update_info_map.keys()

  listening_histories = \
    ListeningHistory.query.filter(ListeningHistory.episode_id.in_(episode_ids),
                                  ListeningHistory.user_id == user.id).all()
  listening_histories_by_episode = \
    {lh.episode_id: lh for lh in listening_histories}
  result = []
  for episode_id in episode_ids:
    try:
      current_progress = \
        episode_update_info_map[episode_id]['current_progress']
      percentage_listened = \
        episode_update_info_map[episode_id]['percentage_listened']
    except KeyError:
      raise Exception('Invalid episode_id {} provided'.format(episode_id))
    if episode_id in listening_histories_by_episode:
      lh = listening_histories_by_episode[episode_id]
      lh.current_progress = current_progress
      lh.percentage_listened = lh.percentage_listened + percentage_listened
    else:
      lh = \
        ListeningHistory(episode_id=episode_id, user_id=user.id,
                         percentage_listened=percentage_listened,
                         current_progress=current_progress)
      db.session.add(lh)
    result.append(lh)
  # Update episode durations if needed
  episode_ids_to_query = []
  for episode_id, values in episode_update_info_map.iteritems():
    if 'real_duration' in values:
      episode_ids_to_query.append(episode_id)
  episodes = episodes_dao.get_episodes(episode_ids_to_query, user.id)
  for episode in episodes:
    if not episode.real_duration_written:
      episode.duration = episode_update_info_map[episode.id]['real_duration']
      episode.real_duration_written = True
  db_utils.db_session_commit()
  return result

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
    order_by(ListeningHistory.updated_at.desc()).\
    limit(max_hs).\
    offset(offset).\
    all()

  episodes = \
    episodes_dao.get_episodes([h.episode_id for h in listening_histories],
                              user.id)

  for h, e in zip(listening_histories, episodes):
    h.episode = e

  return listening_histories
