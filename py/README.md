# Podcasts API

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
APP_SETTINGS # e.g. config.DevelopmentConfig
````

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
mysql> CREATE DATABASE pcasts_db_dev;
mysql> CREATE DATABASE pcasts_podcast_db_dev;
mysql> \q
cd src
python manage.py db migrate # Migrates the DB
python manage.py db upgrade # Upgrades the DB
````

If the `migrations` directory is ever deleted, regenerate it with the following:

````bash
python manage.py db init --multidb
````
