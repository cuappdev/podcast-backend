import json
from app.pcasts.utils import google_utils
from . import *

class GoogleSignInController(AppDevController):

  def get_path(self):
    return '/users/google_sign_in/'

  def get_methods(self):
    return ['POST']

  def content(self, **kwargs):
    body = request.data
    body_json = json.loads(body)
    google_user_info = google_utils.get_me(body_json['access_token'])

    user, is_new_user = \
        users_dao.get_or_create_user_from_google_creds(google_user_info)
    session = sessions_dao.get_or_create_session_and_activate(user.id)
    app.logger.info({
        'user': user.username,
        'platform': 'google',
        'message': 'session created successfully'
    })

    return {
        'user': user_schema.dump(user).data,
        'session': session_schema.dump(session).data,
        'is_new_user': is_new_user
    }
