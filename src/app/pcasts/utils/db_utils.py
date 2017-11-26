from . import *

DB_COMMIT_ERROR_MESSAGE = 'Failure to complete DB transaction'

def commit_models(model_lst):
  try:
    for m in model_lst:
      db.session.add(m)
    db.session.commit()
    return model_lst
  except Exception as e:
    app.logger.info({
        'message': DB_COMMIT_ERROR_MESSAGE,
        'error': e
    })
    db.session.rollback()
    raise Exception(DB_COMMIT_ERROR_MESSAGE)

def commit_model(m):
  return commit_models([m])[0]

def delete_models(model_lst):
  try:
    for m in model_lst:
      db.session.delete(m)
    db.session.commit()
    return model_lst
  except Exception as e:
    app.logger.info({
        'message': DB_COMMIT_ERROR_MESSAGE,
        'error': e
    })
    db.session.rollback()
    raise Exception(DB_COMMIT_ERROR_MESSAGE)

def delete_model(m):
  return delete_models([m])[0]

def db_session_commit():
  try:
    db.session.commit()
  except Exception as e:
    app.logger.info({
        'message': DB_COMMIT_ERROR_MESSAGE,
        'error': e
    })
    db.session.rollback()
    raise Exception(DB_COMMIT_ERROR_MESSAGE)

def db_session_expunge_all():
  db.session.expunge_all()
