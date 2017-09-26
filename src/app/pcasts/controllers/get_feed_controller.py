import time
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
    return '/feed/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    maxtime = request.args['maxtime']
    page_size = int(request.args['page_size'])

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
    """
    Given a list of sources from which to generate a feed page, this function
      merges the lists together in descending order of when the elements were
      created.

    Args:
        sources (list): A list where each element is a list of models containing
          information for a feed element. Each sublist must already be sorted
          by descending time.
        contexts (list): A list where contexts[i] is the `FeedContexts` type
          of sources[i].
        page_size (int): Desired length of feed page.

    Returns:
        list: A list of `FeedElements` of length
          min(page_size, sum([len(source) for source in sources])) that is
          sorted by descending time created.
    """
    sources = [list(reversed(s)) for s in sources]  # reverse so we can .pop()
    result, heap, count = [], [], 0
    for source_idx, s in enumerate(sources):
      if s:
        item = s.pop()
        heapq.heappush(heap, (-time.mktime(item.created_at.timetuple()),
                              item, source_idx))
    while len(heap) > 0 and count < page_size:  #pylint: disable=C1801
      _, item, source_idx = heapq.heappop(heap)
      result.append(FeedElement(item, contexts[source_idx]))
      if sources[source_idx]:
        raw_content = sources[source_idx].pop()
        heapq.heappush(heap, (-time.mktime(raw_content.created_at.timetuple()),
                              raw_content, source_idx))
      count += 1
    return result

class FeedElement(object):

  def __init__(self, raw_content, context):
    self.context = context
    self.time = int(time.mktime(raw_content.created_at.timetuple()))
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
    context_to_schemas = {
        FeedContexts.FOLLOWING_RECOMMENDATION: (user_schema, episode_schema),
        FeedContexts.FOLLOWING_SUBSCRIPTION: (user_schema, series_schema),
        FeedContexts.NEW_SUBSCRIBED_EPISODE: (series_schema, episode_schema),
    }
    supplier_schema, content_schema = context_to_schemas[self.context]
    return {
        "context": self.context,
        "time": self.time,
        "context_supplier": supplier_schema.dump(self.context_supplier).data,
        "content": content_schema.dump(self.content).data
    }
