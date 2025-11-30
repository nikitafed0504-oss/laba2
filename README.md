http://localhost:8080/api/hello 

1POST http://localhost:8080/api/music/tracks
Content-Type: application/json
Body (raw JSON):

json
{
    "title": "Bohemian Rhapsody",
    "artist": "Queen",
    "album": "A Night at the Opera",
    "genre": "Rock",
    "duration": 354,
    "year": 1975
}

2. READ (Чтение) - GET
Получить все треки
text
GET http://localhost:8080/api/music/tracks

3. UPDATE (Обновление) - PUT
Обновить трек по ID
text
PUT http://localhost:8080/api/music/tracks/1
Content-Type: application/json
Body (raw JSON):

json
{
    "title": "Bohemian Rhapsody (Remastered)",
    "artist": "Queen",
    "album": "A Night at the Opera (Remastered)",
    "genre": "Classic Rock",
    "duration": 360,
    "year": 1975
}

4. DELETE (Удаление) - DELETE
Удалить трек по ID
text
DELETE http://localhost:8080/api/music/tracks/1
