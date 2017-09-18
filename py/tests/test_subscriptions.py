from tests.test_case import *
from app.pcasts.dao import subscriptions_dao, series_dao
from app.pcasts.controllers import series_subscriptions_controller

class SubscriptionsTestCase(TestCase):

  def setUp(self):
    super(SubscriptionsTestCase, self).setUp()
    Subscription.query.delete() # delete all existing subscriptions
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  def test_get_subscriptions(self):
    assert not subscriptions_dao.get_user_subscriptions(1)

  def test_create_subscription(self):
    response = self.app.post('api/v1/subscriptions/1211520413/')

  def test_subscribers_count(self):
    series_id = '1211520413'
    series = Series.query.filter(Series.id == series_id).first()
    self.assertEquals(series.subscribers_count, 0)

    self.app.post('api/v1/subscriptions/{}/'.format(series_id))
    series = Series.query.filter(Series.id == series_id).first()
    self.assertEquals(series.subscribers_count, 1)

    self.app.delete('api/v1/subscriptions/{}/'.format(series_id))
    series = Series.query.filter(Series.id == series_id).first()
    self.assertEquals(series.subscribers_count, 0)
