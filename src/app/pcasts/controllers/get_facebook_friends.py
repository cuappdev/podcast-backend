from app.pcasts.utils import facebook_utils
from . import *

class GetFacebookFriends(AppDevController):

  def get_path(self):
    return '/users/facebook/friends/'

  def get_methods(self):
    return ['GET']

  def content(self, **kwargs):
    auth_token = request.args['authtoken']
    user = kwargs.get('user')
    # NOTE: Built based on graph api v2.11 assumes friends only returns a list
    # of id's of people that have the app installed
    fb_friend_ids = facebook_utils.get_friend_ids(auth_token)
    users = get_users_by_facebook_ids(fb_friend_ids, user.id)

    return {
        'users': [user_schema.dump(u).data for u in users]
    }
