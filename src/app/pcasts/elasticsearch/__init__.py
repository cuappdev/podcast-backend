from app import constants
from app.pcasts.models._all import *
from elasticsearch import Elasticsearch
from elasticsearch import helpers
from elasticsearch_dsl import Search

es = Elasticsearch(
    [os.environ.get('ELASTICSEARCH_ADDRESS')],
    retry_on_timeout=True
)

episode_schema = EpisodeSchema()
series_schema = SeriesSchema()
user_schema = UserSchema()
