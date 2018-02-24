from flask import json
from tests.test_case import *
from app.pcasts.utils import topic_utils

class UsersTestCase(TestCase):

  def test_create_topic_id(self):
    topics = ["Arts", "Business", "Junk", "TV & Film", "Technology"]
    topic_id = 0
    for topic in topics:
      topic_id = topic_utils.create_topic_id(topic_id, topic)
    self.assertTrue(topic_id & (1 << topic_utils.topic_map['Arts']))
    self.assertTrue(topic_id & (1 << topic_utils.topic_map['Business']))
    self.assertTrue(topic_id & (1 << topic_utils.topic_map['TV & Film']))
    self.assertTrue(topic_id & (1 << topic_utils.topic_map['Technology']))
    self.assertFalse(topic_id & (1 << topic_utils.topic_map['Health']))

  def test_create_subtopic_id(self):
    subtopics = ["Automotive", "Aviation", "Other Games", "Video Games", "Junk"]
    subtopic_id = 0
    for subtopic in subtopics:
      subtopic_id = topic_utils.create_subtopic_id(subtopic_id, subtopic)
    self.assertTrue(subtopic_id & (1 << topic_utils.subtopic_map['Automotive']))
    self.assertTrue(subtopic_id & (1 << topic_utils.subtopic_map['Aviation']))
    self.assertTrue(subtopic_id & (1 << \
        topic_utils.subtopic_map['Video Games']))
    self.assertTrue(subtopic_id & (1 << \
        topic_utils.subtopic_map['Other Games']))
    self.assertFalse(subtopic_id & (1 << topic_utils.subtopic_map['Outdoor']))
