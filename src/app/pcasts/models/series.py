from . import *
from app.pcasts.utils import topic_utils

class Series(Base):
  __tablename__ = 'series'
  __bind_key__ = 'podcast_db'

  id = db.Column(db.Integer, primary_key=True)
  title = db.Column(db.Text)
  country = db.Column(db.String(190))
  author = db.Column(db.String(190))
  image_url_lg = db.Column(db.Text)
  image_url_sm = db.Column(db.Text)
  feed_url = db.Column(db.Text, nullable=False)
  genres = db.Column(db.Text) # semicolon-separated
  subscribers_count = db.Column(db.Integer, nullable=False)
  topic_id = db.Column(db.Integer)
  subtopic_id = db.Column(db.BigInteger)

  def __init__(self, **kwargs):
    self.id = kwargs.get('id')
    self.title = kwargs.get('title', None)
    self.country = kwargs.get('country', None)
    self.author = kwargs.get('author', None)
    self.image_url_lg = kwargs.get('image_url_lg', None)
    self.image_url_sm = kwargs.get('image_url_sm', None)
    self.feed_url = kwargs.get('feed_url')
    self.genres = ';'.join(kwargs.get('genres', []))
    self.subscribers_count = kwargs.get('subscribers_count', 0)
    self.topic_id, self.subtopic_id = \
        topic_utils.get_topic_ids(kwargs.get('genres', []))
