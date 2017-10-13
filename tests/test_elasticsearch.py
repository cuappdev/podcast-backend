import sys
from datetime import datetime
from flask import json
from tests.test_case import *
from app.pcasts.elasticsearch import populate

class ElasticsearchTestCase(TestCase):

  def setUp(self):
    super(ElasticsearchTestCase, self).setUp()

  def test_populate(self):
    populate.populate()
