from . import *

def get_series_list_for_topic(tid, offset, max_search):
  tid = 1 if tid == 'all' else int(tid)
  optional_model = SeriesForTopic.query \
    .filter(SeriesForTopic.topic_id == tid).first()
  if optional_model:
    series_list = [int(sid) for sid in optional_model.series_list.split(',')]
    end = min(offset + max_search, len(series_list))
    return series_list[offset:end]
  else:
    raise Exception('No topic id {} exists'.format(tid))
