import requests

def get_me(access_token):
  base_uri = "https://graph.facebook.com/v2.10/me" \
      +"?fields=id%2Cname%2Cfirst_name%2Clast_name&access_token="
  uri = '{}{}'.format(base_uri, access_token)
  return requests.get(uri).json()
