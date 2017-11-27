from . import *

class CreateDeleteFollowingController(AppDevController):

  def get_path(self):
    return '/followings/<followed_id>/'

  def get_methods(self):
    return ['POST', 'DELETE']

  @authorize
  def content(self, **kwargs):
    follower = kwargs.get('user')
    followed_id = request.view_args['followed_id']
    followed = users_dao.get_user_by_id(follower.id, followed_id)
    if request.method == 'POST':
      following= followings_dao.create_following(follower.id, followed_id)
      app.logger.info({
          'follower': follower.username,
          'followed': followed.username,
          'message': 'following created'
      })
      return {'following': following_schema.dump(following).data}

    elif request.method == 'DELETE':
      followings_dao.delete_following(follower.id, followed_id)
      app.logger.info({
          'follower': follower.username,
          'followed': followed.username,
          'message': 'following deleted'
      })

      return dict()
