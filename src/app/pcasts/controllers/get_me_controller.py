from . import *

class GetMeController(AppDevController):

  def get_path(self):
    return '/users/me/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    return {'user': user_schema.dump(kwargs.get('user')).data}
