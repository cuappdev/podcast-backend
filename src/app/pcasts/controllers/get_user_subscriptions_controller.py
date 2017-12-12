from . import *

class GetUserSubscriptionsController(AppDevController):

  def get_path(self):
    return '/subscriptions/users/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    my_id = kwargs.get('user').id
    their_id = request.view_args['user_id']
    subscriptions = subscriptions_dao.get_user_subscriptions(their_id, my_id)

    return {'subscriptions': \
      [subscription_schema.dump(s).data for s in subscriptions]}
