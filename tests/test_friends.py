import sys
from flask import json
from tests.test_case import *
from app import constants # pylint: disable=C0413

class FriendsTestCase(TestCase):

  def setUp(self):
    super(FriendsTestCase, self).setUp()

  def tearDown(self):
    super(FriendsTestCase, self).tearDown()

  def test_find_friends(self):
    fb_master_token = get_facebook_app_access_token()
    user_1_token = create_facebook_user(fb_master_token, "User 1")
    user_2_token = create_facebook_user(fb_master_token, "User 3")
    user_3_token = create_facebook_user(fb_master_token, "User 2")
    user_4_token = create_facebook_user(fb_master_token, "User 2")
    # Test with 0 friends
    # Test with 1 friends
    # Test with 2 friends
    # Test without facebook id
