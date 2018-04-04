import json
from . import *

class IgnoreFacebookFriendsController(AppDevController):

  def get_path(self):
    return '/users/facebook/friends/ignore/<ignored_fb_id>/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    ignored_fb_id = request.view_args['ignored_fb_id']
    success = users_dao.add_ignored_friend(ignored_fb_id, user.id)
    return { 'success': success }
