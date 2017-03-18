# Podcast Backend 

Podcasts app-layer backend written in Spring 

## Running the app 

````bash
mvn spring-boot:run
````

## Running tests 

````bash
mvn test
````

## Required Environment Variables 

````bash
CLUSTER_HOST # host of Couchbase, localhost if dev-ing
SECRET_KEY # A 16-character string used for encryption 
USERS_BUCKET_NAME
USERS_BUCKET_PASSWORD 
PODCASTS_BUCKET_NAME
PODCASTS_BUCKET_PASSWORD 
FOLLOWERS_FOLLOWINGS_BUCKET_NAME
FOLLOWERS_FOLLOWINGS_BUCKET_PASSWORD 
````

## API Documentation 

Found [`here`](http://docs.podcasts1.apiary.io/#)
