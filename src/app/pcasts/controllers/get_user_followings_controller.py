from . import *

class GetUserFollowingsController(AppDevController):

  def get_path(self):
    return '/followings/show/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = request.view_args['user_id']
    followings = followings_dao.get_followings(user_id)

    return {'followings': following_schema.dump(f) for f in followings}
