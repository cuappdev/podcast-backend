import os
import sys

src_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__))) + '/src'
sys.path.append(src_path)

from app import app # pylint: disable=C0413
from app import constants # pylint: disable=C0413
from app.pcasts.models._all import * # pylint: disable=C0413
from app.pcasts.utils.db_utils import * # pylint: disable=C0413

def load_users():
  default_users = [
      User(
          google_id=constants.TEST_USER_GOOGLE_ID1,
          facebook_id=constants.TEST_USER_FACEBOOK_ID1,
          email='default_email1',
          first_name='default_first_name1',
          last_name='default_last_name1',
          image_url='',
          followers_count=0,
          followings_count=0,
      ),
      User(
          google_id=constants.TEST_USER_GOOGLE_ID2,
          facebook_id=constants.TEST_USER_FACEBOOK_ID2,
          email='default_email2',
          first_name='default_first_name2',
          last_name='default_last_name2',
          image_url='',
          followers_count=0,
          followings_count=0,
      )
  ]

  User.query.delete()
  db_session_commit()
  commit_models(default_users)
