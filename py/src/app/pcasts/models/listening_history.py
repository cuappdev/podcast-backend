import datetime
from sqlalchemy import UniqueConstraint
from . import *

class ListeningHistory(Base):
  __tablename__ = 'listening_histories'
  __bind_key__ = 'db'
  __table_args__ = (
      UniqueConstraint('user_id', 'episode_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  listening_time = db.Column(db.DateTime, nullable=False)
  user_id = db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  episode_id = db.Column(db.Integer, nullable=False)

  user = db.relationship('User')

  def __init__(self, **kwargs):
    self.listening_time = datetime.datetime.now()
    self.user_id = kwargs.get('user_id')
    self.episode_id = kwargs.get('episode_id')

  def update_listening_time(self):
    self.listening_time = datetime.datetime.now()
