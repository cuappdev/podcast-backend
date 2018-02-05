import json
import urllib
import requests
import io

topic_lookup_url = 'https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/genres?id=26'

def read_url(url):
  response = urllib.urlopen(url)
  return json.loads(response.read())

def fetch_topics():
  topics = [] # list of jsons {id,name}
  subtopics = [] # list of jsons {id,name, parent_id}
  data = read_url(topic_lookup_url)
  genres = data['26']['subgenres']
  for genre in genres.values():
    topics.append( {'id' : int(genre['id']), 'name' : str(genre['name'])})
    if 'subgenres' in genre:
      for subgenre in genre['subgenres'].values():
        subtopics.append({'id' : int(subgenre['id']),
                          'name' : str(subgenre['name']),
                          'parent_id' : int(genre['id'])})
  return topics, subtopics

def serialize_json(data, path):
  with open(path, 'w') as outfile:
    json.dump(data, outfile)

topics, subtopics = fetch_topics()
topics = { "topics" : topics }
subtopics = {"subtopics" : subtopics}
serialize_json(topics, 'static/topics.json')
serialize_json(subtopics, 'static/subtopics.json')
