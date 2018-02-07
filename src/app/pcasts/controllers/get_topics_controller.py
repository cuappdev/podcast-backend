import json
import flask
from . import *
from app import constants

class GetTopicsController(AppDevController):

  def get_path(self):
    return '/topics/parent/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    json_data = open(constants.TOPIC_FILE)
    data = json.load(json_data)

    return data
