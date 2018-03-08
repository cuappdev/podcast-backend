import requests
import json

SESSION_TOKEN = 0
SERVER_URL = "http://SERVER_URL/api/v1/search/users/A/?offset=0&max=50"

def make_request():
  body = {"BODY_VAL" : 0}
  header = {"Accept" : "application/json",
            "Content-Type": "application/json",
            "Authorization": "Bearer {}".join(SESSION_TOKEN)}
  response = requests.get(SERVER_URL, headers=header)
  return response

response = post_lot_data()
data = json.loads(response.content)
