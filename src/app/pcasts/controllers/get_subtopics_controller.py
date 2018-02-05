import json
from . import *

class GetSubtopicsController(AppDevController):

  def get_path(self):
    return '/topics/subtopics/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    json_data = open(constants.SUBTOPIC_FILE)
    data = json.load(json_data)

    return { 'subtopics' : data['subtopics']}
