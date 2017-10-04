from . import *

class GetUserByIdController(AppDevController):

  def get_path(self):
    return '/users/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    requested_user_id = request.view_args['user_id']
    requested_user = users_dao.get_user_by_id(user_id, requested_user_id)

    return {'user': user_schema.dump(requested_user).data}
