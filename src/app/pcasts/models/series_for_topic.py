from sqlalchemy import UniqueConstraint
from . import *

class SeriesForTopic(Base):
  __tablename__ = 'series_for_topic'

  topic_id = db.Column(db.Integer, primary_key=True)
  series_list = db.Column(db.Text) # comma-separated

  def __init__(self, **kwargs):
    self.topic_id = kwargs.get('topic_id')
    self.series_list = kwargs.get('series_list')
