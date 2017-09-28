from . import *

class SearchEpisodeController(AppDevController):

  def get_path(self):
    return '/search/episodes/<query>'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    search_title = request.view_args['query']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_episodes = episodes_dao.\
        search_episode(search_title, offset, max_search)
    return {'episodes': \
        [episode_schema.dump(e).data for e in possible_episodes]}
