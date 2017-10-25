from . import *

class DiscoverEpisodesController(AppDevController):

  def get_path(self):
    return '/discovery/series/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    # TODO
    return {'episodes':[]}
