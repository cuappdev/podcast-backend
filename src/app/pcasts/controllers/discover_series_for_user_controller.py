from . import *

class DiscoverSeriesForUserController(AppDevController):

  def get_path(self):
    return '/discover/series/user/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    offset = request.args['offset']
    max_num = request.args['max']
    series = discover_dao.get_series_for_user(user_id, offset, max_num)
    return {'series': [series_schema.dump(s).data for s in series]}
