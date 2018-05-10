from . import *

def populate_episode(episode, user_id):
  episode.is_recommended = is_recommended_by_user(episode.id, user_id)
  episode.is_bookmarked = is_bookmarked_by_user(episode.id, user_id)
  episode.current_progress = current_progress_for_user(episode.id, user_id)

def get_episodes(episode_ids, user_id):
  if not episode_ids:
    return []
  episodes = Episode.query.filter(Episode.id.in_(episode_ids)).all()
  for e in episodes:
    populate_episode(e, user_id)
  return episodes

def get_episode(episode_id, user_id):
  episode = Episode.query.filter(Episode.id == episode_id).first()
  populate_episode(episode, user_id)
  return episode

def get_episode_by_title(title, user_id):
  episode = Episode.query.filter(Episode.title == title).first()
  populate_episode(episode, user_id)
  return episode

def get_episodes_by_series(series_id, offset, max_search, user_id):
  episodes = Episode.query.filter(Episode.series_id == series_id)\
      .order_by(Episode.pub_date.desc())\
      .offset(offset).limit(max_search).all()
  for e in episodes:
    populate_episode(e, user_id)
  return episodes

def get_episodes_by_many_series(user_id, series_ids, offset, max_search):
  episodes = Episode.query.filter(Episode.series_id.in_(series_ids)) \
      .order_by(Episode.pub_date.desc()) \
      .order_by(Episode.id) \
      .offset(offset).limit(max_search).all()
  for e in episodes:
    populate_episode(e, user_id)
  return episodes

def get_episodes_maxtime(user_id, series_ids, maxdatetime, page_size):
  episodes = Episode.query \
    .filter(Episode.series_id.in_(series_ids),
            Episode.pub_date <= maxdatetime) \
    .order_by(Episode.pub_date.desc()) \
    .limit(page_size) \
    .all()
  for e in episodes:
    populate_episode(e, user_id)
  return episodes

def clear_all_recommendations_counts():
  episodes = Episode.query.filter(Episode.recommendations_count > 0).all()
  for e in episodes:
    e.recommendations_count = 0
  db_utils.commit_models(episodes)

def is_bookmarked_by_user(episode_id, user_id):
  optional_bookmark = Bookmark.query \
    .filter(Bookmark.episode_id == episode_id, Bookmark.user_id == user_id) \
    .first()
  return optional_bookmark is not None

def is_recommended_by_user(episode_id, user_id):
  optional_recommendation = Recommendation.query \
    .filter(Recommendation.episode_id == episode_id,
            Recommendation.user_id == user_id) \
    .first()
  return optional_recommendation is not None

def current_progress_for_user(episode_id, user_id):
  lh = ListeningHistory.query \
    .filter(ListeningHistory.episode_id == episode_id,
            ListeningHistory.user_id == user_id) \
    .first()
  return None if not lh else lh.current_progress

def search_episode(search_name, offset, max_search, user_id):
  possible_episode_ids = [
      tup[0] for tup in
      Episode.query.\
      with_entities(Episode.id).\
      filter(Episode.title.like('%' + search_name + '%')).\
      offset(offset).\
      limit(max_search).\
      all()
  ]

  return get_episodes(possible_episode_ids, user_id)

# Second ordering by id to resolve ties showing up at different offsets
def get_top_episodes_by_recommenders(offset, max_search, user_id):
  found_episode_ids = [
      tup[0] for tup in
      Episode.query.\
      with_entities(Episode.id, Episode.recommendations_count).\
      order_by(Episode.recommendations_count.desc()).\
      order_by(Episode.id).\
      offset(offset).\
      limit(max_search).\
      all()
  ]
  found_episodes = get_episodes(found_episode_ids, user_id)
  return order_by_ids(found_episode_ids, found_episodes)

def get_series_ids_from_episodes(episode_ids):
  episodes = Episode.query.with_entities(Episode.series_id)\
      .filter(Episode.id.in_(episode_ids)).distinct().all()
  return [episode[0] for episode in episodes]
