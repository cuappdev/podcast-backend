import json
from . import *

class ListeningHistoryDismissController(AppDevController):

  def get_path(self):
    return '/history/listening/dismiss/<episode_id>/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    episode_id = request.view_args['episode_id']
    dismissed = True
    if 'dismissed' in request.args:
      if request.args['dismissed'].lower() == 'false':
        dismissed = False
      elif request.args['dismissed'].lower() == 'true':
        dismissed = True
    listening_histories_dao.\
      update_listening_history_dismissed(episode_id, user.id, dismissed)
    app.logger.info({
        'user': user.username,
        'episode_id': episode_id,
        'message': 'listening history dismissed value set {}'.format(dismissed)
    })
    return dict()
