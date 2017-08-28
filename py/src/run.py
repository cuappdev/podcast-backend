from app import app

if __name__ == '__main__':
  PORT = 5000
  HOST = '0.0.0.0'
  print 'Server running on {}:{}'.format(HOST, PORT)
  app.run(host=HOST, port=PORT)
