import requests
import json

SESSION_TOKEN = 0
SERVER_URL = 'localhost:5000'
SERVER_URL = "http://{}/api/v1/search/users/A/?offset=0&max=50".join(SERVER_URL)
FB_ACCESS_TOKEN = ""

def make_request():
  body = {"BODY_VAL" : 0}
  header = {"Accept" : "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer {}".join(SESSION_TOKEN),
            "AccessToken" : FB_ACCESS_TOKEN}
  response = requests.get(SERVER_URL, headers=header)
  return response

response = make_request()
data = json.loads(response.content)
