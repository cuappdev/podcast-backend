from tests.test_case import *
from app.pcasts.dao import subscriptions_dao
from app.pcasts.controllers import series_subscriptions_controller

class SubscriptionsTestCase(TestCase):

  def setUp(self):
    super(SubscriptionsTestCase, self).setUp()
    Subscription.query.delete() # delete all existing subscriptions
    db_session_commit()

  def test_get_subscriptions(self):
    assert not subscriptions_dao.get_user_subscriptions(1)

  def test_create_subscription(self):
    response = self.app.post('api/v1/subscriptions/1211520413/')
    print(response.data)

if __name__ == '__main__':
  unittest.main()
