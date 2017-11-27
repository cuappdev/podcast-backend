import os
import datetime
import logging
from logging.handlers import RotatingFileHandler
from flask import Flask, render_template, jsonify, make_response
from flask_sqlalchemy import SQLAlchemy
import config

# Configure Flask app
app = Flask(__name__, static_url_path='/templates')
app.config.from_object(os.environ['APP_SETTINGS'])
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True

# Database
db = SQLAlchemy(app)

# Import + Register Blueprints
from app.pcasts import pcasts as pcasts # pylint: disable=C0413
app.register_blueprint(pcasts)

# HTTP error handling
@app.errorhandler(404)
def not_found(error):
  return render_template('404.html'), 404

from app.pcasts_logger import PcastsJsonFormatter
# Initialize log handler
if not app.config['TESTING']:
  date_tag = datetime.datetime.now().strftime('%Y-%b-%d')
  logs_path = '{}/logs'.format(app.root_path)
  if not os.path.exists(logs_path):
    os.makedirs(logs_path)
  log_handler = RotatingFileHandler(
      '{}/info-{}.log'.format(logs_path, date_tag),
      maxBytes=10*1024*1024, backupCount=5
  )
  formatter = PcastsJsonFormatter('(timestamp) (level) (message)')
  log_handler.setFormatter(formatter)
  log_handler.setLevel(logging.INFO)
  app.logger.addHandler(log_handler)
