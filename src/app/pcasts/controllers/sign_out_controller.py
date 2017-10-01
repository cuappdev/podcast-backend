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

    if not success:
      raise Exception('There is no active session associated with this user!')

    return dict()
