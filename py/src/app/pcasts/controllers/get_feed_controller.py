import heapq
from . import *

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

    feed = merge_sorted_feed_sources([following_recommendations,
                                      following_subscriptions,
                                      new_subscribed_episodes], page_size)

    #TODO: serialize feed, get context source (you already get the context, look for it)

  @classmethod
  def merge_sorted_feed_sources(cls, sources, page_size):
    result, heap, add_count = [], [], 0
    for i, s in enumerate(sources):
      if len(s) > 0:  #pylint: disable=C1801
        heapq.heappush(heap, (s[0], i))
    next_index_to_add = [1 for _ in range(sources)]
    while len(heap) > 0 and add_count < page_size:  #pylint: disable=C1801
      item, i = heapq.heappop(heap)
      add_count += 1
      result.append(item)
      if next_index_to_add[i] < len(sources[i]):
        heapq.heapush(heap, sources[i][next_index_to_add[i]])
        next_index_to_add[i] += 1
    return result

    class FeedElement(object):
      pass
