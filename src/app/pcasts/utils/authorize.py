import datetime
import os
from functools import wraps
from app import app
from app import constants
from app.pcasts.dao import users_dao
from flask import request, jsonify

def authorize(f):
  @wraps(f)
  def authorization_decorator(*args, **kwargs):
    if app.config['TESTING']:
      test_user = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1)
      if test_user:
        return f(user=test_user, *args, **kwargs)
      else:
        raise Exception('Test user not found')

    auth_header = request.headers.get('Authorization')
    if auth_header is None:
      raise Exception('Authorization header is not included!')

    session_token = auth_header.replace('Bearer', '').strip()
    if session_token is None or not session_token:
      raise Exception('Session code not found!')
    print session_token
    user = users_dao.get_user_by_valid_session(session_token)
    if user is None:
      raise Exception('Session invalid!')

    return f(user=user, *args, **kwargs)
  return authorization_decorator
