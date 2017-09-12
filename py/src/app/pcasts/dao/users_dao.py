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

def get_user_by_valid_session(session_token):
  optional_session = Session.query.\
      filter(Session.session_token == session_token).\
      filter(Session.expires_at >= datetime.datetime.now()).\
      filter(Session.is_active).\
      first()

  if optional_session is None:
    return None

  return optional_session.user

def get_user_by_id(user_id):
  users = get_users_by_id([userid])
  if not users:
    raise Exception('User with user_id: {} does not exist'.format(str(user_id)))
  else:
    return users[0]

def get_users_by_id(user_ids):
  user_ids = set(user_ids)
  return User.query.filter(User.id.in_(user_ids)).all()
