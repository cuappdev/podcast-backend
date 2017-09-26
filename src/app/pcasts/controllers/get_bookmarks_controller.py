from appdev.controllers import *
from . import *

class GetBookmarksController(AppDevController):

  def get_path(self):
    return '/bookmarks/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    bookmarks = bookmarks_dao.get_user_bookmarks(user)
    return {'bookmarks': [bookmark_schema.dump(b).data for b in bookmarks]}
