import json
from app.pcasts.utils import facebook_utils
from . import *

class FacebookSignInController(AppDevController):

  def get_path(self):
    return '/users/facebook_sign_in/'

  def get_methods(self):
    return ['POST']

  def content(self, **kwargs):
    body = request.data
    body_json = json.loads(body)
    facebook_info = facebook_utils.get_me(body_json['access_token'])

    user, is_new_user = \
        users_dao.get_or_create_user_from_facebook_creds(facebook_info)
    session = sessions_dao.get_or_create_session_and_activate(user.id)
    app.logger.info(
        '(id: %s, username: %s, platform: %s) session created successfully',
        user.id, user.username, 'facebook'
    )

    return {
        'user': user_schema.dump(user).data,
        'session': session_schema.dump(session).data,
        'is_new_user': is_new_user
    }
