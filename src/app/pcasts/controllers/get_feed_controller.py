from app.pcasts.dao.feed_dao import FeedContexts
from . import *

class GetFeedController(AppDevController):

  def get_path(self):
    return '/feed/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    maxtime = request.args['maxtime']
    page_size = int(request.args['page_size'])

    feed = feed_dao.get_feed(user, maxtime, page_size)
    serialized_feed = [
        feed_dao.attach_fields_to_json(f, self.serialize(f), user) for f in feed
    ]

    return {'feed': serialized_feed}

  def serialize(self, feed_element):
    context_to_schemas = {
        FeedContexts.FOLLOWING_RECOMMENDATION: (user_schema, episode_schema),
        FeedContexts.FOLLOWING_SUBSCRIPTION: (user_schema, series_schema),
        FeedContexts.NEW_SUBSCRIBED_EPISODE: (series_schema, episode_schema),
        FeedContexts.SHARED_EPISODE: (user_schema, episode_schema)
    }
    supplier_schema, content_schema = context_to_schemas[feed_element.context]
    return {
        "context": feed_element.context,
        "time": feed_element.time,
        "context_supplier": supplier_schema.\
            dump(feed_element.context_supplier).data,
        "content": content_schema.dump(feed_element.content).data
    }
