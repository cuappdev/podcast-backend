from . import *

class GetListeningHistoryController(AppDevController):

  def get_path(self):
    return '/history/listening/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    offset = int(request.args['offset'])
    max_hs = int(request.args['max'])

    histories = [
        listening_history_schema.dump(h).data
        for h in listening_histories_dao.get_listening_history(
            user,
            max_hs,
            offset
        )
    ]

    return {'listening_histories': histories}
