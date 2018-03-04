from sqlalchemy import UniqueConstraint
from . import *

class Share(Base):
  __tablename__ = 'sharing'
  __table_args__ = (
      UniqueConstraint('sharer_id', 'sharee_id', 'episode_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  sharer_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  sharee_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  episode_id = db.Column(db.Integer, nullable=False)

  sharer = \
    db.relationship('User', foreign_keys=[sharer_id])
  sharee = \
    db.relationship('User', foreign_keys=[sharee_id])

  def __init__(self, **kwargs):
    self.sharer_id = kwargs.get('sharer_id')
    self.sharee_id = kwargs.get('sharee_id')
    self.episode_id = kwargs.get('episode_id')
