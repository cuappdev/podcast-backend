import unittest
import os
import sys

from tests.loading_utils import *

class TestCase(unittest.TestCase):

  def setUp(self):
    self.app = app.test_client()
    load_users()
