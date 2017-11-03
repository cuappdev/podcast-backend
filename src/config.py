import os

# Ensure Python path
basedir = os.path.abspath(os.path.dirname(__file__))

# Database info
DB_USERNAME = os.environ['DB_USERNAME']
DB_PASSWORD = os.environ['DB_PASSWORD']
DB_HOST = os.environ['DB_HOST']
DB_NAME = os.environ['DB_NAME']
DB_URL = 'mysql://{}:{}@{}/{}?charset=utf8mb4'.format(
    DB_USERNAME,
    DB_PASSWORD,
    DB_HOST,
    DB_NAME
)

# Separate DB for podcast-specific stuff
PODCAST_DB_USERNAME = os.environ['PODCAST_DB_USERNAME']
PODCAST_DB_PASSWORD = os.environ['PODCAST_DB_PASSWORD']
PODCAST_DB_HOST = os.environ['PODCAST_DB_HOST']
PODCAST_DB_NAME = os.environ['PODCAST_DB_NAME']
PODCAST_DB_URL = 'mysql://{}:{}@{}/{}?charset=utf8mb4'.format(
    PODCAST_DB_USERNAME,
    PODCAST_DB_PASSWORD,
    PODCAST_DB_HOST,
    PODCAST_DB_NAME
)

# Analog of database for testing purposes
TEST_DB_USERNAME = os.environ.get('TEST_DB_USERNAME')
TEST_DB_PASSWORD = os.environ.get('TEST_DB_PASSWORD')
TEST_DB_HOST = os.environ.get('TEST_DB_HOST')
TEST_DB_NAME = os.environ.get('TEST_DB_NAME')
TEST_DB_URL = 'mysql://{}:{}@{}/{}?charset=utf8mb4'.format(
    TEST_DB_USERNAME,
    TEST_DB_PASSWORD,
    TEST_DB_HOST,
    TEST_DB_NAME
)

# Analog of podcast database for testing purposes
TEST_PODCAST_DB_USERNAME = os.environ.get('TEST_PODCAST_DB_USERNAME')
TEST_PODCAST_DB_PASSWORD = os.environ.get('TEST_PODCAST_DB_PASSWORD')
TEST_PODCAST_DB_HOST = os.environ.get('TEST_PODCAST_DB_HOST')
TEST_PODCAST_DB_NAME = os.environ.get('TEST_PODCAST_DB_NAME')
TEST_PODCAST_DB_URL = 'mysql://{}:{}@{}/{}?charset=utf8mb4'.format(
    TEST_PODCAST_DB_USERNAME,
    TEST_PODCAST_DB_PASSWORD,
    TEST_PODCAST_DB_HOST,
    TEST_PODCAST_DB_NAME
)

# Facebook API information
FACEBOOK_APP_ID = os.environ.get('FACEBOOK_APP_ID')
FACEBOOK_APP_SECRET = os.environ.get('FACEBOOK_APP_SECRET')
FACEBOOK_API_PERMISSIONS = 'email,user_friends'

# Test Case Constants
NUM_TEST_USERES = 2

class Config(object):
  DEBUG = False
  TESTING = False
  CSRF_ENABLED = True
  CSRF_SESSION_KEY = "secret"
  SECRET_KEY = "not_this"
  THREADS_PER_PAGE = 2

  # Mounting our DBs
  SQLALCHEMY_DATABASE_URI = DB_URL
  SQLALCHEMY_BINDS = {'podcast_db': PODCAST_DB_URL}

class ProductionConfig(Config):
  DEBUG = False

class StagingConfig(Config):
  DEVELOPMENT = True
  DEBUG = True

class DevelopmentConfig(Config):
  DEVELOPMENT = True
  DEBUG = True

class TestingConfig(Config):
  TESTING = True
  SQLALCHEMY_DATABASE_URI = TEST_DB_URL
  SQLALCHEMY_BINDS = {'podcast_db': TEST_PODCAST_DB_URL}
