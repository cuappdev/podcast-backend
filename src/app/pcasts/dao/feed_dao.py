import heapq
import time
from app.pcasts.dao import users_dao, episodes_dao, series_dao, shares_dao
from . import *

class Enum(set):
  def __getattr__(self, name):
    if name in self:
      return name
    raise AttributeError

FeedContexts = Enum(['FOLLOWING_RECOMMENDATION',
                     'FOLLOWING_SUBSCRIPTION',
                     'NEW_SUBSCRIBED_EPISODE',
                     'SHARED_EPISODE'])

def get_feed(user, maxtime, page_size):
  following_recommendations = \
    get_following_recommendations(user.id, maxtime, page_size)
  following_subscriptions = \
    get_following_subscriptions(user.id, maxtime, page_size)
  new_subscribed_episodes = \
    get_new_subscribed_episodes(user.id, maxtime, page_size)
  shared_episodes = \
    get_shared_episodes(user.id, maxtime, page_size)

  # TODO - huge hack, fix later
  augmented_episodes = []
  for e in new_subscribed_episodes:
    e.updated_at = e.pub_date
    augmented_episodes.append(e)

  feed = merge_sorted_feed_sources(
      [
          following_recommendations,
          following_subscriptions,
          augmented_episodes,
          shared_episodes
      ],
      [
          FeedContexts.FOLLOWING_RECOMMENDATION,
          FeedContexts.FOLLOWING_SUBSCRIPTION,
          FeedContexts.NEW_SUBSCRIBED_EPISODE,
          FeedContexts.SHARED_EPISODE
      ],
      page_size)

  return feed

def merge_sorted_feed_sources(sources, contexts, page_size):
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
      heapq.heappush(heap, (-time.mktime(item.updated_at.timetuple()),
                            item, source_idx))
  while len(heap) > 0 and count < page_size:  #pylint: disable=C1801
    _, item, source_idx = heapq.heappop(heap)
    result.append(FeedElement(item, contexts[source_idx]))
    if sources[source_idx]:
      raw_content = sources[source_idx].pop()
      heapq.heappush(heap, (-time.mktime(raw_content.updated_at.timetuple()),
                            raw_content, source_idx))
    count += 1
  return result

def attach_fields_to_json(feed_element, feed_element_json, user):
  if feed_element.context == FeedContexts.FOLLOWING_RECOMMENDATION:
    feed_element_json['context_supplier']['is_following'] = users_dao.\
      is_following_user(
          user.id,
          feed_element_json['context_supplier']['id']
      )
    feed_element_json['content']['series']['is_subscribed'] = series_dao.\
      is_subscribed_by_user(
          feed_element_json['content']['series']['id'],
          user.id
      )
    feed_element_json['blurb'] = feed_element.blurb
  elif feed_element.context == FeedContexts.FOLLOWING_SUBSCRIPTION:
    feed_element_json['context_supplier']['is_following'] = users_dao.\
      is_following_user(
          user.id,
          feed_element_json['context_supplier']['id']
      )
  elif feed_element.context == FeedContexts.NEW_SUBSCRIBED_EPISODE:
    pass
  elif feed_element.context == FeedContexts.SHARED_EPISODE:
    feed_element_json['context_supplier']['is_following'] = users_dao.\
      is_following_user(
          user.id,
          feed_element_json['context_supplier']['id']
      )
    feed_element_json['content']['series']['is_subscribed'] = series_dao.\
      is_subscribed_by_user(
          feed_element_json['content']['series']['id'],
          user.id
      )

  return feed_element_json

def get_following_subscriptions(user_id, maxtime, page_size):
  followings = followings_dao.get_followings(user_id)
  following_ids = [f.followed_id for f in followings]
  maxdatetime = datetime.datetime.fromtimestamp(int(maxtime))
  subscriptions = Subscription.query \
    .filter(Subscription.user_id.in_(following_ids),
            Subscription.updated_at <= maxdatetime) \
    .order_by(Subscription.updated_at.desc()) \
    .limit(page_size) \
    .all()
  series = series_dao.\
    get_multiple_series([s.series_id for s in subscriptions], user_id)

  series_id_to_series = {s.id:s for s in series}
  for sub in subscriptions:
    sub.series = series_id_to_series[sub.series_id]

  return subscriptions

def get_following_recommendations(user_id, maxtime, page_size):
  followings = followings_dao.get_followings(user_id)
  following_ids = [f.followed_id for f in followings]
  maxdatetime = datetime.datetime.fromtimestamp(int(maxtime))
  recommendations = Recommendation.query \
    .filter(Recommendation.user_id.in_(following_ids),
            Recommendation.updated_at <= maxdatetime) \
    .order_by(Recommendation.updated_at.desc()) \
    .limit(page_size) \
    .all()
  episodes = episodes_dao.\
    get_episodes([r.episode_id for r in recommendations], user_id)

  episode_id_to_episode = {e.id:e for e in episodes}
  for r in recommendations:
    r.episode = episode_id_to_episode[r.episode_id]

  return recommendations

def get_new_subscribed_episodes(user_id, maxtime, page_size):
  subscriptions = subscriptions_dao.get_user_subscriptions(user_id, user_id)
  series_ids = [s.series_id for s in subscriptions]
  maxdatetime = datetime.datetime.fromtimestamp(int(maxtime))
  episodes = episodes_dao.get_episodes_maxtime(
      user_id,
      series_ids,
      maxdatetime,
      page_size
  )

  # Populate series data
  series = series_dao.get_multiple_series(series_ids, user_id)
  series_map = {s.id: s for s in series}
  for e in episodes:
    e.series = series_map[e.series_id]
  return episodes

def get_shared_episodes(user_id, maxtime, page_size):
  maxdatetime = datetime.datetime.fromtimestamp(int(maxtime))
  shares = Share.query.filter(Share.sharee_id == user_id,
                              Share.updated_at <= maxdatetime) \
           .order_by(Share.updated_at.desc()) \
           .limit(page_size) \
           .all()
  episodes = episodes_dao.\
    get_episodes([s.episode_id for s in shares], user_id)
  episode_id_to_episode = {e.id:e for e in episodes}
  for s in shares:
    s.episode = episode_id_to_episode[s.episode_id]
  return shares

class FeedElement(object):

  def __init__(self, raw_content, context):
    self.context = context
    self.time = int(time.mktime(raw_content.updated_at.timetuple()))
    if context == FeedContexts.FOLLOWING_RECOMMENDATION:
      self.context_supplier = raw_content.user
      self.content = raw_content.episode
      self.blurb = raw_content.blurb
    elif context == FeedContexts.FOLLOWING_SUBSCRIPTION:
      self.context_supplier = raw_content.user
      self.content = raw_content.series
    elif context == FeedContexts.NEW_SUBSCRIBED_EPISODE:
      self.context_supplier = raw_content.series
      self.content = raw_content
    elif context == FeedContexts.SHARED_EPISODE:
      self.context_supplier = raw_content.sharer
      self.content = raw_content.episode
