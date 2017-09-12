from app.pcasts.dao import episodes_dao
from . import *

def is_bookmarked_by_user(episode_id, user_id):
  optional_bookmark = Bookmark.query \
    .filter(Bookmark.episode_id == episode_id and Bookmark.user_id == user_id) \
    .first()
  return optional_bookmark is not None

def create_bookmark(episode_id, user):
  optional_episode = episodes_dao.get_episode(episode_id)
  if optional_episode:
    bookmark = Bookmark(episode_id=episode_id, user_id=user.id)
    bookmark.episode = episodes_dao.get_episode(episode_id)
    return db_utils.commit_model(bookmark)
  else:
    raise Exception("Invalid episode_id provided")

def delete_bookmark(episode_id, user):
  optional_bookmark = Bookmark.query \
    .filter(Bookmark.episode_id == episode_id, Bookmark.user_id == user.id) \
    .first()
  if optional_bookmark:
    optional_bookmark.episode = episodes_dao.get_episode(episode_id)
    return db_utils.delete_model(optional_bookmark)
  else:
    raise Exception('Specified bookmark does not exist')

def get_user_bookmarks(user):
  bookmarks = Bookmark.query.filter(Bookmark.user_id == user.id).all()
  episodes = episodes_dao.get_episodes([b.episode_id for b in bookmarks])
  for b, e in zip(bookmarks, episodes):
    b.episode = e
  return bookmarks
