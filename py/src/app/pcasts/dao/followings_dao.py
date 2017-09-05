from app.pcasts.dao import users_dao
from . import *

def get_followings(user_id):
  followings = \
    Following.query.filter(Following.follower_id == user_id).all()

  return followings

def get_followers(user_id):
  followers = \
    Following.query.filter(Following.followed_id == user_id).all()

  return followers

def create_following(follower_id, followed_id):
  if int(follower_id) != int(followed_id):
    following = Following(follower_id=follower_id, followed_id=followed_id)
    follower = users_dao.get_user_by_id(follower_id)
    followed = users_dao.get_user_by_id(followed_id)
    if follower and followed:
      follower.followings_count += 1
      followed.followers_count += 1
    else:
      raise Exception("Improper follower_id or followed_id")
  else:
    raise Exception("follower_id cannot equal followed_id")

  return db_utils.commit_model(following)

def delete_following(follower_id, followed_id):
  maybe_following = Following.query \
    .filter(Following.follower_id == follower_id
            and Following.followed_id == followed_id) \
    .first()

  if maybe_following:
    follower = users_dao.get_user_by_id(follower_id)
    followed = users_dao.get_user_by_id(followed_id)
    follower.followings_count -= 1
    followed.followers_count -= 1
    db_utils.delete_model(maybe_following)
  else:
    raise Exception("Specified following does not exist")
