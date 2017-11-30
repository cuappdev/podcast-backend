import unittest
import os
import sys

from tests.test_user import *

class TestCase(unittest.TestCase):

  def setUp(self):
    User.query.delete()
    self.app = app.test_client()
    initTestUser()
    self.user1 = TestUser(test_client=self.app)
    self.user2 = TestUser(test_client=self.app)
    self.user3 = TestUser(test_client=self.app)

  def tearDown(self):
    User.query.delete()
