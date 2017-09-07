from . import *

class Episode(Base):
  __tablename__ = 'episodes'
  __bind_key__ = 'podcast_db'

  id = db.Column(db.Integer, primary_key=True)
  title = db.Column(db.String(255))
  author = db.Column(db.String(255))
  summary = db.Column(db.String(255))
  pub_date = db.Column(db.DateTime, default=db.func.current_timestamp())
  duration = db.Column(db.String(255))
  audio_url = db.Column(db.String(1000))
  tags = db.Column(db.String(1000)) # semicolon-separated
  recommendations_count = db.Column(db.Integer, nullable=False)

  series_id = \
    db.Column(db.Integer, db.ForeignKey('series.id', ondelete='CASCADE'))

  series = db.relationship('Series', cascade='all,delete', backref='episodes')

  def __init__(self, **kwargs):
    self.title = kwargs.get('title')
    self.author = kwargs.get('author')
    self.duration = kwargs.get('duration')
    self.audio_url = kwargs.get('audio_url')
    self.tags = kwargs.get('tags')
    self.recommendations_count = kwargs.get('recommendations_count', 0)
