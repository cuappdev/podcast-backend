from . import *

class UpdateUsernameController(AppDevController):

  def get_path(self):
    return '/users/change_username/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    old_name = user.username
    new_name = request.args['username']
    updated_user = users_dao.change_user_name(user.id, new_name)
    app.logger.info(
        '(id: %s, old: %s, new: %s, episode_id: %s) username updated',
        user.id, user.username, old_name, new_name
    )
    return {'user': user_schema.dump(updated_user).data}
