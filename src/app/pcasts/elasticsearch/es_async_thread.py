import threading
import time
from app.pcasts.elasticsearch import populate

class EsAsyncThread(object):

  def __init__(self, interval=600):
    self.interval = interval

    thread = threading.Thread(target=self.run, args=())
    thread.daemon = True
    thread.start()

  def run(self):
    while True:
      populate.populate()
      time.sleep(self.interval)
