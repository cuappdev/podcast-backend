from . import *
from appdev.controllers import *

class HelloWorldController(AppDevController):

  def get_path(self):
    return '/hello/'

  def get_methods(self):
    return ['GET']

  def content(self, **kwargs):
    return { 'message': 'Hello, World!' }
