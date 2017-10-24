from elasticsearch_dsl import Q
from elasticsearch_dsl.function import *
from elasticsearch_dsl.query import *
from . import *

def search_series(term, offset, max_size):
  q = {"query": {
      "function_score": {
          "query": {
              'bool': {
                  "disable_coord": True,
                  "should": [
                      {"match": {
                          "title": {
                              "query": term,
                              "boost": 3,
                              "fuzziness": 1,
                          }
                      }},
                      {"match": {
                          "author": {
                              "query": term,
                              "boost": 2,
                              "fuzziness": 1
                          }
                      }},
                      {"match": {
                          "genres": {
                              "query": term,
                              "boost": 1,
                              "fuzziness": 1
                          }
                      }},
                  ],
                  "minimum_should_match": 1,
              }
          },
          "functions": {
              "field_value_factor": {
                  "field": 'subscribers_count',
                  "modifier": 'log2p'
              }
          }
      }
  }}
  query = Search(using=es, index='series-index').\
      update_from_dict(q)[int(offset):int(offset)+int(max_size)]
  results = query.execute()
  series_ids = [s['id'] for s in results]
  return series_ids

def search_episodes(term, offset, max_size):
  q = {"query": {
      "function_score": {
          "query": {
              'bool': {
                  "disable_coord": True,
                  "should": [
                      {"match": {
                          "title": {
                              "query": term,
                              "boost": 3,
                              "fuzziness": 1,
                          }
                      }},
                      {"match": {
                          "author": {
                              "query": term,
                              "boost": 2,
                              "fuzziness": 1
                          }
                      }},
                      {"match": {
                          "tags": {
                              "query": term,
                              "boost": 1,
                              "fuzziness": 0
                          }
                      }},
                      {"match": {
                          "summary": {
                              "query": term,
                              "boost": 0.1,
                              "fuzziness": 0
                          }
                      }}
                  ],
                  "minimum_should_match": 1,
              }
          },
          "functions": {
              "field_value_factor": {
                  "field": 'recommendations_count',
                  "modifier": 'log2p'
              }
          }
      }
  }}
  query = Search(using=es, index='episodes-index').\
      update_from_dict(q)[int(offset):int(offset)+int(max_size)]
  results = query.execute()
  episode_ids = [e['id'] for e in results]
  return episode_ids

def search_users(term, offset, max_size):
  q = {"query": {
      "function_score": {
          "query": {
              'bool': {
                  "disable_coord": True,
                  "should": [
                      {"match": {
                          "username": {
                              "query": term,
                              "boost": 2,
                              "fuzziness": 1,
                          }
                      }},
                      {"match": {
                          "google_id": {
                              "query": term,
                              "boost": 1,
                              "fuzziness": 1
                          }
                      }},
                  ],
                  "minimum_should_match": 1,
              }
          },
          "functions": {
              "field_value_factor": {
                  "field": 'followers_count',
                  "modifier": 'log2p'
              }
          }
      }
  }}
  query = Search(using=es, index='users-index').\
      update_from_dict(q)[int(offset):int(offset)+int(max_size)]
  results = query.execute()
  user_ids = [u['id'] for u in results]
  return user_ids
