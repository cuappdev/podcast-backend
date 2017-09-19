import sys
from tests.test_case import *
from app.pcasts.dao import users_dao
from app.pcasts.controllers import series_subscriptions_controller

src_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__))) + '/src'
sys.path.append(src_path)

from app import constants # pylint: disable=C0413

class FollowingsTestCase(TestCase):

  def test_is_following(self):
    following = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).first()
    followed_id = User.query \
      .filter(User.google_id == constants.TEST_USER_GOOGLE_ID2).first().id
    followed = users_dao.get_user_by_id(following.id, followed_id)
    self.assertFalse(followed.is_following)

    self.app.post('api/v1/followings/{}'.format(followed_id))
    followed = users_dao.get_user_by_id(following.id, followed_id)
    self.assertTrue(followed.is_following)

    self.app.delete('api/v1/followings/{}'.format(followed_id))
    followed = users_dao.get_user_by_id(following.id, followed_id)
    self.assertFalse(followed.is_following)
