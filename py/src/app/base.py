from marshmallow_sqlalchemy import ModelSchema
from . import db

class Base(db.Model):
  __abstract__ = True
  created_at = db.Column(db.DateTime, default = db.func.current_timestamp())
  updated_at = db.Column(db.DateTime, default = db.func.current_timestamp())
