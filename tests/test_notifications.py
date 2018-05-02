from datetime import datetime
import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import notifications_dao
from app import constants


class NotificationsTestCase(TestCase):

  def setUp(self):
    super(NotificationsTestCase, self).setUp()
    Subscription.query.delete()
    ReadNewEpisodeNotification.query.delete()
    db_session_commit()

  def tearDown(self):
    super(NotificationsTestCase, self).tearDown()
    Subscription.query.delete()
    ReadNewEpisodeNotification.query.delete()
    db_session_commit()

  def test_register_for_new_episodes(self):
    series_ids = [s.id for s in Series.query.limit(3).all()]
    # Test unsubscribed
    response = self.user1.post('api/v1/notifications/episodes/{}/'.\
        format(series_ids[0]))
    data = json.loads(response.data)
    self.assertFalse(data['success'])

    # Test subscribed
    self.user1.post('api/v1/subscriptions/{}/'.format(series_ids[0]))
    response = self.user1.post('api/v1/notifications/episodes/{}/'.\
        format(series_ids[0]))
    data = json.loads(response.data)
    self.assertTrue(data['success'])

  def test_get_new_episdoes(self):
    series_ids = [s.id for s in Series.query.limit(3).all()]

    # Not signed up for any notifcations
    response = self.user1.get('api/v1/notifications/episodes/' + \
        '?offset={}&max={}'.format(0,10))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['episodes']) == 0)

    # One new series notifications
    self.user1.post('api/v1/subscriptions/{}/'.format(series_ids[0]))
    self.user1.post('api/v1/notifications/episodes/{}/'.format(series_ids[0]))
    response = self.user1.get('api/v1/notifications/episodes/' + \
        '?offset={}&max={}'.format(0,10))
    data = json.loads(response.data)['data']
    self.assertTrue(len(data['episodes']) <= 10 and len(data['episodes']) > 0)
    prev_date = convert_to_datetime(data['episodes'][0]['pub_date'])

    for episode in data['episodes']:
      self.assertTrue(episode['unread_notifcation'])
      self.assertTrue(convert_to_datetime(episode['pub_date']) <= prev_date)
      prev_date = convert_to_datetime(episode['pub_date'])

    # Multiple new series notifications
    self.user1.post('api/v1/subscriptions/{}/'.format(series_ids[1]))
    self.user1.post('api/v1/notifications/episodes/{}/'.format(series_ids[1]))
    response = self.user1.get('api/v1/notifications/episodes/' + \
        '?offset={}&max={}'.format(2,10))
    data = json.loads(response.data)['data']
    prev_date = convert_to_datetime(data['episodes'][0]['pub_date'])
    self.assertTrue(len(data['episodes']) <= 10 and len(data['episodes']) > 0)
    for episode in data['episodes']:
      self.assertTrue(episode['unread_notifcation'])
      self.assertTrue(convert_to_datetime(episode['pub_date']) <= prev_date)
      prev_date = convert_to_datetime(episode['pub_date'])


  def test_mark_notifications_read(self):
    series_ids = [s.id for s in Series.query.limit(3).all()]
    # One new series notifications
    self.user1.post('api/v1/subscriptions/{}/'.format(series_ids[0]))
    self.user1.post('api/v1/notifications/episodes/{}/'.format(series_ids[0]))
    response = self.user1.get('api/v1/notifications/episodes/' + \
        '?offset={}&max={}'.format(0,10))
    data = json.loads(response.data)['data']
    read_notifications = [episode['id'] for episode in data['episodes']]
    payload = {
        'episodes': read_notifications
    }
    data = json.dumps(payload)
    response = self.user1.post('api/v1/notifications/episodes/read/', data)
    data = json.loads(response.data)
    self.assertTrue(data['success'])

    response = self.user1.get('api/v1/notifications/episodes/' + \
        '?offset={}&max={}'.format(0,10))
    data = json.loads(response.data)['data']
    print response.data
    prev_date = convert_to_datetime(data['episodes'][0]['pub_date'])
    self.assertTrue(len(data['episodes']) <= 10 and len(data['episodes']) > 0)
    for episode in data['episodes']:
      self.assertFalse(episode['unread_notifcation'])
      self.assertTrue(convert_to_datetime(episode['pub_date']) <= prev_date)
      prev_date = convert_to_datetime(episode['pub_date'])




#ISO 8601 offset is not supported by python 2.7. Turns a string into datetime
def convert_to_datetime(string_date):
  return datetime.datetime.strptime(string_date[0:19], '%Y-%m-%dT%H:%M:%S')
