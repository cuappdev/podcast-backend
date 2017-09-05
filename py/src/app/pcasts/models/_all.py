from marshmallow_sqlalchemy import ModelSchema
from app.pcasts.models.user import *
from app.pcasts.models.series import *
from app.pcasts.models.episode import *
from app.pcasts.models.session import *
from app.pcasts.models.subscription import *
from app.pcasts.models.bookmark import *
from app.pcasts.models.recommendation import *
from app.pcasts.models.following import *

class UserSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = User

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

class EpisodeSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Episode

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
