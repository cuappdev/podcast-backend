from . import *

def get_or_create_session_and_activate(user_id):
  optional_session = Session.query.filter(Session.user_id == user_id).first()

  if optional_session is not None:
    session = optional_session
  else:
    session = db_utils.commit_model(Session(user_id=user_id))

  # Activates the session (is_active is true, updates tokens, etc.)
  session.activate_session()
  db_utils.db_session_commit()

  return session
