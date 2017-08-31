from marshmallow_sqlalchemy import ModelSchema
from app.pcasts.models.user import *
from app.pcasts.models.series import *
from app.pcasts.models.episode import *
from app.pcasts.models.session import *

class UserSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = User

class SessionSchema(ModelSchema):
  class Meta(ModelSchema.Meta):
    model = Session
