from . import *

class GetNewEpisodeNotificationController(AppDevController):

  def get_path(self):
    return '/notifications/episodes/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    max_episodes = request.args['max']
    offset = request.args['offset']
    episode_notifications = notifications_dao. \
        new_episode_notifications(user.id, max_episodes, offset)
    return {'episodes': \
        [episode_schema.dump(e).data for e in episode_notifications]}
