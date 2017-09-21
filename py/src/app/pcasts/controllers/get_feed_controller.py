import heapq
from . import *

class Enum(set):
  def __getattr__(self, name):
    if name in self:
      return name
    raise AttributeError

FeedContexts = Enum(['FOLLOWING_RECOMMENDATION',
                     'FOLLOWING_SUBSCRIPTION',
                     'NEW_SUBSCRIBED_EPISODE'])

class GetFeedController(AppDevController):

  def get_path(self):
    return '/feed'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    maxtime = kwargs.get('time')
    page_size = kwargs.get('page_size')

    following_recommendations = \
      followings_dao.get_following_recommendations(user.id, maxtime, page_size)
    following_subscriptions = \
      followings_dao.get_following_subscriptions(user.id, maxtime, page_size)
    new_subscribed_episodes = \
      subscriptions_dao.get_new_subscribed_episodes(user.id, maxtime, page_size)

    feed = self.merge_sorted_feed_sources(
        [
            following_recommendations,
            following_subscriptions,
            new_subscribed_episodes
        ],
        [
            FeedContexts.FOLLOWING_RECOMMENDATION,
            FeedContexts.FOLLOWING_SUBSCRIPTION,
            FeedContexts.NEW_SUBSCRIBED_EPISODE
        ],
        page_size)

    return {'feed': [f.serialize() for f in feed]}

  @classmethod
  def merge_sorted_feed_sources(cls, sources, contexts, page_size):
    sources = [reversed(s) for s in sources]  # reverse so we can just .pop()
    result, heap, count = [], [], 0
    for source_idx, s in sources:
      if s:
        item = s.pop()
        heapq.heappush(heap, (-item.created_at, item, source_idx))
    while len(heap) > 0 and count < page_size:  #pylint: disable=C1801
      _, item, source_idx = heapq.heappop(heap)
      result.append(FeedElement(item, contexts[source_idx]))
      if sources[source_idx]:
        raw_content = sources[source_idx].pop()
        heapq.heapush(heap, (-raw_content.created_at, raw_content, source_idx))
      count += 1
    return result

  class FeedElement(object):

    def __init__(self, raw_content, context):
      self.context = context
      self.time = raw_content.created_at
      if context == FeedContexts.FOLLOWING_RECOMMENDATION:
        self.context_supplier = raw_content.user
        self.content = raw_content.episode
      elif context == FeedContexts.FOLLOWING_SUBSCRIPTION:
        self.context_supplier = raw_content.user
        self.content = raw_content.series
      elif context == FeedContexts.NEW_SUBSCRIBED_EPISODE:
        self.context_supplier = raw_content.series
        self.content = raw_content

    def serialize(self):
      json = {
          "context": self.context,
          "time": self.time
      }
      if context == FeedContexts.FOLLOWING_RECOMMENDATION:
        supplier_schema = user_schema
        content_schema = episode_schema
      elif context == FeedContexts.FOLLOWING_SUBSCRIPTION:
        supplier_schema = user_schema
        content_schema = series_schema
      elif context == FeedContexts.NEW_SUBSCRIBED_EPISODE:
        supplier_schema = series_schema
        content_schema = episode_schema
      json['context_supplier'] = \
        supplier_schema.dump(self.context_supplier).data
      json['content'] = content_schema.dump(self.content).data
