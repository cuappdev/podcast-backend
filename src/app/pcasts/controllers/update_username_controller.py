from . import *

class UpdateUsernameController(AppDevController):

  def get_path(self):
    return '/users/change_username/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    new_name = request.args['username']
    updated_user = users_dao.change_user_name(user_id, new_name)
    return {'user': user_schema.dump(updated_user).data}
