from . import *

class UpdateSessionController(AppDevController):

  def get_path(self):
    return '/sessions/update/'

  def get_methods(self):
    return ['POST']

  def content(self, **kwargs):
    update_token = request.args['update_token']
    renewed_session = sessions_dao.update_session(update_token)
    return {'session': session_schema.dump(renewed_session).data}
