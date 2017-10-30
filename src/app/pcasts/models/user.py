from . import *
from sqlalchemy.orm import validates

class User(Base):
  __tablename__ = 'users'

  id = db.Column(db.Integer, primary_key=True)
  google_id = db.Column(db.String(255), unique=True)
  facebook_id = db.Column(db.String(255), unique=True)
  email = db.Column(db.String(255), nullable=False)
  first_name = db.Column(db.String(255))
  last_name = db.Column(db.String(255))
  image_url = db.Column(db.String(1500)) # Might have long image URL
  followers_count = db.Column(db.Integer, nullable=False)
  followings_count = db.Column(db.Integer, nullable=False)
  username = db.Column(db.String(255), nullable=False, unique=True)

  def __init__(self, **kwargs):
    assert kwargs.get('facebook_id') is not None or\
        kwargs.get('google_id') is not None
    self.google_id = kwargs.get('google_id')
    self.facebook_id = kwargs.get('facebook_id')
    self.email = kwargs.get('email', '')
    self.first_name = kwargs.get('first_name')
    self.last_name = kwargs.get('last_name')
    self.image_url = kwargs.get('image_url')
    self.followers_count = kwargs.get('followers_count', 0)
    self.followings_count = kwargs.get('followings_count', 0)
    non_empty_id, id_source = (self.facebook_id, 'facebook') if self.google_id \
        is None else (self.google_id, 'google')
    self.username = kwargs.get('username', 'temp-{}-{}'. \
        format(id_source, non_empty_id[0:20]))

  def __eq__(self, other_user):
    return self.google_id == other_user.google_id and \
        self.email == other_user.email and \
        self.facebook_id == other_user.facebook_id and \
        self.first_name == other_user.first_name and \
        self.last_name == other_user.last_name and \
        self.image_url == other_user.image_url and \
        self.followers_count == other_user.followers_count and \
        self.followings_count == other_user.followings_count and \
        self.username == other_user.username

  @validates('username')
  def validate_username(self, key, username):
    if len(username) < 1:
      raise Exception('Username length must greater than 0')
    return username
