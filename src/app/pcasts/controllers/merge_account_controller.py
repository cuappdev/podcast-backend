from app.pcasts.utils import facebook_utils, google_utils
from . import *

class MergeAccountController(AppDevController):

  def get_path(self):
    return '/users/merge/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    token = request.args['access_token']
    new_platform = request.args['platform']

    if new_platform == "facebook":
      facebook_info = facebook_utils.get_me(token)
      user = users_dao.add_facebook_login(user, facebook_info)
    elif new_platform == "google":
      google_info = google_utils.get_me(token)
      user = users_dao.add_google_login(user, google_info)
    else:
      raise Exception('Platform {} not supported yet'.format(new_platform))

    app.logger.info({
        'user': user.username,
        'new_platform': new_platform,
        'message': 'account merged'
    })
    return {
        'user': user_schema.dump(user).data
    }
