from . import *

class CreateDeleteShareController(AppDevController):

  def get_path(self):
    return '/shares/<id>/'

  def get_methods(self):
    return ['POST', 'DELETE']

  @authorize
  def content(self, **kwargs):
    sharer = kwargs.get('user')
    if request.method == 'POST':
      episode_id = request.view_args['id']
      sharee_ids = [int(sid) for sid in request.args['sharee_ids'].split(',')]
      for sharee_id in sharee_ids:
        share = shares_dao.create_share(sharer.id, sharee_id, episode_id)
        app.logger.info({
            'sharer': sharer.username,
            'sharee': share.sharee.username,
            'episode': episode_id,
            'message': 'share created'
        })
      return {'share': share_schema.dump(share).data}

    elif request.method == 'DELETE':
      share_id = request.view_args['id']
      share = shares_dao.delete_share(share_id)
      app.logger.info({
          'sharer': share.sharer.username,
          'sharee': share.sharee.username,
          'episode': share.episode_id,
          'message': 'share deleted'
      })

      return dict()
