import sys
from flask import json
from tests.test_case import *
from tests.test_user import *
from tests import api_utils
from app import constants # pylint: disable=C0413

class FriendsTestCase(TestCase):

  def setUp(self):
    super(FriendsTestCase, self).setUp()
    Following.query.delete()
    db_session_commit()

  def tearDown(self):
    super(FriendsTestCase, self).tearDown()
    Following.query.delete()
    db_session_commit()

  def test_get_friends(self):
    fb_app_token = api_utils.get_facebook_app_access_token()

    fb_user1 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB One')
    fb_user2 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Two')
    fb_user3 = TestUser(test_client=self.app, platform=constants.FACEBOOK,\
        app_access_token=fb_app_token, name='FB Three')

    # No Friends
    payload_1 = {
        'access_token': fb_user1.tokens[constants.FACEBOOK]
    }
    p1_data = json.dumps(payload_1)
    response = fb_user1.get('api/v1/users/facebook/friends/', data=p1_data)
    data = json.loads(response.data)['data']

    self.assertEquals(data['users'], [])

    # 1 Friend
    api_utils.create_facebook_friendship(fb_user1, fb_user2)
    payload_2 = {
        'access_token': fb_user2.tokens[constants.FACEBOOK]
    }
    p2_data = json.dumps(payload_2)
    response = fb_user2.get('api/v1/users/facebook/friends/', data=p2_data)
    data = json.loads(response.data)['data']

    self.assertTrue(len(data['users']) == 1)
    self.assertEquals(data['users'][0]['id'], fb_user1.uid)
    self.assertEquals(data['users'][0]['is_following'], False)

    # 2 Friends with 1 following
    fb_user1.post('api/v1/followings/{}/'.format(fb_user2.uid))
    api_utils.create_facebook_friendship(fb_user1, fb_user3)
    response = fb_user1.get('api/v1/users/facebook/friends/', data=p1_data)
    data = json.loads(response.data)['data']

    self.assertTrue(len(data['users']) == 2)
    if data['users'][0]['id'] == fb_user3.uid:
      data['users'].reverse()
    self.assertEquals(data['users'][0]['id'], fb_user2.uid)
    self.assertEquals(data['users'][0]['is_following'], True)

    self.assertEquals(data['users'][1]['id'], fb_user3.uid)
    self.assertEquals(data['users'][1]['is_following'], False)
