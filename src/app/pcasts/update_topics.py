import io
import json
import urllib
import requests

topic_lookup_url = 'https://itunes.apple.com/WebObjects/MZStoreServices.woa/ws/genres?id=26'

def read_url(url):
  response = urllib.urlopen(url)
  return json.loads(response.read())

def fetch_topics():
  topics_list = [] # list of jsons {id,name}
  data = read_url(topic_lookup_url)
  genres = data['26']['subgenres']
  for genre in genres.values():
    subtopics = []
    if 'subgenres' in genre:
      for subgenre in genre['subgenres'].values():
        subtopics.append({'id' : int(subgenre['id']),
                          'name' : str(subgenre['name'])})
    topics_list.append({'id' : int(genre['id']),
                        'name' : str(genre['name']),
                        'subtopics' : subtopics})
  return topics_list

def serialize_json(data, path):
  with open(path, 'w') as outfile:
    json.dump(data, outfile)

topics = fetch_topics()
topics = {"topics": topics}
serialize_json(topics, 'static/topics.json')
