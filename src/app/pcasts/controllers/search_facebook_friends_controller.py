import json
from app.pcasts.utils import facebook_utils
from . import *

class SearchFacebookFriends(AppDevController):

  def get_path(self):
    return '/search/facebook/friends/<query>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    access_token = request.headers.get('access_token')
    offset = request.args['offset']
    max_search = request.args['max']
    query = request.view_args['query']
    user = kwargs.get('user')

    fb_friend_ids = facebook_utils.get_friend_ids(user.facebook_id, \
        access_token)
    users = users_dao.search_facebook_users(fb_friend_ids, user.id, \
        offset, max_search, query)

    return {
        'users': [user_schema.dump(u).data for u in users]
    }
