from . import *


class SeriesForTopicController(AppDevController):

  def get_path(self):
    return '/series/topic/<topic_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    topic_id = request.view_args['topic_id']

    series = discover_dao.get_series_for_topic(topic_id, user_id)

    return {'series': [series_schema.dump(s).data for s in series]}
