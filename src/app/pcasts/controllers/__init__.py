from flask import request, render_template, jsonify, redirect
from appdev.controllers import *
from app.pcasts.dao import users_dao, \
  sessions_dao, \
  subscriptions_dao, \
  series_dao, \
  bookmarks_dao, \
  episodes_dao, \
  recommendations_dao, \
  followings_dao, \
  listening_histories_dao, \
  itunes_dao, \
  discover_dao, \
  feed_dao, \
  shares_dao, \
  notifications_dao


from app.pcasts.utils.authorize import *
from app.pcasts.models._all import *

# Serializers
user_schema = UserSchema()
session_schema = SessionSchema()
subscription_schema = SubscriptionSchema()
bookmark_schema = BookmarkSchema()
episode_schema = EpisodeSchema()
series_schema = SeriesSchema()
recommendation_schema = RecommendationSchema()
following_schema = FollowingSchema()
listening_history_schema = ListeningHistorySchema()
share_schema = ShareSchema()
