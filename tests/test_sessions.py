import sys
from time import sleep
from flask import json
from tests.test_case import *
from app.pcasts.dao import sessions_dao, users_dao
from app import constants

class SessionsTestCase(TestCase):

  def setUp(self):
    super(SessionsTestCase, self).setUp()
    Session.query.delete()
    db_session_commit()

  def tearDown(self):
    super(SessionsTestCase, self).tearDown()
    Session.query.delete()
    db_session_commit()

  def _setup_session(self):
    user = User.query.\
      filter(User.google_id == constants.TEST_USER_GOOGLE_ID1).\
      first()
    sessions_dao.get_or_create_session_and_activate(user.id)
    session = Session.query.\
      filter(Session.user_id == user.id).\
      first()
    self.assertTrue(session is not None)
    self.assertTrue(session.user_id == user.id)
    return user.id, \
      session.id, \
      session.session_token, \
      session.update_token, \
      session.expires_at, \
      session.updated_at

  def test_session_update(self):
    # Setup session
    user_id, \
      previous_session_id, \
      previous_session_token, \
      previous_update_token, \
      previous_expires_at, \
      previous_updated_at = self._setup_session()

    sleep(2) # sleep to simulate time passing

    # Update session
    self.app.post('api/v1/sessions/update/?update_token={}'.\
      format(previous_update_token))

    updated_session = Session.query.\
      filter(Session.user_id == user_id).\
      first()

    # Check everything
    self.assertEqual(updated_session.id, previous_session_id)
    self.assertNotEqual(updated_session.session_token, previous_session_token)
    self.assertNotEqual(updated_session.update_token, previous_update_token)
    self.assertGreater(updated_session.expires_at, previous_expires_at)
    self.assertTrue(updated_session.is_active)
    self.assertGreater(updated_session.updated_at, previous_updated_at)

  def test_session_deactivation(self):
    # Setup session
    user_id, session_id, _, _, _, _ = self._setup_session()

    # Deactivate session
    self.app.post('api/v1/users/sign_out/')

    deactivated_session = Session.query.\
      filter(Session.user_id == user_id).\
      first()

    # Check everything
    self.assertEqual(session_id, deactivated_session.id)
    self.assertTrue(not deactivated_session.is_active)
