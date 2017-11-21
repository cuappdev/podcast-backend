import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app import constants # pylint: disable=C0413

class ListeningHistoryTestCase(TestCase):

  def __init__(self, *args, **kwargs):
    super(ListeningHistoryTestCase, self).__init__(*args, **kwargs)
    self.changed_episodes = {}
    self.old_durations = {}

  def setUp(self):
    super(ListeningHistoryTestCase, self).setUp()
    ListeningHistory.query.delete()
    db_session_commit()

  def tearDown(self):
    super(ListeningHistoryTestCase, self).tearDown()
    ListeningHistory.query.delete()
    episodes = \
      Episode.query.filter(Episode.id.in_(self.changed_episodes.keys())).all()
    for episode in episodes:
      episode.duration = self.changed_episodes[episode.id]
      episode.real_duration_written = False
    self.changed_episodes = {}
    db_session_commit()

  def generate_listening_histories(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode1 = episodes_dao.get_episode_by_title(episode_title1, self.user1.uid)
    self.changed_episodes[episode1.id] = episode1.duration
    episode_id1 = episode1.id

    episode_title2 = 'Battle of the camera drones'
    episode2 = episodes_dao.get_episode_by_title(episode_title2, self.user1.uid)
    self.changed_episodes[episode2.id] = episode2.duration
    episode_id2 = episode2.id

    data = {
        episode_id1: {
            'percentage_listened': 0.5,
            'current_progress': 0.5
        },
        episode_id2: {
            'percentage_listened': 0.9,
            'current_progress': 0.11,
            'real_duration': "9000:01"
        }
    }

    listening_history = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    self.assertIsNone(listening_history)

    self.user1.post('api/v1/history/listening/', data=json.dumps(data))

    return episode_id1, episode_id2, self.user1

  def test_create_listening_histories(self):
    episode_id1, episode_id2, user = self.generate_listening_histories()

    listening_history1 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    episode1 = episodes_dao.get_episode(episode_id1, user.uid)
    self.assertEquals(listening_history1.episode_id, int(episode_id1))
    self.assertEquals(listening_history1.percentage_listened, 0.5)
    self.assertEquals(listening_history1.current_progress, 0.5)
    self.assertFalse(episode1.real_duration_written)
    self.assertEquals(episode1.duration, self.changed_episodes[episode1.id])
    self.assertEquals(episode1.current_progress, 0.5)

    listening_history2 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id2).first()
    episode2 = episodes_dao.get_episode(episode_id2, user.uid)
    self.assertEquals(listening_history2.episode_id, int(episode_id2))
    self.assertEquals(listening_history2.percentage_listened, 0.9)
    self.assertEquals(listening_history2.current_progress, 0.11)
    self.assertTrue(episode2.real_duration_written)
    self.assertEquals(episode2.duration, "9000:01")
    self.assertEquals(episode2.current_progress, 0.11)

  def test_update_listening_histories(self):
    episode_id1, _, user = self.generate_listening_histories()

    listening_history1 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()
    episode1 = episodes_dao.get_episode(episode_id1, user.uid)
    self.assertEquals(listening_history1.episode_id, int(episode_id1))
    self.assertEquals(listening_history1.percentage_listened, 0.5)
    self.assertEquals(listening_history1.current_progress, 0.5)
    self.assertFalse(episode1.real_duration_written)
    self.assertEquals(episode1.duration, self.changed_episodes[episode1.id])
    self.assertEquals(episode1.current_progress, 0.5)

    data = {
        episode_id1: {
            'percentage_listened': 1.2,
            'current_progress': 0.75,
            'real_duration': "9000:01"
        }
    }
    self.user1.post('api/v1/history/listening/', data=json.dumps(data))

    listening_history1 = ListeningHistory.query.\
        filter(ListeningHistory.episode_id == episode_id1).first()

    episode1 = episodes_dao.get_episode(episode_id1, user.uid)

    self.assertEquals(listening_history1.episode_id, int(episode_id1))
    self.assertEquals(listening_history1.percentage_listened, 1.7)
    self.assertEquals(listening_history1.current_progress, 0.75)
    self.assertTrue(episode1.real_duration_written)
    self.assertEquals(episode1.duration, "9000:01")
    self.assertEquals(episode1.current_progress, 0.75)

  def test_get_listening_history(self):
    episode_id1, episode_id2, _ = self.generate_listening_histories()

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
    episode_id1, episode_id2, _ = self.generate_listening_histories()

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
    self.generate_listening_histories()

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 2)

    self.user1.delete('api/v1/history/listening/clear/')

    response = self.user1.get('api/v1/history/listening/?offset=0&max=5')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['listening_histories']), 0)
