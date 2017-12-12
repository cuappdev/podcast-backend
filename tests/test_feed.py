import time
import json
from tests.test_case import *
from app.pcasts.dao import subscriptions_dao, series_dao, recommendations_dao, \
  followings_dao, episodes_dao, bookmarks_dao
from app.pcasts.controllers.get_feed_controller import FeedContexts

class FeedTestCase(TestCase):

  def setUp(self):
    super(FeedTestCase, self).setUp()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    Following.query.delete()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Bookmark.query.delete()
    db_session_commit()

  def tearDown(self):
    super(FeedTestCase, self).tearDown()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    Following.query.delete()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    Bookmark.query.delete()
    db_session_commit()

  def test_standard_feed(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    followings_dao.create_following(self.user1.uid, self.user2.uid)
    recommendations_dao.create_recommendation(episode_id1, self.user2.user)
    time.sleep(1)
    recommendations_dao.create_recommendation(episode_id2, self.user2.user)
    time.sleep(1)
    subscriptions_dao.create_subscription(self.user2.uid, '1211520413')
    subscriptions_dao.create_subscription(self.user1.uid, '1211520413')

    maxtime = int(time.time())
    raw_response = self.user1.get('api/v1/feed/?maxtime={}&page_size=5'
                                  .format(maxtime)).data
    response = json.loads(raw_response)
    self.assertEqual(len(response['data']['feed']), 5)
    self.assertEqual([item['context'] for item in response['data']['feed']],
                     [FeedContexts.FOLLOWING_SUBSCRIPTION,
                      FeedContexts.FOLLOWING_RECOMMENDATION,
                      FeedContexts.FOLLOWING_RECOMMENDATION,
                      FeedContexts.NEW_SUBSCRIBED_EPISODE,
                      FeedContexts.NEW_SUBSCRIBED_EPISODE])
    for item in response['data']['feed']:
      self.assertEqual(type(item['time']), int)
      self.assertTrue(item['time'] <= maxtime)

    # Ensure is_bookmarked is working correctly with new subscribed episodes
    subscribed_ep = episodes_dao.get_episodes_by_series('1211520413', 0, 3)[0]
    bookmarks_dao.create_bookmark(subscribed_ep.id, self.user1.user)
    raw_response = self.user1.get('api/v1/feed/?maxtime={}&page_size=5'
                                  .format(maxtime)).data
    response = json.loads(raw_response)

    self.assertTrue(response['data']['feed'][3]['content']['is_bookmarked'])
    self.assertFalse(response['data']['feed'][4]['content']['is_bookmarked'])
