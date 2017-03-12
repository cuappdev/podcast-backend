# Podcast Backend 

Podcasts app-layer backend written in Spring 

## Running the app 

````bash
mvn spring-boot:run
````

## Required Environment Variables 

````bash
CLUSTER_HOST # host of Couchbase, localhost if dev-ing
SECRET_KEY # A 16-character string used for encryption 
USERS_BUCKET_PASSWORD # self-explanatory 
PODCASTS_BUCKET_PASSWORD # self-explanatory
FOLLOWERS_FOLLOWINGS_BUCKET_PASSWORD # self-explanatory
````
