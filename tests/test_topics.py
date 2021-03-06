from flask import json
from tests.test_case import *
from app import constants

class TopicsTestCase(TestCase):

  def setUp(self):
    super(TopicsTestCase, self).setUp()

  def tearDown(self):
    super(TopicsTestCase, self).tearDown()

  def test_get_topics(self):
    results = self.user1.get('api/v1/topics/parent/')
    topics = json.loads(results.data)['data']
    self.assertEquals(len(topics['topics']), 16)
    for topic in topics['topics']:
        self.assertTrue(len(topic['subtopics']) >= 0)
