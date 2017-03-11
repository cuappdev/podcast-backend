#!/usr/bin/python

import json
import os

def load_google_creds():
  google_json = json.load(open('./tools/google.json'))
  os.environ['TEST_ID_TOKEN'] = google_json['id_token']

if __name__ == '__main__':
  load_google_creds()
