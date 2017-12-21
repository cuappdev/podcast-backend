import requests

def get_me(access_token):
  base_uri = "https://graph.facebook.com/v2.10/me" \
      +"?fields=id%2Cname%2Cfirst_name%2Clast_name%2Cpicture&access_token="
  uri = '{}{}'.format(base_uri, access_token)
  return requests.get(uri).json()

def get_friend_ids(fb_id, access_token):
  uri = "https://graph.facebook.com/v2.11/{}/friends?access_token={}".\
      format(fb_id, access_token)
  request = requests.get(uri).json()
  friends = request['data']
  return [friend['id'] for friend in friends]
