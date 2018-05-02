from sqlalchemy import UniqueConstraint
from . import *

class Subscription(Base):
  __tablename__ = 'subscriptions'
  __table_args__ = (
      UniqueConstraint('user_id', 'series_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  user_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  series_id = db.Column(db.Integer, nullable=False)
  subscribed_new_episodes = db.Column(db.Boolean, default=True)

  user = db.relationship('User')

  def __init__(self, **kwargs):
    self.user_id = kwargs.get('user_id')
    self.series_id = kwargs.get('series_id')
    self.series = kwargs.get('series', None)
    self.series = kwargs.get('subscribed_new_episodes', None)
