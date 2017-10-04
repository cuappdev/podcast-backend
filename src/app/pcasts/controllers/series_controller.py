from . import *

class SeriesController(AppDevController):

  def get_path(self):
    return '/series/<series_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    series_id = request.view_args['series_id']
    user_id = kwargs.get('user').id

    series = series_dao.get_series(series_id, user_id)
    return {'series': series_schema.dump(series).data}
