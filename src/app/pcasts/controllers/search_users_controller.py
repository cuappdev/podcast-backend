from . import *

class SearchUsersController(AppDevController):

  def get_path(self):
    return '/search/users/<query>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_name = request.view_args['query']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_users = users_dao.\
        search_users(user_name, offset, max_search)
    return {'users': \
        [user_schema.dump(u).data for u in possible_users]}
