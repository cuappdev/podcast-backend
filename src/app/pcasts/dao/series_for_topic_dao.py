from . import *

def get_series_list_for_topic(tid):
  tid = 1 if tid == 'all' else int(tid)
  optional_model = SeriesForTopic.query \
    .filter(SeriesForTopic.topic_id == tid).first()
  if optional_model:
    return [int(sid) for sid in optional_model.series_list.split(',')]
  else:
    raise Exception('No topic id {} exists'.format(tid))
