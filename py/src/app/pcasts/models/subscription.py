from sqlalchemy import UniqueConstraint
from . import *

class Subscription(Base):
  __tablename__ = 'subscriptions'
  __bind_key__ = 'db'
  __table_args__ = (
      UniqueConstraint('user_id', 'series_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  user_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  series_id = db.Column(db.Integer, nullable=False)

  user = db.relationship('User', cascade='all,delete')

  def __init__(self, **kwargs):
    self.user_id = kwargs.get('user_id')
    self.series_id = kwargs.get('series_id')
    self.series = kwargs.get('series', None)
