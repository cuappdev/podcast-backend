import sys
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app import constants # pylint: disable=C0413

class BookmarksTestCase(TestCase):

  def setUp(self):
    super(BookmarksTestCase, self).setUp()
    Bookmark.query.delete()
    db_session_commit()

  def tearDown(self):
    super(BookmarksTestCase, self).tearDown()
    Bookmark.query.delete()
    db_session_commit()

  def test_create_bookmark(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id1).first()
    self.assertIsNone(bookmark)

    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id1))
    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id1).first()
    self.assertEquals(bookmark.episode_id, int(episode_id1))

    self.assertRaises(
        Exception,
        self.user1.post('api/v1/bookmarks/{}/'.format(episode_id1))
    )

    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id2))
    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id2).first()
    self.assertEquals(bookmark.episode_id, int(episode_id2))

    bookmarks = Bookmark.query.all()
    self.assertEquals(len(bookmarks), 2)

  def test_get_bookmarks(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id1).first()
    self.assertIsNone(bookmark)

    response = self.user1.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 0)

    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id1))
    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/bookmarks/')
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
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao\
        .get_episode_by_title(episode_title2, self.user1.uid).id

    bookmark = Bookmark.query.filter(Bookmark.episode_id == episode_id1).first()
    self.assertIsNone(bookmark)

    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id1))
    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 2)

    self.user1.delete('api/v1/bookmarks/{}/'.format(episode_id1))
    self.user1.delete('api/v1/bookmarks/{}/'.format(episode_id2))

    response = self.user1.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['bookmarks']), 0)

    self.assertRaises(
        Exception,
        self.user1.delete('api/v1/bookmarks/{}/'.format(episode_id1)),
    )

    self.assertRaises(
        Exception,
        self.user1.delete('api/v1/bookmarks/{}/'.format(episode_id2)),
    )

  def test_is_bookmarked(self):
    episode_title = 'Colombians to deliver their verdict on peace accord'
    episode = episodes_dao.\
        get_episode_by_title(episode_title, self.user1.uid)
    episode_id = episode.id
    self.assertFalse(episode.is_bookmarked)

    self.user1.post('api/v1/bookmarks/{}/'.format(episode_id))
    episode = episodes_dao.\
        get_episode(episode_id, self.user1.uid)
    self.assertTrue(episode.is_bookmarked)

    response = self.user1.get('api/v1/bookmarks/')
    data = json.loads(response.data)
    self.assertTrue(data['data']['bookmarks'][0]['episode']['is_bookmarked'])

    self.user1.delete('api/v1/bookmarks/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, self.user1.uid)
    self.assertFalse(episode.is_bookmarked)
