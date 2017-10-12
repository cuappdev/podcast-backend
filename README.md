# Podcasts API

## Virtual Environment

Make sure you have [`virtualenv`](https://virtualenv.pypa.io/en/stable/) installed.  
On creating a `virtualenv` called `venv`, run the following:

````bash
pip install git+https://github.com/cuappdev/appdev.py.git#egg=appdev.py
pip install -r requirements.txt
````

## Setting up Database

Ensure you have `mysql` plus command line tools setup:

````bash
mysql
mysql> CREATE DATABASE pcasts_db_dev CHARACTER SET utf8mb4;
mysql> CREATE DATABASE pcasts_podcast_db_dev CHARACTER SET utf8mb4;
mysql> CREATE DATABASE test_pcasts_db_dev CHARACTER SET utf8mb4;
mysql> CREATE DATABASE test_pcasts_podcast_db_dev CHARACTER SET utf8mb4;
mysql> \q
cd src/scripts

python setup_db.py dev
python setup_db.py test
````

If the `migrations` directory is ever deleted, you can regenerate it with the following:

````bash
python manage.py db init --multidb
````

## Environment Variables

I highly recommend [`autoenv`](https://github.com/kennethreitz/autoenv).
The required environment variables for this API are the following:

````bash
DB_USERNAME
DB_PASSWORD
DB_HOST
DB_NAME
PODCAST_DB_USERNAME
PODCAST_DB_PASSWORD
PODCAST_DB_HOST
PODCAST_DB_NAME
TEST_DB_USERNAME
TEST_DB_PASSWORD
TEST_DB_HOST
TEST_DB_NAME
TEST_PODCAST_DB_USERNAME
TEST_PODCAST_DB_PASSWORD
TEST_PODCAST_DB_HOST
TEST_PODCAST_DB_NAME
APP_SETTINGS # e.g. config.DevelopmentConfig
````

If using `autoenv` for local development, create a `.env` file, like the sample below:
````bash
export DB_USERNAME=CHANGEME
export DB_PASSWORD=CHANGEME
export DB_HOST=localhost
export DB_NAME=pcasts_db_dev
export PODCAST_DB_USERNAME=CHANGEME
export PODCAST_DB_PASSWORD=CHANGEME
export PODCAST_DB_HOST=localhost
export PODCAST_DB_NAME=pcasts_podcast_db_dev
export TEST_DB_USERNAME=CHANGEME
export TEST_DB_PASSWORD=CHANGEME
export TEST_DB_HOST=localhost
export TEST_DB_NAME=test_pcasts_db_dev
export TEST_PODCAST_DB_USERNAME=CHANGEME
export TEST_PODCAST_DB_PASSWORD=CHANGEME
export TEST_PODCAST_DB_HOST=localhost
export TEST_PODCAST_DB_NAME=test_pcasts_podcast_db_dev
export APP_SETTINGS=config.DevelopmentConfig
````

In the `/tests` directory, create another `.env` file that changes the `APP_SETTINGS`:
````bash
export APP_SETTINGS=config.TestingConfig
````
## Loading in Test Data
From the root directory, run:
````
python src/scripts/load_data.py dev
python src/scripts/load_data.py test
````

## Testing
To run all unit tests, from the `/tests` directory, run:
````
./test.sh
````

To run a single test, from the `/tests` directory, run:
````
./test.sh test_file_name.py
````
