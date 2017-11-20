import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app import constants # pylint: disable=C0413

class ListeningHistoryTestCase(TestCase):

  def setUp(self):
    super(ListeningHistoryTestCase, self).setUp()
    ListeningHistory.query.delete()
    db_session_commit()

  def tearDown(self):
    super(ListeningHistoryTestCase, self).tearDown()
    ListeningHistory.query.delete()
    db_session_commit()

  def test_create_listening_history(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    listening_history = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    self.assertIsNone(listening_history)

    self.user1.post('api/v1/history/listening/{}/'.format(episode_id1))
    listening_history = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    self.assertEquals(listening_history.episode_id, int(episode_id1))

  def test_get_listening_history(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 0)

    self.user1.post('api/v1/history/listening/{}/'.format(episode_id1))
    self.user1.post('api/v1/history/listening/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 2)

    self.assertTrue(
        data['data']['listening_histories'][0]['episode_id'] == episode_id1 or
        data['data']['listening_histories'][1]['episode_id'] == episode_id1
    )

    self.assertTrue(
        data['data']['listening_histories'][0]['episode_id'] == episode_id2 or
        data['data']['listening_histories'][1]['episode_id'] == episode_id2
    )

  def test_delete_listening_history(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    self.user1.post('api/v1/history/listening/{}/'.format(episode_id1))
    self.user1.post('api/v1/history/listening/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 2)

    self.user1.delete('api/v1/history/listening/{}/'.format(episode_id1))

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 1)
    self.assertEquals(
        data['data']['listening_histories'][0]['episode_id'],
        episode_id2
    )

  def test_clear_listening_history(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    self.user1.post('api/v1/history/listening/{}/'.format(episode_id1))
    self.user1.post('api/v1/history/listening/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 2)

    self.user1.delete('api/v1/history/listening/clear/')

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 0)
