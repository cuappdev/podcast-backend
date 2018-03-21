import json
from app.pcasts.utils import facebook_utils
from . import *

class GetFacebookFriends(AppDevController):

  def get_path(self):
    return '/users/facebook/friends/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    access_token = request.headers.get('AccessToken')
    offset = request.args['offset']
    max_search = request.args['max']
    return_following = request.args.get('return_following', default="true")
    user = kwargs.get('user')
    return_following = True if return_following == 'true' else False
    fb_friend_ids = facebook_utils.get_friend_ids(user.facebook_id, \
        access_token)
    users = users_dao.get_users_by_facebook_ids(fb_friend_ids, user.id, \
        offset, max_search, return_following)

    return {
        'users': [user_schema.dump(u).data for u in users]
    }
