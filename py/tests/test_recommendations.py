import sys
from tests.test_case import *
from app.pcasts.dao import episodes_dao
from app.pcasts.controllers import series_subscriptions_controller

src_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__))) + '/src'
sys.path.append(src_path)

from app import constants # pylint: disable=C0413

class RecommendationsTestCase(TestCase):

  def setUp(self):
    super(RecommendationsTestCase, self).setUp()
    Recommendation.query.delete()
    episodes_dao.clear_all_recommendations_counts()
    db_session_commit()

  def test_is_recommended(self):
    user = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    episode_id = '202161'
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertFalse(episode.is_recommended)

    self.app.post('api/v1/recommendations/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertTrue(episode.is_recommended)

    self.app.delete('api/v1/recommendations/{}/'.format(episode_id))
    episode = episodes_dao.get_episode(episode_id, user.id)
    self.assertFalse(episode.is_recommended)
