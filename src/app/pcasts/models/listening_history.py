import datetime
from sqlalchemy import UniqueConstraint
from . import *

class ListeningHistory(Base):
  __tablename__ = 'listening_histories'
  __table_args__ = (
      UniqueConstraint('user_id', 'episode_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  user_id = db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  episode_id = db.Column(db.Integer, nullable=False)
  listening_duration = db.Column(db.Float, nullable=False)
  time_at = db.Column(db.Float, nullable=False)

  user = db.relationship('User')

  def __init__(self, **kwargs):
    self.listening_time = datetime.datetime.now()
    self.user_id = kwargs.get('user_id')
    self.episode_id = kwargs.get('episode_id')
    self.listening_duration = kwargs.get('listening_duration')
    self.time_at = kwargs.get('time_at')
