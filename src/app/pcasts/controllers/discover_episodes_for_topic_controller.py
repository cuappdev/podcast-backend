from . import *

class DiscoverEpisodesForTopicController(AppDevController):

  def get_path(self):
    return '/discover/episodes/topic/<topic_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = kwargs.get('user').id
    topic_id = request.view_args['topic_id']
    offset = request.args['offset']
    max_search = request.args['max']
    episodes = discover_dao.get_episodes_for_topic(topic_id, user_id, offset, \
        max_search)
    return {'episodes': [episode_schema.dump(e).data for e in episodes]}
