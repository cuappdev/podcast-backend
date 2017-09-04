from marshmallow_sqlalchemy import ModelSchema
from app.pcasts.models.user import *
from app.pcasts.models.series import *
from app.pcasts.models.episode import *
from app.pcasts.models.session import *
from app.pcasts.models.subscription import *

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
