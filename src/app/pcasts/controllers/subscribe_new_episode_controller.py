from . import *

class SubscribeNewEpisodeController(AppDevController):

  def get_path(self):
    return '/notifications/episodes/<series_id>/'

  def get_methods(self):
    return ['POST', 'DELETE']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    series_id = request.view_args['series_id']
    if request.method == 'POST':
      notifications_dao.create_new_episode_notification(user.id, series_id)
    if request.method == 'DELETE':
      notifications_dao.delete_new_episode_notification(user.id, series_id)
    return {}
