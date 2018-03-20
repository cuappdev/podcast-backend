from app.pcasts.dao import episodes_dao
from . import *

def create_bookmark(episode_id, user):
  optional_episode = episodes_dao.get_episode(episode_id, user.id)
  if optional_episode:
    bookmark = Bookmark(episode_id=episode_id, user_id=user.id)
    bookmark.episode = episodes_dao.get_episode(episode_id, user.id)
    return db_utils.commit_model(bookmark)
  else:
    raise Exception("Invalid episode_id provided")

def delete_bookmark(episode_id, user):
  optional_bookmark = Bookmark.query \
    .filter(Bookmark.episode_id == episode_id, Bookmark.user_id == user.id) \
    .first()
  if optional_bookmark:
    optional_bookmark.episode = episodes_dao.get_episode(episode_id, user.id)
    return db_utils.delete_model(optional_bookmark)
  else:
    raise Exception('Specified bookmark does not exist')

def get_user_bookmarks(user):
  bookmarks = Bookmark.query.filter(Bookmark.user_id == user.id).all()
  episodes = episodes_dao.get_episodes([b.episode_id for b in bookmarks],
                                       user.id)
  episode_id_to_episode = {e.id:e for e in episodes}
  for b in bookmarks:
    b.episode = episode_id_to_episode[b.episode_id]
  return bookmarks
