from . import *

class SearchiTunesController(AppDevController):

  def get_path(self):
    return '/search/itunes/<query>/'

  def get_methods(self):
    return ['POST']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    query = request.view_args['query']
    series_from_itunes, new_series_ids = \
      itunes_dao.search_from_itunes(query, user.id)
    app.logger.info({
        'user': user.username,
        'query': query,
        'message': 'itunes search'
    })
    return {
        'series': [series_schema.dump(s).data for s in series_from_itunes],
        'new_series_ids': new_series_ids
    }
