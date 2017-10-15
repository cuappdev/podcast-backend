from . import *

class GetUserRecommendationsController(AppDevController):

  def get_path(self):
    return '/recommendations/users/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    caller_user_id = kwargs.get('user').id
    requested_user_id = request.view_args['user_id']
    recommendations = recommendations_dao.get_user_recommendations(
        caller_user_id,
        requested_user_id
    )

    return {'recommendations': \
      [recommendation_schema.dump(r).data for r in recommendations]}
