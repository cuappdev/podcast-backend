import os

TEST_USER_GOOGLE_ID1 = 'default_google_id1'
TEST_USER_GOOGLE_ID2 = 'default_google_id2'
TEST_USER_GOOGLE_ID3 = 'default_google_id3'

NUM_TEST_USERS = 3

# Platforms
FACEBOOK = "Facebook"
GOOGLE = "Google"

FACEBOOK_API_PERMISSIONS = 'email,user_friends'
TEST_APP_SETTING = 'config.TestingConfig'
ELASTICSEARCH_PATH = \
    '{}/pcasts/elasticsearch'.format(
        os.path.dirname(os.path.realpath(__file__))
    )
ELASTICSEARCH_PICKLE_PATH = \
    '{}/updated.p'.format(ELASTICSEARCH_PATH)
ELASTICSEARCH_PICKLE_KEY = 'last_updated'
