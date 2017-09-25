from sqlalchemy import UniqueConstraint
from . import *

class Following(Base):
  __tablename__ = 'followings'
  __bind_key__ = 'db'
  __table_args__ = (
      UniqueConstraint('follower_id', 'followed_id'),
  )

  id = db.Column(db.Integer, primary_key=True)
  follower_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))
  followed_id = \
    db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'))

  follower = \
    db.relationship('User', foreign_keys=[follower_id])
  followed = \
    db.relationship('User', foreign_keys=[followed_id])

  def __init__(self, **kwargs):
    self.follower_id = kwargs.get('follower_id')
    self.followed_id = kwargs.get('followed_id')
