from . import *

class DiscoverSeriesController(AppDevController):

  def get_path(self):
    return '/discover/series/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    offset = int(request.args['offset'])
    max_ss = int(request.args['max'])

    top_series = get_top_series_by_subscribers(offset, max_ss, user_id)

    return {'series':[series_schema.dump(s).data for s in top_series]}
