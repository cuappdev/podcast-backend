from marshmallow_sqlalchemy import ModelSchema
from marshmallow import fields
from app.pcasts.models.user import *
from app.pcasts.models.series import *
from app.pcasts.models.episode import *
from app.pcasts.models.session import *
from app.pcasts.models.subscription import *
from app.pcasts.models.bookmark import *
from app.pcasts.models.recommendation import *
from app.pcasts.models.following import *
from app.pcasts.models.listening_history import *
from app.pcasts.models.share import *
from app.pcasts.models.ignored_users import *
from app.pcasts.models.series_for_topic import *

class UserSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = User
  is_following = fields.Boolean()

class SessionSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Session

class SubscriptionSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Subscription
  user = fields.Nested('UserSchema', many=False)
  series = fields.Nested("SeriesSchema", many=False)

class SeriesSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Series
  last_updated = fields.DateTime()
  is_subscribed = fields.Boolean()

class EpisodeSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Episode
  is_recommended = fields.Boolean()
  is_bookmarked = fields.Boolean()
  current_progress = fields.Float()
  series = fields.Nested(
      'SeriesSchema',
      only=['id', 'image_url_lg', 'image_url_sm', 'title'])

class BookmarkSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Bookmark
  episode = fields.Nested('EpisodeSchema', many=False)
  user = fields.Nested('UserSchema', many=False)

class RecommendationSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
      model = Recommendation
  episode = fields.Nested('EpisodeSchema', many=False)
  user = fields.Nested('UserSchema', many=False)

class FollowingSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Following
  follower = fields.Nested('UserSchema', many=False)
  followed = fields.Nested('UserSchema', many=False)

class ListeningHistorySchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = ListeningHistory
  episode = fields.Nested('EpisodeSchema', many=False)
  user = fields.Nested('UserSchema', many=False)

class ShareSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Share
  sharer = fields.Nested('UserSchema', many=False)
  sharee = fields.Nested('UserSchema', many=False)
  episode = fields.Nested('EpisodeSchema', many=False)
