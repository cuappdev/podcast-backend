import json
from . import *

class ListeningHistoryController(AppDevController):

  def get_path(self):
    return '/history/listening/'

  def get_methods(self):
    return ['GET', 'POST']

  @authorize
  def content(self, **kwargs):
    if request.method == 'GET':
      user = kwargs.get('user')
      offset = int(request.args['offset'])
      max_hs = int(request.args['max'])
      dismissed = None
      if 'dismissed' in request.args:
        if request.args['dismissed'].lower() == 'false':
          dismissed = False
        elif request.args['dismissed'].lower() == 'true':
          dismissed = True
      histories = \
        listening_histories_dao.get_listening_history(user, max_hs, offset,
                                                      dismissed=dismissed)
      return {'listening_histories': [listening_history_schema.dump(h).data
                                      for h in histories]}
    elif request.method == 'POST':
      user = kwargs.get('user')
      episode_info = {long(k): v for k, v in json.loads(request.data).items()}
      listening_histories_dao \
        .create_or_update_listening_histories(episode_info, user)
      app.logger.info({
          'user': user.username,
          'episode_info': episode_info,
          'message': 'listening history batch created or updated'
      })
      return dict()
