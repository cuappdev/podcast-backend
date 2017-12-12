from sqlalchemy.dialects.mysql import MEDIUMTEXT
from . import *

class Episode(Base):
  __tablename__ = 'episodes'
  __bind_key__ = 'podcast_db'

  id = db.Column(db.Integer, primary_key=True)
  title = db.Column(db.Text)
  author = db.Column(db.Text)
  summary = db.Column(MEDIUMTEXT)
  pub_date = db.Column(db.DateTime, default=db.func.current_timestamp())
  duration = db.Column(db.String(255))
  real_duration_written = db.Column(db.Boolean, nullable=False)
  audio_url = db.Column(db.Text)
  tags = db.Column(db.Text) # semicolon-separated
  recommendations_count = db.Column(db.Integer, nullable=False)

  series_id = \
    db.Column(db.Integer, db.ForeignKey('series.id', ondelete='CASCADE'))
  series = db.relationship('Series', backref='episodes')

  def __init__(self, **kwargs):
    self.title = kwargs.get('title', None)
    self.author = kwargs.get('author', None)
    self.summary = kwargs.get('summary', None)
    self.pub_date = kwargs.get('pub_date', None)
    self.duration = kwargs.get('duration', None)
    self.real_duration_written = kwargs.get('real_duration_written', False)
    self.audio_url = kwargs.get('audio_url', None)
    self.tags = ';'.join(kwargs.get('tags', []))
    self.series_id = kwargs.get('series_id')
    self.recommendations_count = kwargs.get('recommendations_count', 0)
