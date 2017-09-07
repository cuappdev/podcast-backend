import datetime
import json
import os
import sys

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from app.pcasts.models._all import * # pylint: disable=C0413
from app.pcasts.utils.db_utils import * # pylint: disable=C0413

def get_json_file_names():
  files = []
  for _, _, filenames in os.walk('./data'):
    files.extend(filenames)
  return files

def build_json_lst(files):
  json_lst = []
  for f in files:
    file_stream = open('./data/{}'.format(f))
    data = json.load(file_stream)
    file_stream.close()
    json_lst.append(data)
  return json_lst

def load_up_db(json_lst):
  for element in json_lst:
    try:
      # Series work
      series_json = element['series']
      new_series = Series(
          id=series_json.get('id'),
          title=series_json.get('title'),
          country=series_json.get('country'),
          author=series_json.get('author'),
          image_url_lg=series_json.get('imageUrlLg'),
          image_url_sm=series_json.get('imageUrlSm'),
          feed_url=series_json.get('feedUrl'),
          genres=series_json.get('genres')
      )

      # Episode work
      episode_jsons = element['episodes']
      new_eps = []
      for ep_json in episode_jsons:
        # Specific formatting of pubdate
        pub_d = datetime.\
                datetime.\
                fromtimestamp(float(ep_json.get('pubDate'))) \
                if ep_json.get('pubDate') else \
                None
        new_eps.append(Episode(
            title=ep_json.get('title'),
            author=ep_json.get('author'),
            summary=ep_json.get('summary'),
            pub_date=pub_d,
            duration=ep_json.get('duration'),
            audio_url=ep_json.get('audioUrl'),
            tags=ep_json.get('tags'),
            series_id=ep_json.get('seriesId')
        ))
      commit_models([new_series] + new_eps)
      print 'Successfully saved: \'{}\''.format(new_series.title.encode('utf8'))
    except Exception as e: # pylint: disable=W0703
      print e
      print 'Error saving podcast'

if __name__ == '__main__':
  # Clear these tables
  Series.query.delete()
  Episode.query.delete()
  db_session_commit()

  # Grab info from files
  json_files = get_json_file_names()
  lst = build_json_lst(json_files)

  # Perform DB transactions
  load_up_db(lst)
