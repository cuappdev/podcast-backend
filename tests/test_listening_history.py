import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app import constants # pylint: disable=C0413

class ListeningHistoryTestCase(TestCase):

  def __init__(self, *args, **kwargs):
    super(ListeningHistoryTestCase, self).__init__(*args, **kwargs)
    self.changed_episodes = []

  def setUp(self):
    super(ListeningHistoryTestCase, self).setUp()
    ListeningHistory.query.delete()
    db_session_commit()

  def tearDown(self):
    super(ListeningHistoryTestCase, self).tearDown()
    ListeningHistory.query.delete()
    for e in self.changed_episodes:
      e.real_duration = None
    db_session_commit()

  def generate_listening_histories(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.get_episode_by_title(episode_title1, user.id).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.get_episode_by_title(episode_title2, user.id).id

    data = {
        episode_id1: {
            'listening_duration': 0.5,
            'time_at': 0.5,
            'real_duration': None
        },
        episode_id2: {
            'listening_duration': 0.9,
            'time_at': 0.11,
            'real_duration': 3670.5
        }
    }

    listening_history = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    self.assertIsNone(listening_history)

    self.app.post('api/v1/history/listening/', data=json.dumps(data))

    return episode_id1, episode_id2, user

  def test_create_listening_histories(self):
    episode_id1, episode_id2, user = self.generate_listening_histories()

    listening_history1 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    episode1 = episodes_dao.get_episode(episode_id1, user.id)
    self.changed_episodes.append(episode1)
    self.assertEquals(listening_history1.episode_id, int(episode_id1))
    self.assertEquals(listening_history1.listening_duration, 0.5)
    self.assertEquals(listening_history1.time_at, 0.5)
    self.assertIsNone(episode1.real_duration)

    listening_history2 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id2).first()
    episode2 = episodes_dao.get_episode(episode_id2, user.id)
    self.changed_episodes.append(episode2)
    self.assertEquals(listening_history2.episode_id, int(episode_id2))
    self.assertEquals(listening_history2.listening_duration, 0.9)
    self.assertEquals(listening_history2.time_at, 0.11)
    self.assertEquals(episode2.real_duration, 3670.5)

  def test_update_listening_histories(self):
    episode_id1, _, user = self.generate_listening_histories()

    listening_history1 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    episode1 = episodes_dao.get_episode(episode_id1, user.id)
    self.assertEquals(listening_history1.episode_id, int(episode_id1))
    self.assertEquals(listening_history1.listening_duration, 0.5)
    self.assertEquals(listening_history1.time_at, 0.5)
    self.assertIsNone(episode1.real_duration)

    data = {
        episode_id1: {
            'listening_duration': 1.2,
            'time_at': 0.75,
            'real_duration': 9000.1
        }
    }
    self.app.post('api/v1/history/listening/', data=json.dumps(data))

    listening_history1 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()

    episode1 = episodes_dao.get_episode(episode_id1, user.id)
    self.changed_episodes.append(episode1)

    self.assertEquals(listening_history1.episode_id, int(episode_id1))
    self.assertEquals(listening_history1.listening_duration, 1.7)
    self.assertEquals(listening_history1.time_at, 0.75)
    self.assertEquals(episode1.real_duration, 9000.1)

  def test_get_listening_history(self):
    episode_id1, episode_id2, _ = self.generate_listening_histories()

    response = self.app.get('api/v1/history/listening/?offset=0&max=5')
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
    episode_id1, episode_id2, _ = self.generate_listening_histories()

    response = self.app.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 2)

    self.app.delete('api/v1/history/listening/{}/'.format(episode_id1))

    response = self.app.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 1)
    self.assertEquals(
        data['data']['listening_histories'][0]['episode_id'],
        episode_id2
    )

  def test_clear_listening_history(self):
    self.generate_listening_histories()

    response = self.app.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 2)

    self.app.delete('api/v1/history/listening/clear/')

    response = self.app.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 0)
