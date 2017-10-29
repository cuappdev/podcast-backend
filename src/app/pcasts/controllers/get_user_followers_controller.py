from . import *

class GetUserFollowersController(AppDevController):

  def get_path(self):
    return '/followers/show/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    my_id = kwargs.get('user').id
    user_id = request.view_args['user_id']
    followers = followings_dao.get_followers(user_id)
    followers_json = [following_schema.dump(f).data for f in followers]
    followings_dao.attach_is_following_to_json(my_id, followers_json)

    return {'followers': followers_json}
