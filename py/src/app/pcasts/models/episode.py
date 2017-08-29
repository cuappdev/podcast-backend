from . import *

class Episode(Base):
  __tablename__ = 'epsiodes'
  __bind_key__ = 'podcast_db'

  id = db.Column(db.Integer, primary_key=True)
  title = db.Column(db.String(255))
  author = db.Column(db.String(255))
  summary = db.Column(db.String(255))
  pub_date = db.Column(db.DateTime, default=db.func.current_timestamp())
  duration = db.Column(db.String(255))
  audio_url = db.Column(db.String(1000))
  tags = db.Column(db.String(1000)) # semicolon-separated

  series_id = \
    db.Column(db.Integer, db.ForeignKey('series.id', ondelete='CASCADE'))

  series = db.relationship('Series', cascade='all,delete', backref='episodes')
