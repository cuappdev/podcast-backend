from appdev.controllers import *
from . import *

class GetSharesController(AppDevController):

  def get_path(self):
    return '/shares/'

  def get_methods(self):
    return ['GET']

  @authorize
  def content(self, **kwargs):
    user = kwargs.get('user')
    offset = request.args['offset']
    max_shares = request.args['max']
    shares = shares_dao.get_shared_with_user(user.id, max_shares, offset)
    return {'shares': [share_schema.dump(s).data for s in shares]}
