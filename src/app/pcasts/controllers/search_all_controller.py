from . import *

class SearchAllController(AppDevController):

  def get_path(self):
    return '/search/all/<query>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    search_query = request.view_args['query']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_episodes = episodes_dao.\
        search_episode(search_query, offset, max_search)
    possible_users = users_dao.\
        search_users(search_query, offset, max_search)
    possible_series = series_dao.\
        search_series(search_query, offset, max_search)
    return {'users': \
                [user_schema.dump(u).data for u in possible_users],
            'series': \
                [series_schema.dump(s).data for s in possible_series],
            'episodes': \
                [episode_schema.dump(e).data for e in possible_episodes],}
