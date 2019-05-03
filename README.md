# Populator

Microservice for creating database with music features from https://gitlab.com/nieznanm/extractor.

## Created tables

1. ID,NAME,URL,FEATURE_0,...,FEATURE_N-1
2. ID,GENRE

First table for counted features, second for arbitrarily known facts about the songs (can be expanded later).

## Prerequisites
- java 1.8
- MySQL 8.0.13

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
    
