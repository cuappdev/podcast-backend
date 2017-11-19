from flask import json
from tests import api_utils
from tests.test_case import *
from app import constants
from app.pcasts.dao import users_dao, sessions_dao, followings_dao
from tests.test_user import *

class TestUserTestCase(TestCase):

  def setUp(self):
    Following.query.delete()
    User.query.delete()
    Session.query.delete()
    db_session_commit()
    super(TestUserTestCase, self).setUp()
    initTestUser()

  def tearDown(self):
    super(TestUserTestCase, self).tearDown()
    Following.query.delete()
    User.query.delete()
    Session.query.delete()
    db_session_commit()


  def test_init_user_goog(self):
    # Create first goog user
    g_user1 = TestUser(self.app, name="User one")
    self.assertEquals(g_user1.tokens, {})
    self.assertEquals(g_user1.app_tokens, {})
    self.assertEquals(g_user1.name, "User one")
    self.assertEquals(g_user1.goog_user_count, 0)
    self.assertEquals(TestUser.goog_user_count, 1)
    # After login
    self.assertEquals(constants.NUM_TEST_USERS + 1, \
        users_dao.get_number_users())
    self.assertTrue(users_dao.get_user_by_id(g_user1.uid, g_user1.uid) \
        != None)
    self.assertTrue(g_user1.session_token != None)

    # Create second goog user
    g_user2 = TestUser(self.app, name="User two")
    self.assertEquals(g_user2.tokens, {})
    self.assertEquals(g_user2.app_tokens, {})
    self.assertEquals(g_user2.name, "User two")
    self.assertEquals(g_user2.goog_user_count, 1)
    self.assertEquals(TestUser.goog_user_count, 2)
    # After login
    self.assertEquals(constants.NUM_TEST_USERS + 2, \
        users_dao.get_number_users())
    self.assertTrue(users_dao.get_user_by_id(g_user2.uid, g_user2.uid) != None)
    self.assertTrue(g_user2.session_token != None)

  def test_init_user_fb(self):

    fb_app_token = api_utils.get_facebook_app_access_token()

    # First fb user
    fb_user1 = TestUser(self.app, name="User one", \
        app_access_token=fb_app_token, platform=constants.FACEBOOK)
    self.assertTrue(fb_user1.tokens != {})
    self.assertTrue(fb_user1.app_tokens != {})
    self.assertEquals(fb_user1.name, "User one")
    self.assertEquals(fb_user1.goog_user_count, 0)
    self.assertEquals(TestUser.goog_user_count, 0)
    # After login
    self.assertEquals(constants.NUM_TEST_USERS + 1,\
        users_dao.get_number_users())
    self.assertTrue(users_dao.get_user_by_id(fb_user1.uid, fb_user1.uid)\
        != None)
    self.assertTrue(fb_user1.session_token != None)

    # Second fb user
    fb_user2 = TestUser(self.app, name="User two", \
        app_access_token=fb_app_token, platform=constants.FACEBOOK)
    self.assertTrue(fb_user2.tokens != {})
    self.assertTrue(fb_user2.app_tokens != {})
    self.assertEquals(fb_user2.name, "User two")
    self.assertEquals(fb_user2.goog_user_count, 0)
    self.assertEquals(TestUser.goog_user_count, 0)
    # After login
    self.assertEquals(constants.NUM_TEST_USERS + 2, \
        users_dao.get_number_users())
    self.assertTrue(users_dao.get_user_by_id(fb_user2.uid, fb_user2.uid) \
        != None)
    self.assertTrue(fb_user2.session_token != None)

  def test_get(self):
    fb_app_token = api_utils.get_facebook_app_access_token()
    g_user1 = TestUser(self.app, name="User one")
    fb_user1 = TestUser(self.app, name="User two", \
        app_access_token=fb_app_token, platform=constants.FACEBOOK)

    response = fb_user1.get("api/v1/users/me/")
    self.assertEquals(response['user']['id'], fb_user1.uid)
    response = g_user1.get("api/v1/users/me/")
    self.assertEquals(response['user']['id'], g_user1.uid)


  def test_post(self):
    fb_app_token = api_utils.get_facebook_app_access_token()
    g_user1 = TestUser(self.app, name="User one")
    fb_user1 = TestUser(self.app, name="User two", \
        app_access_token=fb_app_token, platform=constants.FACEBOOK)

    response = fb_user1.post("api/v1/followings/{}/".format(g_user1.uid))
    self.assertTrue(response['following'] != None)
    response = g_user1.post("api/v1/followings/{}/".format(fb_user1.uid))
    self.assertTrue(response['following'] != None)

  def test_delete(self):
    fb_app_token = api_utils.get_facebook_app_access_token()
    g_user1 = TestUser(self.app, name="User one")
    fb_user1 = TestUser(self.app, name="User two", \
        app_access_token=fb_app_token, platform=constants.FACEBOOK)

    followings_dao.create_following(g_user1.uid, fb_user1.uid)
    followings_dao.create_following(fb_user1.uid, g_user1.uid)
    response = fb_user1.delete("api/v1/followings/{}/".format(g_user1.uid))
    self.assertEquals(response, {})
    response = g_user1.delete("api/v1/followings/{}/".format(fb_user1.uid))
    self.assertEquals(response, {})
