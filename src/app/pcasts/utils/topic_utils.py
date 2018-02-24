import json
from app import constants

# Returns a mapping from (sub)topic name to least significant bit and (sub)topic
# id to least significant bit.
def create_topics_mappings():
  topic_bit_position, subtopic_bit_position = 0, 0
  topic_name_offset, subtopic_name_offset = {}, {}
  topic_id_offset, subtopic_id_offset = {}, {}
  name_to_id = {}
  topics, subtopics = [], []

  json_data = open(constants.TOPIC_FILE)
  data = json.load(json_data)
  for topic in data['topics']:
    name_to_id[topic['name']] = topic['id']
    topics.append(topic['name'])
    for subtopic in topic['subtopics']:
      name_to_id[subtopic['name']] = subtopic['id']
      subtopics.append(subtopic['name'])

  topics.sort()
  subtopics.sort()
  for topic in topics:
    topic_name_offset[topic] = topic_bit_position
    topic_id_offset[name_to_id[topic]] = topic_bit_position
    topic_bit_position += 1

  for subtopic in subtopics:
    subtopic_name_offset[subtopic] = subtopic_bit_position
    subtopic_id_offset[name_to_id[subtopic]] = subtopic_bit_position
    subtopic_bit_position += 1

  return topic_id_offset, subtopic_id_offset, \
         topic_name_offset, subtopic_name_offset

# Returns an id that represents both the old topics and the new topic
# identified by subtopic_offset
def create_topic_id(old_topic_id, topic_name):
  bitmask = 0
  if topic_name in topic_name_offset:
    bitmask = 1 << topic_name_offset[topic_name]
  return old_topic_id | bitmask

# Returns an id that represents both the old subtopics and the new subtopic
# identified by subtopic_offset
def create_subtopic_id(old_topic_id, subtopic_name):
  bitmask = 0
  if subtopic_name in subtopic_name_offset:
    bitmask = 1 << subtopic_name_offset[subtopic_name]
  return old_topic_id | bitmask

def get_topic_ids(genres):
  topic_id = 0
  subtopic_id = 0
  for genre in genres:
    topic_id = create_topic_id(topic_id, genre)
    subtopic_id = create_subtopic_id(subtopic_id, genre)
  return topic_id, subtopic_id

def translate_topic_id(topic_id):
  return 1 << topic_id_offset[topic_id]

def translate_subtopic_id(topic_id):
  return 1 << subtopic_id_offset[topic_id]

#Maps from topic id to the offset bit and topic name to offset bits
topic_id_offset, subtopic_id_offset, topic_name_offset, \
  subtopic_name_offset = create_topics_mappings()
