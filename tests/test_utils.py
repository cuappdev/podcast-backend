from flask import json
from tests.test_case import *
from app.pcasts.utils import topic_utils

class TestUtilsTestCase(TestCase):

  def test_create_topic_id(self):
    topics = ["Arts", "Business", "Junk", "TV & Film", "Technology"]
    topic_id = 0
    for topic in topics:
      topic_id = topic_utils.create_topic_id(topic_id, topic)
    self.assertTrue(topic_id & (1 << topic_utils.topic_name_offset['Arts']))
    self.assertTrue(topic_id & (1 << topic_utils.topic_name_offset['Business']))
    self.assertTrue(topic_id & (1 << \
        topic_utils.topic_name_offset['TV & Film']))
    self.assertTrue(topic_id & (1 << \
        topic_utils.topic_name_offset['Technology']))
    self.assertFalse(topic_id & (1 << \
        topic_utils.topic_name_offset['Health']))

  def test_create_subtopic_id(self):
    subtopics = ["Automotive", "Aviation", "Other Games", "Video Games", "Junk"]
    subtopic_id = 0
    for subtopic in subtopics:
      subtopic_id = topic_utils.create_subtopic_id(subtopic_id, subtopic)
    self.assertTrue(subtopic_id & (1 << \
        topic_utils.subtopic_name_offset['Automotive']))
    self.assertTrue(subtopic_id & (1 << \
        topic_utils.subtopic_name_offset['Aviation']))
    self.assertTrue(subtopic_id & (1 << \
        topic_utils.subtopic_name_offset['Video Games']))
    self.assertTrue(subtopic_id & (1 << \
        topic_utils.subtopic_name_offset['Other Games']))
    self.assertFalse(subtopic_id & (1 << \
        topic_utils.subtopic_name_offset['Outdoor']))
