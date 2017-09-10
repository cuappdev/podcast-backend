import unittest
import os
import sys

from tests.loading_utils import * # pylint: disable=C0413

class TestCase(unittest.TestCase):

  def setUp(self):
    self.app = app.test_client()
    load_users()
