from sqlalchemy import UniqueConstraint
from sqlalchemy import PrimaryKeyConstraint
from . import *

class ReadNewEpisodeNotification(Base):
  __tablename__ = 'read_new_episode_notifications'
  __table_args__ = (
      UniqueConstraint('user_id', 'episode_id'),
      PrimaryKeyConstraint('user_id', 'episode_id'),
  )

  user_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  episode_id = db.Column(db.Integer, nullable=False)
  subscription_id = \
    db.Column(db.Integer, db.ForeignKey('subscriptions.id', ondelete='CASCADE'))

  user = db.relationship('User')
  subscription = db.relationship('Subscription')

  def __init__(self, **kwargs):
    self.user_id = kwargs.get('user_id')
    self.episode_id = kwargs.get('episode_id')
    self.subscription_id = kwargs.get('subscription_id')
