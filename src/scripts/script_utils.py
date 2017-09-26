import sys
import os

def set_app_settings():
  if len(sys.argv) > 1:
    if sys.argv[1] == 'test':
      os.environ['APP_SETTINGS'] = 'config.TestingConfig'
    elif sys.argv[1] == 'dev':
      os.environ['APP_SETTINGS'] = 'config.DevelopmentConfig'
    else:
      raise Exception("Invalid app setting argument")
  else:
    raise Exception("App setting required")
