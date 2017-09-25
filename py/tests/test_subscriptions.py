from flask import json
from tests.test_case import *
from app import constants
from app.pcasts.dao import subscriptions_dao, series_dao, users_dao
from app.pcasts.controllers import series_subscriptions_controller

class SubscriptionsTestCase(TestCase):

  def setUp(self):
    super(SubscriptionsTestCase, self).setUp()
    Subscription.query.delete()
    series_dao.clear_all_subscriber_counts()
    db_session_commit()

  def test_create_subscription(self):
    series_id1 = '1211520413'
    series_id2 = '1210383304'
    series = Series.query.filter(Series.id == series_id1).first()
    self.assertEquals(series.subscribers_count, 0)

    self.app.post('api/v1/subscriptions/{}/'.format(series_id1))
    series = Series.query.filter(Series.id == series_id1).first()
    self.assertEquals(series.subscribers_count, 1)

    with self.assertRaises(Exception):
      self.app.post('api/v1/subscriptions/{}/'.format(series_id1))

    self.app.post('api/v1/subscriptions/{}/'.format(series_id2))
    series = Series.query.filter(Series.id == series_id1).first()
    self.assertEquals(series.subscribers_count, 1)

  def test_get_subscriptions(self):
    series_id1 = '1211520413'
    series_id2 = '1210383304'

    response = self.app.get('api/v1/subscriptions/{}/?offset={}&max={}'.
                            format(series_id1, 0, 1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 0)

    self.app.post('api/v1/subscriptions/{}/'.format(series_id1))
    self.app.post('api/v1/subscriptions/{}/'.format(series_id2))

    response = self.app.get('api/v1/subscriptions/{}/?offset={}&max={}'.
                            format(series_id1, 0, 1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 1)
    self.assertEquals(
        data['data']['subscriptions'][0]['series_id'], int(series_id1)
    )
    self.assertEquals(
        data['data']['subscriptions'][0]['user']['google_id'],
        constants.TEST_USER_GOOGLE_ID1
    )

    response = self.app.get('api/v1/subscriptions/{}/?offset={}&max={}'.
                            format(series_id1, 0, 0))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 0)

    response = self.app.get('api/v1/subscriptions/{}/?offset={}&max={}'.
                            format(series_id2, 0, 1))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 1)
    self.assertEquals(
        data['data']['subscriptions'][0]['series_id'], int(series_id2)
    )
    self.assertEquals(
        data['data']['subscriptions'][0]['user']['google_id'],
        constants.TEST_USER_GOOGLE_ID1
    )

  def test_get_user_subscriptions(self):
    series_id1 = '1211520413'
    series_id2 = '1210383304'
    self.app.post('api/v1/subscriptions/{}/'.format(series_id1))
    self.app.post('api/v1/subscriptions/{}/'.format(series_id2))

    test_user_id = users_dao.\
        get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    response = self.app.\
        get('api/v1/subscriptions/users/{}/'.format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 2)
    self.assertTrue(
        data['data']['subscriptions'][0]['series_id'] == int(series_id1) or
        data['data']['subscriptions'][1]['series_id'] == int(series_id1)
    )
    self.assertTrue(
        data['data']['subscriptions'][0]['series_id'] == int(series_id2) or
        data['data']['subscriptions'][1]['series_id'] == int(series_id2)
    )

  def test_delete_user_subscriptions(self):
    series_id1 = '1211520413'
    series_id2 = '1210383304'
    self.app.post('api/v1/subscriptions/{}/'.format(series_id1))
    self.app.post('api/v1/subscriptions/{}/'.format(series_id2))

    test_user_id = users_dao.\
      get_user_by_google_id(constants.TEST_USER_GOOGLE_ID1).id

    response = self.app.\
        get('api/v1/subscriptions/users/{}/'.format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 2)

    self.app.delete('api/v1/subscriptions/{}/'.format(series_id1))
    self.app.delete('api/v1/subscriptions/{}/'.format(series_id2))

    response = self.app.\
        get('api/v1/subscriptions/users/{}/'.format(test_user_id))
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['subscriptions']), 0)

    with self.assertRaises(Exception):
      self.app.delete('api/v1/subscriptions/{}/'.format(series_id1))

    with self.assertRaises(Exception):
      self.app.delete('api/v1/subscriptions/{}/'.format(series_id2))

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

  def test_is_subscribed(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    series_id = '1211520413'
    series = series_dao.get_series(series_id, user.id)
    self.assertFalse(series.is_subscribed)

    self.app.post('api/v1/subscriptions/{}/'.format(series_id))
    series = series_dao.get_series(series_id, user.id)
    self.assertTrue(series.is_subscribed)

    self.app.delete('api/v1/subscriptions/{}/'.format(series_id))
    series = series_dao.get_series(series_id, user.id)
    self.assertFalse(series.is_subscribed)
