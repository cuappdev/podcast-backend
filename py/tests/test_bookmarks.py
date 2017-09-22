import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app.pcasts.controllers import series_subscriptions_controller

src_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__))) + '/src'
sys.path.append(src_path)

from app import constants # pylint: disable=C0413

class BookmarksTestCase(TestCase):

  def setUp(self):
    super(BookmarksTestCase, self).setUp()
    Bookmark.query.delete()
    db_session_commit()

  def test_create_bookmark(self):
    episode_id1 = '202161'
    episode_id2 = '202162'
    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id1).first()
    self.assertIsNone(bookmark)

    self.app.post('api/v1/bookmarks/{}/'.format(episode_id1))
    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id1).first()
    self.assertEquals(bookmark.episode_id, int(episode_id1))

    with self.assertRaises(Exception):
      self.app.post('api/v1/bookmarks/{}/'.format(episode_id1))

    self.app.post('api/v1/bookmarks/{}/'.format(episode_id2))
    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id2).first()
    self.assertEquals(bookmark.episode_id, int(episode_id2))

    bookmarks = Bookmark.query.all()
    self.assertEquals(len(bookmarks), 2)

  def test_get_bookmarks(self):
    episode_id1 = '202161'
    episode_id2 = '202162'

    response = self.app.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 0)

    self.app.post('api/v1/bookmarks/{}/'.format(episode_id1))
    self.app.post('api/v1/bookmarks/{}/'.format(episode_id2))

    response = self.app.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 2)
    self.assertTrue(
        data['data']['bookmarks'][0]['episode']['id'] == int(episode_id1) or
        data['data']['bookmarks'][1]['episode']['id'] == int(episode_id1)
    )
    self.assertTrue(
        data['data']['bookmarks'][0]['episode']['id'] == int(episode_id2) or
        data['data']['bookmarks'][1]['episode']['id'] == int(episode_id2)
    )

  def test_delete_bookmarks(self):
    episode_id1 = '202161'
    episode_id2 = '202162'

    self.app.post('api/v1/bookmarks/{}/'.format(episode_id1))
    self.app.post('api/v1/bookmarks/{}/'.format(episode_id2))

    response = self.app.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 2)

    self.app.delete('api/v1/bookmarks/{}/'.format(episode_id1))
    self.app.delete('api/v1/bookmarks/{}/'.format(episode_id2))

    response = self.app.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 0)

    with self.assertRaises(Exception):
      self.app.delete('api/v1/bookmarks/{}/'.format(episode_id1))

    with self.assertRaises(Exception):
      self.app.delete('api/v1/bookmarks/{}/'.format(episode_id2))

  def test_is_bookmarked(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    episode_id = '202161'
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertFalse(episode.is_bookmarked)

    self.app.post('api/v1/bookmarks/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertTrue(episode.is_bookmarked)

    self.app.delete('api/v1/bookmarks/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertFalse(episode.is_bookmarked)
