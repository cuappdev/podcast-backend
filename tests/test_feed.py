import time
import json
from tests.test_case import *
from app.pcasts.dao import subscriptions_dao, series_dao, recommendations_dao, \
  followings_dao, episodes_dao, bookmarks_dao, shares_dao
from app.pcasts.dao.feed_dao import FeedContexts

class FeedTestCase(TestCase):

  def setUp(self):
    super(FeedTestCase, self).setUp()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    Following.query.delete()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Bookmark.query.delete()
    Share.query.delete()
    db_session_commit()

  def tearDown(self):
    super(FeedTestCase, self).tearDown()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    Following.query.delete()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Bookmark.query.delete()
    Share.query.delete()
    db_session_commit()

  def test_standard_feed(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid)

    episode_title2 = 'Battle of the camera drones'
    episode2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid)

    followings_dao.create_following(self.user1.uid, self.user2.uid)
    recommendations_dao\
      .create_or_update_recommendation(episode1.id, self.user2.user)
    time.sleep(1)
    recommendations_dao.\
      create_or_update_recommendation(episode2.id, self.user1.user)
    time.sleep(1)
    subscriptions_dao.create_subscription(self.user2.uid, '1211520413')
    subscriptions_dao.create_subscription(self.user1.uid, '1211520413')
    shares_dao.create_share(self.user2.uid, self.user1.uid, episode1.id)

    maxtime = int(time.time())
    raw_response = self.user1.get('api/v1/feed/?maxtime={}&page_size=7'
                                  .format(maxtime)).data
    response = json.loads(raw_response)
    self.assertEqual(len(response['data']['feed']), 7)
    self.assertEqual([item['context'] for item in response['data']['feed']],
                     [FeedContexts.SHARED_EPISODE,
                      FeedContexts.FOLLOWING_SUBSCRIPTION,
                      FeedContexts.FOLLOWING_SUBSCRIPTION,
                      FeedContexts.FOLLOWING_RECOMMENDATION,
                      FeedContexts.FOLLOWING_RECOMMENDATION,
                      FeedContexts.NEW_SUBSCRIBED_EPISODE,
                      FeedContexts.NEW_SUBSCRIBED_EPISODE])
    for item in response['data']['feed']:
      self.assertEqual(type(item['time']), int)
      self.assertTrue(item['time'] <= maxtime)
      if item['context'] == FeedContexts.FOLLOWING_SUBSCRIPTION:
        self.assertTrue(item['context_supplier']['is_following']
                        or item['context_supplier']['id'] == self.user1.uid)
      elif item['context'] == FeedContexts.FOLLOWING_RECOMMENDATION:
        self.assertTrue(item['context_supplier']['is_following']
                        or item['context_supplier']['id'] == self.user1.uid)
        self.assertFalse(item['content']['series']['is_subscribed'])
      elif item['context'] == FeedContexts.NEW_SUBSCRIBED_EPISODE:
        self.assertTrue(item['context_supplier']['is_subscribed'])
        self.assertTrue('last_updated' in item['context_supplier'])
      elif item['context'] == FeedContexts.SHARED_EPISODE:
        self.assertTrue(item['context_supplier']['is_following'])

    # Ensure that is_subscribed updates
    subscriptions_dao.create_subscription(self.user1.uid, episode1.series_id)

    raw_response = self.user1.get('api/v1/feed/?maxtime={}&page_size=7'
                                  .format(maxtime)).data
    response = json.loads(raw_response)
    for item in response['data']['feed']:
      self.assertEqual(type(item['time']), int)
      self.assertTrue(item['time'] <= maxtime)
      if item['context'] == FeedContexts.FOLLOWING_SUBSCRIPTION:
        self.assertTrue(item['context_supplier']['is_following']
                        or item['context_supplier']['id'] == self.user1.uid)
      elif item['context'] == FeedContexts.FOLLOWING_RECOMMENDATION:
        self.assertTrue(item['context_supplier']['is_following']
                        or item['context_supplier']['id'] == self.user1.uid)
        self.assertTrue(item['content']['series']['is_subscribed'])
      elif item['context'] == FeedContexts.NEW_SUBSCRIBED_EPISODE:
        self.assertTrue(item['context_supplier']['is_subscribed'])
        self.assertTrue('last_updated' in item['context_supplier'])
      elif item['context'] == FeedContexts.SHARED_EPISODE:
        self.assertTrue(item['context_supplier']['is_following'])

    # Ensure is_bookmarked is working correctly with new subscribed episodes
    subscribed_ep = episodes_dao.get_episodes_by_series('1211520413', \
        0, 3, self.user1.uid)[0]
    bookmarks_dao.create_bookmark(subscribed_ep.id, self.user1.user)
    raw_response = self.user1.get('api/v1/feed/?maxtime={}&page_size=7'
                                  .format(maxtime)).data
    response = json.loads(raw_response)

    first = response['data']['feed'][5]['content']['is_bookmarked']
    second = response['data']['feed'][6]['content']['is_bookmarked']
    self.assertTrue((first or second) and not (first and second))
