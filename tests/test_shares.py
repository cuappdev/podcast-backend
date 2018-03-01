import sys
import time
from flask import json
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app import constants

class SharesTestCase(TestCase):

  def setUp(self):
    super(SharesTestCase, self).setUp()
    Share.query.delete()
    db_session_commit()

  def tearDown(self):
    super(SharesTestCase, self).tearDown()
    Share.query.delete()
    db_session_commit()

  # delete_share
  # get_shares

  def test_create_bookmark(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    share = Share.query.filter(Share.episode_id == episode_id1).first()
    self.assertIsNone(share)

    self.user1.post('api/v1/shares/{}/?sharee_ids={}'
                    .format(episode_id1, self.user2.uid))
    share = Share.query.filter(Share.episode_id == episode_id1).first()
    self.assertEqual(len(Share.query.all()), 1)
    self.assertEquals(share.episode_id, int(episode_id1))
    self.assertEqual(share.sharer_id, self.user1.uid)
    self.assertEqual(share.sharee_id, self.user2.uid)

    # test updating time on duplicate
    old_time = share.updated_at
    time.sleep(1)
    self.user1.post('api/v1/shares/{}/?sharee_ids={}'
                    .format(episode_id1, self.user2.uid))
    share = Share.query.filter(Share.episode_id == episode_id1).first()
    self.assertEqual(len(Share.query.all()), 1)
    self.assertNotEqual(old_time, share.updated_at)

    print 1
    # test multiple sharees
    self.user2.post('api/v1/shares/{}/?sharee_ids={}'
                    .format(episode_id2,
                            ','.join([str(self.user1.uid),
                                      str(self.user3.uid)])))
    self.assertEqual(len(Share.query.all()), 3)
    share1 = Share.query.filter(Share.episode_id == episode_id2,
                                Share.sharer_id == self.user2.uid,
                                Share.sharee_id == self.user1.uid)
    share2 = Share.query.filter(Share.episode_id == episode_id2,
                                Share.sharer_id == self.user2.uid,
                                Share.sharee_id == self.user3.uid)
    self.assertIsNotNone(share1)
    self.assertIsNotNone(share2)

  def test_delete_share(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id
    self.user1.post('api/v1/shares/{}/?sharee_ids={}'
                    .format(episode_id1, self.user2.uid))
    self.assertEqual(len(Share.query.all()), 1)
    share_id = Share.query.first().id
    self.user1.delete('api/v1/shares/{}/'.format(share_id))
    self.assertEqual(len(Share.query.all()), 0)

  def test_get_shares(self):
    episode_title1 = 'Colombians to deliver their verdict on peace accord'
    episode_id1 = episodes_dao.\
        get_episode_by_title(episode_title1, self.user1.uid).id

    episode_title2 = 'Battle of the camera drones'
    episode_id2 = episodes_dao.\
        get_episode_by_title(episode_title2, self.user1.uid).id

    response = self.user1.get('api/v1/shares/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['shares']), 0)

    self.user2.post('api/v1/shares/{}/?sharee_ids={}'
                    .format(episode_id1, self.user1.uid))
    self.user3.post('api/v1/shares/{}/?sharee_ids={}'
                    .format(episode_id2,
                            ','.join([str(self.user2.uid),
                                      str(self.user1.uid)])))

    response = self.user1.get('api/v1/shares/')
    data = json.loads(response.data)
    self.assertEquals(len(data['data']['shares']), 2)
    self.assertTrue(
        data['data']['shares'][0]['episode']['id'] == int(episode_id1) or
        data['data']['shares'][1]['episode']['id'] == int(episode_id1)
    )
    self.assertTrue(
        data['data']['shares'][0]['episode']['id'] == int(episode_id2) or
        data['data']['shares'][1]['episode']['id'] == int(episode_id2)
    )
