from . import *

class SearchEpisodeController(AppDevController):

  def get_path(self):
    return '/search/episodes/<query>'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    search_title = request.view_args['query']
    offset = request.args['offset']
    max_search = request.args['max']
    possible_episodes = episodes_dao.\
        search_episode(search_title, offset, max_search)
    cleaned_episodes = []
    for e in possible_episodes:
      cleaned_episodes.append({
          "type": "episode",
          "seriesId": e.series.id,
          "seriesTitle" : e.series.title,
          "imageUrlSm": "None",
          "imageUrlLg": "None",
          "title": e.title,
          "author": e.author,
          "summary": e.summary,
          "pubDate": e.pub_date,
          "duration": e.duration,
          "audioUrl": e.audio_url,
          "tags": e.tags,
          "id": e.id,
      })
    return {'episodes': cleaned_episodes}
