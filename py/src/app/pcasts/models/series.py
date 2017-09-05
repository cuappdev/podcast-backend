from . import *

class Series(Base):
  __tablename__ = 'series'
  __bind_key__ = 'podcast_db'

  id = db.Column(db.Integer, primary_key=True)
  title = db.Column(db.String(255))
  country = db.Column(db.String(255))
  author = db.Column(db.String(255))
  image_url_lg = db.Column(db.String(1000))
  image_url_sm = db.Column(db.String(1000))
  feed_url = db.Column(db.String(1000), nullable=False)
  genres = db.Column(db.String(1000)) # semicolon-separated

  def __init__(self, **kwargs):
    self.id = kwargs.get('id')
    self.title = kwargs.get('title', None)
    self.country = kwargs.get('country', None)
    self.author = kwargs.get('author', None)
    self.image_url_lg = kwargs.get('image_url_lg', None)
    self.image_url_sm = kwargs.get('image_url_sm', None)
    self.feed_url = kwargs.get('feed_url')
    self.genres = ';'.join(kwargs.get('genres', []))
