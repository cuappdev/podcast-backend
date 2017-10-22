from app.pcasts.utils import google_utils
from . import *

class GoogleSignInController(AppDevController):

  def get_path(self):
    return '/users/google_sign_in/'

  def get_methods(self):
    return ['POST']

  def content(self, **kwargs):
    token = request.args['access_token']
    google_user_info = google_utils.get_me(token)

    user, is_new_user = \
        users_dao.get_or_create_user_from_google_creds(google_user_info)
    session = sessions_dao.get_or_create_session_and_activate(user.id)

    return {
        'user': user_schema.dump(user).data,
        'session': session_schema.dump(session).data,
        'is_new_user': is_new_user
    }
