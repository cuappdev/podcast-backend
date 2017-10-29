from . import *

class DiscoverEpisodesController(AppDevController):

  def get_path(self):
    return '/discover/episodes/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    offset = int(request.args['offset'])
    max_es = int(request.args['max'])

    top_eps = episode_dao.\
      get_top_episodes_by_recommenders(offset, max_hs, user_id)

    return {'episodes': [episode_schema.dump(e).data for e in top_eps]}
