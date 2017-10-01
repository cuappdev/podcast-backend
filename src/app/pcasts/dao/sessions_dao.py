from . import *

def get_or_create_session_and_activate(user_id):
  optional_session = Session.query.filter(Session.user_id == user_id).first()

  if optional_session is not None:
    session = optional_session
  else:
    session = db_utils.commit_model(Session(user_id=user_id))

  session.activate_session()
  db_utils.db_session_commit()

  return session

def update_session(update_token):
  optional_session = Session.query.filter(
      Session.update_token == update_token,
      Session.is_active
  ).first()

  if not optional_session:
    raise Exception('Invalid update_token or expired session')

  optional_session.activate_session()
  db_utils.db_session_commit()

  return session

def deactivate_session(user_id):
  optional_session = Session.query.filter(Session.user_id == user_id).first()

  if not optional_session:
    return False

  optional_session.deactivate()
  db_utils.db_session_commit()
  return True
