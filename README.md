# Populator

Microservice for creating database with music features from https://gitlab.com/nieznanm/extractor.

## Created tables

1. ID,NAME,URL,FEATURE_0,...,FEATURE_N-1
2. ID,GENRE

First table for counted features, second for arbitrarily known facts about the songs (can be expanded later).

## Prerequisites
- java 1.8
- MySQL 8.0.13
- Maven (to build locally, not need on destination server, where only jar or war will be necessary)

## Usage
Address: localhost:8080/initialization  
Method: POST  
Body (application/json):

    [
    {
        "url": "https://freepd.com/music/City%20Sunshine.mp3",
        "genre": "Rock",
        "name": "City Sunshine"
    },
    {
        "url": "https://freepd.com/music/Stereotype%20News.mp3",
        "genre": "Folk",
        "name": "Stereotype News"
    }
    ]
    
## How to run
1. Clone repository
2. Copy application-sample.yml and change its name to application.yml (file is located in resource folder)
3. Set database path, user and password in application.yml
4. Go to main folder and run: 'mvn package'
5. Go to "target" folder
6. Run: 'java -jar populator-0.0.1-SNAPSHOT.jar
