import sys
from time import sleep
from flask import json
from tests.test_case import *
from app.pcasts.dao import sessions_dao, users_dao
from app import constants

class SessionsTestCase(TestCase):

  def setUp(self):
    Session.query.delete()
    db_session_commit()
    super(SessionsTestCase, self).setUp()

  def tearDown(self):
    super(SessionsTestCase, self).tearDown()
    Session.query.delete()
    db_session_commit()

  def _setup_session(self):
    sessions_dao.get_or_create_session_and_activate(self.user1.uid)
    session = Session.query.\
      filter(Session.user_id == self.user1.uid).\
      first()
    self.assertTrue(session is not None)
    self.assertTrue(session.user_id == self.user1.uid)
    return self.user1.uid, \
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
    self.user1.post('api/v1/sessions/update/?update_token={}'.\
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
    user_id, session_id, new_token, _, _, _ = self._setup_session()
    self.user1.session_token = new_token
    # Deactivate session
    self.user1.post('api/v1/users/sign_out/')

    deactivated_session = Session.query.\
      filter(Session.user_id == user_id).\
      first()

    # Check everything
    self.assertEqual(session_id, deactivated_session.id)
    self.assertFalse(deactivated_session.is_active)
