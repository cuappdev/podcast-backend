from . import *

class GetUserRecommendationsController(AppDevController):

  def get_path(self):
    return '/recommendations/users/<user_id>/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user_id = request.view_args['user_id']
    recommendations = recommendations_dao.get_user_recommendations(user_id)

    return {'recommendations': \
      [recommendation_schema.dump(r).data for r in recommendations]}
