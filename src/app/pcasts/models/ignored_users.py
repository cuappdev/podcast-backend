from sqlalchemy import UniqueConstraint
from . import *

class IgnoredUsers(Base):
  __tablename__ = 'ignored_users'
  __table_args__ = (
      UniqueConstraint('user_id'),
  )

  user_id = db.Column(db.Integer, \
      db.ForeignKey('users.id', ondelete='CASCADE'), primary_key=True)
  ignored_fb_ids = db.Column(db.Text)

  user = db.relationship('User')

  def __init__(self, **kwargs):
    self.user_id = kwargs.get('user_id')
    self.ignored_fb_ids = kwargs.get('ignored_fb_ids')
