from flask import request, render_template, jsonify, redirect
from appdev.controllers import *
from app.pcasts.dao import users_dao, sessions_dao, subscriptions_dao, series_dao
from app.pcasts.utils.authorize import *

from app.pcasts.models._all import *

# Serializers
user_schema = UserSchema()
session_schema = SessionSchema()
subscription_schema = SubscriptionSchema()
