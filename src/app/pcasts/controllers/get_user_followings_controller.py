from . import *

class GetUserFollowingsController(AppDevController):

  def get_path(self):
    return '/followings/show/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    my_id = kwargs.get('user').id
    user_id = request.view_args['user_id']
    followings = followings_dao.get_followings(user_id)
    followings_json = [following_schema.dump(f).data for f in followings]
    followings_dao.attach_is_following_to_json(my_id, followings_json)

    return {'followings': followings_json}
