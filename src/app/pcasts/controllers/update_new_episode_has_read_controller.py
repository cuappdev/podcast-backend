import json
from . import *

class UpdateNewEpisodeHasReadController(AppDevController):

  def get_path(self):
    return '/notifications/episodes/read/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    episode_ids = json.loads(request.data)['episodes']
    episode_ids = [int(episode) for episode in episode_ids]
    notifications_dao.update_new_episode_has_read(user.id, episode_ids)
    return {}
