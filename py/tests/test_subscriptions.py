from tests.test_case import *
from app.pcasts.dao import subscriptions_dao # pylint: disable=C0413

class SubscriptionsTestCase(TestCase):

  def setUp(self):
    super(SubscriptionsTestCase, self).setUp()
    Subscription.query.delete() # delete all existing subscriptions
    db_session_commit()

  def test_get_subscriptions(self):
    assert not subscriptions_dao.get_user_subscriptions(1)

if __name__ == '__main__':
  unittest.main()
