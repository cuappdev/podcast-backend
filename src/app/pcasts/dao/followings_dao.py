import datetime
import time
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
  follower_id = int(follower_id)
  followed_id = int(followed_id)
  if follower_id != followed_id:
    users = users_dao.get_users_by_id(follower_id, [follower_id, followed_id])
    if len(users) == 2:
      following = Following(follower_id=follower_id, followed_id=followed_id)
      follower = users[0] if users[0].id == follower_id else users[1]
      followed = users[0] if users[0].id == followed_id else users[1]
      follower.followings_count = follower.followings_count + 1
      followed.followers_count = followed.followers_count + 1
      return db_utils.commit_model(following)
    else:
      raise Exception("Improper follower_id and/or followed_id")
  else:
    raise Exception("follower_id cannot equal followed_id")

def delete_following(follower_id, followed_id):
  follower_id = int(follower_id)
  followed_id = int(followed_id)
  maybe_following = Following.query \
    .filter(Following.follower_id == follower_id,
            Following.followed_id == followed_id) \
    .first()

  if maybe_following:
    users = users_dao.get_users_by_id(follower_id, [follower_id, followed_id])
    follower = users[0] if users[0].id == follower_id else users[1]
    followed = users[0] if users[0].id == followed_id else users[1]
    follower.followings_count = follower.followings_count - 1
    followed.followers_count = followed.followers_count - 1

    db_utils.delete_model(maybe_following)
  else:
    raise Exception("Specified following does not exist")

def attach_is_following_to_json(my_id, followings_json):
  for json in followings_json:
    json['follower'][unicode('is_following', "utf-8")] = users_dao.\
        is_following_user(my_id, json['follower']['id'])
    json['followed'][unicode('is_following', "utf-8")] = users_dao.\
        is_following_user(my_id, json['followed']['id'])
