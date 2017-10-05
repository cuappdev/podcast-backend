from . import *

class GetUserFollowersController(AppDevController):

  def get_path(self):
    return '/followers/show/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = request.view_args['user_id']
    followings = followings_dao.get_followers(user_id)

    return {'followers': [following_schema.dump(f).data for f in followings]}
