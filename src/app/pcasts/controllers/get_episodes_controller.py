from . import *

class GetEpisodesController(AppDevController):

  def get_path(self):
    return '/podcasts/episodes/by_series/<series_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    series_id = request.view_args['series_id']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_episodes = episodes_dao.\
        get_episodes_by_series(series_id, offset, max_search, user.id)
    return {'episodes': \
        [episode_schema.dump(e).data for e in possible_episodes]}
