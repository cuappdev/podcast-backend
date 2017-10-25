from . import *

class DiscoverSeriesController(AppDevController):

  def get_path(self):
    return '/discover/series/'

  def get_method(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    # TODO
    return {'series':[]}
