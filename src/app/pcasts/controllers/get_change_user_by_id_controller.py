from . import *

class GetUserByIdController(AppDevController):

  def get_path(self):
    return '/users/<user_id>/'

  def get_methods(self):
    return ['GET', 'POST']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    requested_user_id = request.view_args['user_id']
    if request.method == 'GET':
      requested_user = users_dao.get_user_by_id(user_id, requested_user_id)
      return {'user': user_schema.dump(requested_user).data}
    if request.method == 'POST':
      new_name = request.args['username']
      old_name = request.view_args['user_id']
      updated_user = users_dao.change_user_name(old_name, new_name)
      return {'user': user_schema.dump(updated_user).data}
