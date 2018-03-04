import datetime
from app.pcasts.dao import episodes_dao
from . import *

def create_share(sharer_id, sharee_id, episode_id):
  share = Share.query.filter(Share.sharer_id == sharer_id,
                             Share.sharee_id == sharee_id,
                             Share.episode_id == episode_id).first()
  if share:
    # Update updated_at instead of creating a duplicate
    share.updated_at = datetime.datetime.now()
  else:
    optional_episode = episodes_dao.get_episode(episode_id, sharer_id)
    if optional_episode:
      share = Share(sharer_id=sharer_id, sharee_id=sharee_id,
                    episode_id=episode_id)
    else:
      raise Exception("Invalid episode_id provided")
  return db_utils.commit_model(share)

def delete_share(share_id):
  optional_share = Share.query.filter(Share.id == share_id).first()
  if optional_share:
        return db_utils.delete_model(optional_share)
  else:
    raise Exception('Specified share does not exist')

def get_shared_with_user(user_id, max_shares, offset):
  shares = Share.query.filter(Share.sharee_id == user_id) \
            .limit(max_shares) \
            .offset(offset) \
            .all()
  episodes = episodes_dao.get_episodes([s.episode_id for s in shares],
                                       user_id)
  for s, e in zip(shares, episodes):
    s.episode = e
  return shares
