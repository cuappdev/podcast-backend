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
  genres = db.Column(db.String(1000)) # semicolon-separated
