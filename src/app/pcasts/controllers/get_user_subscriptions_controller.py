from . import *

class GetUserSubscriptionsController(AppDevController):

  def get_path(self):
    return '/subscriptions/users/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = request.view_args['user_id']
    subscriptions = subscriptions_dao.get_user_subscriptions(user_id)

    return {'subscriptions': \
      [subscription_schema.dump(s).data for s in subscriptions]}
