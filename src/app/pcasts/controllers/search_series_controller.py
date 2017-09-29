from . import *

class SearchSeriesController(AppDevController):

  def get_path(self):
    return '/search/series/<query>'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    search_name = request.view_args['query']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_series = series_dao.\
        search_series(search_name, offset, max_search)
    cleaned_series = []
    for s in possible_series:
      cleaned_series.append({
          "type": "series",
          "id": s.id,
          "title": s.title,
          "country": s.country,
          "author": s.author,
          "imageUrlSm": s.image_url_sm,
          "imageUrlLg": s.image_url_lg,
          "feed_url": s.feed_url,
          "genres": s.genres,
      })
    return {'series': cleaned_series}
