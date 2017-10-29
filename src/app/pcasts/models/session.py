import datetime
import hashlib
import os
from . import *

class Session(Base):
  __tablename__ = 'sessions'

  id = db.Column(db.Integer, primary_key=True)
  session_token = db.Column(db.String(255), unique=True, nullable=False)
  expires_at = db.Column(db.DateTime, nullable=False)
  update_token = db.Column(db.String(255), unique=True, nullable=False)
  is_active = db.Column(db.Boolean, nullable=False)

  user_id = \
      db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  user = db.relationship('User')

  def __init__(self, **kwargs):
    self.activate_session()
    self.user_id = kwargs.get('user_id')

  def _urlsafe_base_64(self):
    return hashlib.sha1(os.urandom(64)).hexdigest()

  # Activates the session (is_active is true, updates tokens, etc.)
  def activate_session(self):
    self.session_token = self._urlsafe_base_64()
    self.expires_at = datetime.datetime.now() + datetime.timedelta(days=1)
    self.update_token = self._urlsafe_base_64()
    self.is_active = True

  def deactivate(self):
    self.is_active = False
