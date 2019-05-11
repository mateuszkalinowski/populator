# Populator

Microservice for creating database with music features from https://gitlab.com/nieznanm/extractor.

## Created tables

1. ID,FEATURE_0,...,FEATURE_N-1
2. ID,NAME,URL,GENRE

First table for counted features, second for arbitrarily known facts about the songs (can be expanded later).

## Prerequisites
- java 1.8
- MySQL 8.0.13
- Maven (to build locally, not need on destination server, where only jar or war will be necessary)

## Usage
#### Initialization
Address: localhost:8080/initialization  
Method: POST  
Body (application/json):

    [
    {
        "id": 1,
        "url": "https://freepd.com/music/City%20Sunshine.mp3",
        "genre": "Rock",
        "name": "City Sunshine"
    },
    {
        "id": 2,
        "url": "https://freepd.com/music/Stereotype%20News.mp3",
        "genre": "Folk",
        "name": "Stereotype News"
    }
    ]
Id must be a number, and unique for each song
#### Songs recognition
Address: localhost:8080/recognize  
Method: POST  
Parameters:  
song - binary (song to recognize)  
numberOfSongs - a number (optional parameter (default value - 3), sets how many top matching songs will be returned)  
genre - string (optional parameter, if set, only songs from selected genre will be taken into account)
Sample response:  

    [
    {
        "id": 2
        "mse": 0
    },
    {
        "id": 1
        "mse": 0.7105980199986179
    }
    ]
## How to run
1. Clone repository
2. Copy application-sample.yml and change its name to application.yml (file is located in resource folder)
3. Set database path, user and password in application.yml
4. Set extractor address in applicaiton.yml (default: http://localhost:5000)
4. Go to main folder and run: 'mvn package'
5. Go to "target" folder
6. Run: 'java -jar populator-0.0.1-SNAPSHOT.jar
