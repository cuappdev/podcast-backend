from . import *

class User(Base):
  __tablename__ = 'users'
  __bind_key__ = 'db'

  id = db.Column(db.Integer, primary_key=True)
  google_id = db.Column(db.String(255), unique=True)
  email = db.Column(db.String(255), nullable=False)
  first_name = db.Column(db.String(255))
  last_name = db.Column(db.String(255))
  image_url = db.Column(db.String(1500)) # Might have long image URL
  followers_count = db.Column(db.Integer, nullable=False)
  followings_count = db.Column(db.Integer, nullable=False)
  username = db.Column(db.String(255), nullable=False, unique=True)

  def __init__(self, **kwargs):
    self.google_id = kwargs.get('google_id')
    self.email = kwargs.get('email', '')
    self.first_name = kwargs.get('first_name')
    self.last_name = kwargs.get('last_name')
    self.image_url = kwargs.get('image_url')
    self.followers_count = kwargs.get('followers_count', 0)
    self.followings_count = kwargs.get('followings_count', 0)
    self.username = kwargs.get('username', 'temp-{}'.format(self.google_id))
