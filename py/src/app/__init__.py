from flask import Flask, render_template, jsonify, make_response
from flask_sqlalchemy import SQLAlchemy
import os

# Configure Flask app
app = Flask(__name__, static_url_path = '/templates')
app.config.from_object(os.environ['APP_SETTINGS'])
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True

# Database
db = SQLAlchemy(app)

# Import + Register Blueprints
# from app.pcasts import pcasts as pcasts
# app.register_blueprint(pcasts)

# HTTP error handling
@app.errorhandler(404)
def not_found(error):
  return render_template('404.html'), 404
