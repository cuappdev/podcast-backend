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
    body = request.data
    body_json = json.loads(body)
    access_token = body_json['access_token']
    user = kwargs.get('user')

    fb_friend_ids = facebook_utils.get_friend_ids(user.facebook_id, \
        access_token)
    users = users_dao.get_users_by_facebook_ids(fb_friend_ids, user.id)

    return {
        'users': [user_schema.dump(u).data for u in users]
    }
