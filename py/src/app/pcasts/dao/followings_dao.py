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
    users = users_dao.get_users_by_id([follower_id, followed_id])
    if len(users) == 2:
      follower = users[0] if users[0].id == follower_id else users[1]
      followed = users[0] if users[0].id == followed_id else users[1]
      follower.followings_count += 1
      followed.followers_count += 1
    else:
      raise Exception("Improper follower_id and/or followed_id")
  else:
    raise Exception("follower_id cannot equal followed_id")

  return db_utils.commit_model(following)

def delete_following(follower_id, followed_id):
  maybe_following = Following.query \
    .filter(Following.follower_id == follower_id,
            Following.followed_id == followed_id) \
    .first()

  if maybe_following:
    users = users_dao.get_users_by_id([follower_id, followed_id])
    follower = users[0] if users[0].id == follower_id else users[1]
    followed = users[0] if users[0].id == followed_id else users[1]
    follower.followings_count -= 1
    followed.followers_count -= 1
    db_utils.delete_model(maybe_following)
  else:
    raise Exception("Specified following does not exist")

def get_following_recommendations(user_id, maxtime, page_size):
  followings = get_followings(user_id)
  following_ids = [f.id for f in followings]
  return Recommendation.query \
    .filter(Recommendation.user_id.in_(following_ids) and
            Recommendation.created_at < maxtime) \
    .order_by(Recommendation.created_at.desc()) \
    .limit(page_size) \
    .all()

def get_following_subscriptions(user_id, maxtime, page_size):
  followings = get_followings(user_id)
  following_ids = [f.id for f in followings]
  return Subscription.query \
    .filter(Subscription.user_id.in_(following_ids) and
            Subscription.created_at < maxtime) \
    .order_by(Subscription.created_at.desc()) \
    .limit(page_size) \
    .all()
