from marshmallow_sqlalchemy import ModelSchema
from app.pcasts.models.user import *
from app.pcasts.models.series import *
from app.pcasts.models.episode import *
from app.pcasts.models.session import *
from app.pcasts.models.bookmark import *

class UserSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = User

class SessionSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Session

class EpisodeSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Episode

class BookmarkSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Bookmark
  episode = fields.Nested('EpisodeSchema', many=False)
  user = fields.Nested('UserSchema', many=False)
