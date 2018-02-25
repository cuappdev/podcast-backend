from . import *

class DiscoverSeriesForTopicController(AppDevController):

  def get_path(self):
    return '/discover/series/topic/<topic_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    topic_id = request.view_args['topic_id']
    offset = request.args['offset']
    max_num = request.args['max']
    series = discover_dao.get_series_for_topic(topic_id, user_id, offset, max_num)
    return {'series': [series_schema.dump(s).data for s in series]}
