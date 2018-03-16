import datetime
from . import *

def get_or_create_user_from_google_creds(google_creds):
  if 'id' not in google_creds:
    raise Exception('Issue with google credentials!  Your access_token ' + \
        'might be expired!')

  optional_user = \
      User.query.filter(User.google_id == google_creds['id']).first()

  if optional_user is not None:
    return optional_user, False

  user = User(
      google_id=google_creds['id'],
      first_name=google_creds['given_name'],
      last_name=google_creds['family_name'],
      image_url=google_creds['picture']
  )

  return db_utils.commit_model(user), True

def get_or_create_user_from_facebook_creds(facebook_creds):
  if 'id' not in facebook_creds:
    raise Exception('Issue with facebook credentials!  Your access_token ' + \
        'might be expired!')

  optional_user = \
      User.query.filter(User.facebook_id == facebook_creds['id']).first()

  if optional_user is not None:
    return optional_user, False

  user = User(
      facebook_id=facebook_creds['id'],
      first_name=facebook_creds['first_name'],
      last_name=facebook_creds['last_name'],
      image_url=facebook_creds['picture']['data']['url']
  )

  return db_utils.commit_model(user), True

def get_user_by_valid_session(session_token):
  optional_session = Session.query.\
      filter(Session.session_token == session_token).\
      filter(Session.expires_at >= datetime.datetime.now()).\
      filter(Session.is_active).\
      first()

  if optional_session is None:
    return None

  return optional_session.user

def get_user_by_id(my_id, their_id):
  users = get_users_by_id(my_id, [their_id])
  if not users:
    raise Exception('User with user_id: {} does not exist'.\
                    format(str(their_id)))
  else:
    return users[0]

def get_users_by_id(my_id, their_ids):
  their_ids = set(their_ids)
  them = User.query.filter(User.id.in_(their_ids)).all()
  for u in them:
    u.is_following = is_following_user(my_id, u.id)
  return them

def get_user_by_google_id(google_id):
  optional_user = User.query.filter(User.google_id == google_id).first()
  if not optional_user:
    raise Exception('User with google_id: {} does not exist'.format(google_id))
  else:
    return optional_user

def is_following_user(my_id, their_id):
  optional_following = Following.query \
    .filter(Following.follower_id == my_id,
            Following.followed_id == their_id) \
    .first()
  return optional_following is not None

# A user cannot search for themselves
def search_users(search_name, offset, max_search, user_id):
  possible_users = User.query.filter \
      (User.username.like('%' + search_name + '%') |
       User.first_name.like('%' + search_name + '%') |
       User.last_name.like('%' + search_name + '%')) \
      .filter(User.id != user_id) \
      .offset(offset).limit(max_search).all()
  for u in possible_users:
    u.is_following = is_following_user(user_id, u.id)
  return possible_users

def change_user_name(user_id, new_name):
  user = User.query.filter(User.id == user_id).first()
  if user:
    user.username = new_name
    db_utils.db_session_commit()
    return user
  else:
    raise Exception("The given user_id is invalid")

def get_number_users():
  return User.query.count()

def get_all_users():
  return User.query.filter().all()

def add_facebook_login(user, facebook_info):
  #TODO: Improve error handling through specific handler
  if 'id' not in facebook_info:
    raise Exception('Issue with platform credentials!  Your access_token ' + \
        'might be expired!')
  updated_user = User.query.filter(User.id == user.id).first()
  updated_user.facebook_id = facebook_info['id']
  return db_utils.commit_model(user)

def add_google_login(user, google_info):
  if 'id' not in google_info:
    raise Exception('Issue with platform credentials!  Your access_token ' + \
        'might be expired!')
  updated_user = User.query.filter(User.id == user.id).first()
  updated_user.google_id = google_info['id']
  return db_utils.commit_model(updated_user)

# Enforces an ordering on followers_count
def get_users_by_facebook_ids(facebook_ids, user_id, offset, max_search, \
    return_following):
  if not facebook_ids or facebook_ids == []:
    return []
  user_id = int(user_id)
  if return_following:
    users = User.query.filter(User.facebook_id.in_(facebook_ids)) \
        .order_by(User.followers_count.desc()) \
        .offset(offset).limit(max_search).all()
    for u in users:
      u.is_following = is_following_user(user_id, u.id)
  else: # Facebook friends you don't follow
    users = User.query.filter(User.facebook_id.in_(facebook_ids)).\
        filter(~Following.query.filter(Following.follower_id == user_id, \
                Following.followed_id == User.id).exists())\
        .order_by(User.followers_count.desc()) \
        .offset(offset).limit(max_search).all()
    for u in users:
      u.is_following = False
  return users

def search_facebook_users(facebook_ids, user_id, offset, max_search \
    , query=None):
  if not facebook_ids or facebook_ids == []:
    return []
  #TODO: Elasticsearch
  users = User.query.filter(User.facebook_id.in_(facebook_ids)) \
      .filter(User.username.like('%' + query + '%') |
              User.first_name.like('%' + query + '%') |
              User.last_name.like('%' + query + '%')) \
      .offset(offset).limit(max_search).all()
  for u in users:
    u.is_following = is_following_user(user_id, u.id)
  return users
