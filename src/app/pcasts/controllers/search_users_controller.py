from . import *

class SearchUsersController(AppDevController):

  def get_path(self):
    return '/search/users/<query>'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_name = request.view_args['query']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_users = users_dao.\
        search_users(user_name, offset, max_search)
    cleaned_users = []
    for u in possible_users:
      cleaned_users.append({
          "id": u.id,
          "firstName": u.first_name,
          "lastName": u.last_name,
          "username": u.username,
          "numberFollowers": u.followers_count,
          "numberFollowing": u.followings_count,
          "imageUrl": u.image_url,
      })
    return {'users': cleaned_users}
