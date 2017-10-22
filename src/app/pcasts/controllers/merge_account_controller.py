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

    if platform == "facebook":
      facebook_info = facebook_utils.get_me(token)
      user = users_dao.update_user_platform(user, facebook_info, new_platform)
    elif platform == "google":
      google_info = google_utils.get_me(token)
      user = users_dao.update_user_platform(user, google_info, new_platform)
    else:
      raise Exception("Platform not supported yet")

    return {
      'user': user_schema.dump(user).data
    }
