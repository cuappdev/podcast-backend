from sqlalchemy import UniqueConstraint
from sqlalchemy.dialects.mysql import MEDIUMTEXT
from . import *

class Recommendation(Base):
  __tablename__ = 'recommendations'
  __table_args__ = (
      UniqueConstraint('user_id', 'episode_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  user_id = db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  episode_id = db.Column(db.Integer, nullable=False)
  blurb = db.Column(MEDIUMTEXT)

  user = db.relationship('User')

  def __init__(self, **kwargs):
    self.id = kwargs.get('id')
    self.user_id = kwargs.get('user_id')
    self.episode_id = kwargs.get('episode_id')
    self.blurb = kwargs.get('blurb', None)
