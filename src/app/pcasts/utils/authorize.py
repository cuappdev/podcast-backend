import datetime
import os
from functools import wraps
from flask import request, jsonify
from app import app
from app import constants
from app.pcasts.dao import users_dao

def authorize(f):
  @wraps(f)
  def authorization_decorator(*args, **kwargs):
    auth_header = request.headers.get('Authorization')
    if auth_header is None:
      # Here because old tests depend on this. the goal is to port them and
      # remove these
      if app.config['TESTING']:
          test_user = users_dao.\
            get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1)
          if test_user:
            return f(user=test_user, *args, **kwargs)
          else:
            raise Exception('Test user not found')
      raise Exception('Authorization header is not included!')

    session_token = auth_header.replace('Bearer', '').strip()
    if session_token is None or not session_token:
      raise Exception('Session code not found!')
    user = users_dao.get_user_by_valid_session(session_token)
    if user is None:
      raise Exception('Session invalid!')

    return f(user=user, *args, **kwargs)
  return authorization_decorator
