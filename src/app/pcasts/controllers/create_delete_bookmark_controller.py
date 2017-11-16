from appdev.controllers import *
from . import *

class CreateDeleteBookmarkController(AppDevController):

  def get_path(self):
    return '/bookmarks/<episode_id>/'

  def get_methods(self):
    return ['POST', 'DELETE']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    episode_id = request.view_args['episode_id']
    if request.method == 'POST':
      bookmark = bookmarks_dao.create_bookmark(episode_id, user)
      app.logger.info(
          '(id: %s, username: %s, episode_id: %s) bookmark created',
          user.id, user.username, episode_id
      )
    else:
      bookmark = bookmarks_dao.delete_bookmark(episode_id, user)
      app.logger.info(
          '(id: %s, username: %s, episode_id: %s) bookmark deleted',
          user.id, user.username, episode_id
      )
    return {'bookmark': bookmark_schema.dump(bookmark).data}
