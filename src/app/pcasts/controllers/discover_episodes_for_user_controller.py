from . import *

class DiscoverEpisodesForUserController(AppDevController):

  def get_path(self):
    return '/discover/episodes/user/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    offset = request.args['offset']
    max_num = request.args['max']
    episodes = discover_dao.get_episodes_for_user(user_id, offset, max_num)
    return {'episodes': [episode_schema.dump(e).data for e in episodes]}
