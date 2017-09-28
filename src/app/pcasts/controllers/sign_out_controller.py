from . import *

class SignOutController(AppDevController):

  def get_path(self):
    return '/users/sign_out/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    success = sessions_dao.deactivate_session(user.id)
    if success:
      return dict()
    else:
      raise Exception('Issue logging out!')
