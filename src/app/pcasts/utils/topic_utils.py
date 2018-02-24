import json
from app import constants

# Returns a mapping from topic name to least significant bit. Used for the DB
def create_topics_mappings():
  topic_bit_position, subtopic_bit_position = 0, 0
  topic_to_id, subtopic_to_id = {}, {}
  topics, subtopics = [], []

  json_data = open(constants.TOPIC_FILE)
  data = json.load(json_data)
  for topic in data['topics']:
    topics.append(topic['name'])
    for subtopic in topic['subtopics']:
      subtopics.append(subtopic["name"])

  topics.sort()
  subtopics.sort()
  for topic in topics:
    topic_to_id[topic] = topic_bit_position
    topic_bit_position += 1

  for subtopic in subtopics:
    subtopic_to_id[subtopic] = subtopic_bit_position
    subtopic_bit_position += 1

  return topic_to_id, subtopic_to_id

# Returns an id that represents both the old topics and the new topic
# identified by subtopic_offset
def create_topic_id(old_topic_id, topic_name):
  bitmask = 0
  if topic_name in topic_map:
    bitmask = 1 << topic_map[topic_name]
  return old_topic_id | bitmask

# Returns an id that represents both the old subtopics and the new subtopic
# identified by subtopic_offset
def create_subtopic_id(old_topic_id, subtopic_name):
  bitmask = 0
  if subtopic_name in subtopic_map:
    bitmask = 1 << subtopic_map[subtopic_name]
  return old_topic_id | bitmask

def get_topic_ids(genres):
  topic_id = 0
  subtopic_id = 0
  for genre in genres:
    topic_id = create_topic_id(topic_id, genre)
    subtopic_id = create_subtopic_id(subtopic_id, genre)
  return topic_id, subtopic_id

topic_map, subtopic_map = create_topics_mappings()
