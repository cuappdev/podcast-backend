from flask import Blueprint
from app import *

# PCasts Blueprint
pcasts = Blueprint('pcasts', __name__, url_prefix='/api/v1')

# Import all models
from app.pcasts.models._all import *

# Import all controllers
from app.pcasts.controllers.google_sign_in_controller import *
from app.pcasts.controllers.get_me_controller import *
from app.pcasts.controllers.series_subscriptions_controller import *
from app.pcasts.controllers.get_user_subscriptions_controller import *
from app.pcasts.controllers.create_delete_bookmark_controller import *
from app.pcasts.controllers.get_bookmarks_controller import *
from app.pcasts.controllers.get_create_delete_recommendation_controller import *
from app.pcasts.controllers.get_user_recommendations_controller import *
from app.pcasts.controllers.create_delete_following_controller import *
from app.pcasts.controllers.get_user_followers_controller import *
from app.pcasts.controllers.get_user_followings_controller import *
from app.pcasts.controllers.delete_listening_history_controller import *
from app.pcasts.controllers.listening_history_controller import *
from app.pcasts.controllers.clear_listening_history_controller import *
from app.pcasts.controllers.get_feed_controller import *
from app.pcasts.controllers.update_session_controller import *
from app.pcasts.controllers.sign_out_controller import *
from app.pcasts.controllers.search_episode_controller import *
from app.pcasts.controllers.search_series_controller import *
from app.pcasts.controllers.search_users_controller import *
from app.pcasts.controllers.search_all_controller import *
from app.pcasts.controllers.get_episodes_controller import *
from app.pcasts.controllers.series_controller import *
from app.pcasts.controllers.get_user_by_id_controller import *
from app.pcasts.controllers.update_username_controller import *
from app.pcasts.controllers.discover_series_controller import *
from app.pcasts.controllers.discover_episodes_controller import *
from app.pcasts.controllers.facebook_sign_in_controller import *
from app.pcasts.controllers.merge_account_controller import *
from app.pcasts.controllers.search_itunes_controller import *

controllers = [
    GoogleSignInController(),
    GetMeController(),
    SeriesSubscriptionsController(),
    GetUserSubscriptionsController(),
    CreateDeleteBookmarkController(),
    GetBookmarksController(),
    GetCreateDeleteRecommendationController(),
    GetUserRecommendationsController(),
    CreateDeleteFollowingController(),
    GetUserFollowersController(),
    GetUserFollowingsController(),
    DeleteListeningHistoryController(),
    ListeningHistoryController(),
    ClearListeningHistoryController(),
    GetFeedController(),
    UpdateSessionController(),
    SignOutController(),
    SearchEpisodeController(),
    SearchSeriesController(),
    SearchUsersController(),
    SearchAllController(),
    GetEpisodesController(),
    SeriesController(),
    GetUserByIdController(),
    UpdateUsernameController(),
    DiscoverSeriesController(),
    DiscoverEpisodesController(),
    FacebookSignInController(),
    MergeAccountController(),
    SearchiTunesController(),
]

# Setup all controllers
for controller in controllers:
  pcasts.add_url_rule(
      controller.get_path(),
      controller.get_name(),
      controller.response,
      methods=controller.get_methods()
  )
