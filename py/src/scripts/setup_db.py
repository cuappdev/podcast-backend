import sys
import os
import shutil
from script_utils import * # pylint: disable=W0403

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
set_app_settings()

from app import app # pylint: disable=C0413

def setup_dbs():
  print 'Setting up databases...'
  os.chdir('..')
  os.system('python manage.py db init --multidb')
  os.system('python manage.py db migrate')
  os.system('python manage.py db upgrade')
  os.chdir('scripts')
  print 'Finished setting up databases...'

def delete_migrations():
  try:
    os.chdir('..')
    shutil.rmtree('migrations')
    for db in ['DB', 'PODCAST_DB', 'TEST_DB', 'TEST_PODCAST_DB']:
      os.system('mysql --user={} --password={} {} '
                .format(os.environ['{}_USERNAME'.format(db)],
                        os.environ['{}_PASSWORD'.format(db)],
                        os.environ['{}_NAME'.format(db)])
                + '-e "drop table alembic_version"')
    os.chdir('scripts')
    print 'Migrations folder deleted...'
  except OSError:
    os.chdir('scripts')
    print 'No migrations folder to delete...'

if __name__ == '__main__':
  delete_migrations()
  setup_dbs()
