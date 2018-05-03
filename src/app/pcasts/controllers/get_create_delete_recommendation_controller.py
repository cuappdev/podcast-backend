from . import *

class GetCreateDeleteRecommendationController(AppDevController):

  def get_path(self):
    return '/recommendations/<episode_id>/'

  def get_methods(self):
    return ['GET', 'POST', 'DELETE']

  @authorize
  def content(self, **kwargs):
    episode_id = request.view_args['episode_id']
    user = kwargs.get('user')

    if request.method == "GET":
      offset = request.args['offset']
      max_recs = request.args['max']
      recommendations = recommendations_dao. \
        get_episode_recommendations(episode_id, user.id, max_recs, offset)

      return {'recommendations': \
        [recommendation_schema.dump(r).data for r in recommendations]}

    elif request.method == "POST":
      blurb = None
      if 'blurb' in request.args and request.args['blurb'].lower() != 'none' \
          and request.args['blurb'].lower() != 'null':
        blurb = request.args['blurb']
      recommendation = recommendations_dao\
        .create_or_update_recommendation(episode_id, user, blurb=blurb)
      app.logger.info({
          'user': user.username,
          'episode_id': episode_id,
          'message': 'created or updated recommendation with blurb {}'
                     .format(blurb)
      })

      return {'recommendation': recommendation_schema.dump(recommendation).data}

    elif request.method == "DELETE":
      recommendation = \
        recommendations_dao.delete_recommendation(episode_id, user)
      app.logger.info({
          'user': user.username,
          'episode_id': episode_id,
          'message': 'deleted recommendation'
      })

      return {'recommendation': recommendation_schema.dump(recommendation).data}
