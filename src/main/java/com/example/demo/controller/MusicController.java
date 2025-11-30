package com.example.demo.controller;

import com.example.demo.model.MusicTrack;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private List<MusicTrack> musicLibrary = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(1);

    // Инициализация тестовых данных
    public MusicController() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        musicLibrary.add(new MusicTrack(idCounter.getAndIncrement(),
                "Bohemian Rhapsody", "Queen", "A Night at the Opera",
                "Rock", 354, 1975));

        musicLibrary.add(new MusicTrack(idCounter.getAndIncrement(),
                "Hotel California", "Eagles", "Hotel California",
                "Rock", 391, 1977));

        musicLibrary.add(new MusicTrack(idCounter.getAndIncrement(),
                "Blinding Lights", "The Weeknd", "After Hours",
                "Pop", 200, 2020));

        musicLibrary.add(new MusicTrack(idCounter.getAndIncrement(),
                "Smooth Criminal", "Michael Jackson", "Bad",
                "Pop", 258, 1987));
    }

    // Добавление нового трека
    @PostMapping("/tracks")
    public ResponseEntity<?> addTrack(@RequestBody MusicTrack track) {
        try {
            track.setId(idCounter.getAndIncrement());
            musicLibrary.add(track);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Track added successfully");
            response.put("track", track);
            response.put("totalTracks", musicLibrary.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to add track: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Получение всех треков
    @GetMapping("/tracks")
    public ResponseEntity<List<MusicTrack>> getAllTracks() {
        return ResponseEntity.ok(musicLibrary);
    }

    // Получение трека по ID
    @GetMapping("/tracks/{id}")
    public ResponseEntity<?> getTrackById(@PathVariable Long id) {
        return musicLibrary.stream()
                .filter(track -> track.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Поиск треков по названию
    @GetMapping("/tracks/search")
    public ResponseEntity<List<MusicTrack>> searchTracks(@RequestParam String title) {
        List<MusicTrack> results = musicLibrary.stream()
                .filter(track -> track.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Получение треков по исполнителю
    @GetMapping("/tracks/artist/{artist}")
    public ResponseEntity<List<MusicTrack>> getTracksByArtist(@PathVariable String artist) {
        List<MusicTrack> results = musicLibrary.stream()
                .filter(track -> track.getArtist().equalsIgnoreCase(artist))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Получение треков по жанру
    @GetMapping("/tracks/genre/{genre}")
    public ResponseEntity<List<MusicTrack>> getTracksByGenre(@PathVariable String genre) {
        List<MusicTrack> results = musicLibrary.stream()
                .filter(track -> track.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Обновление трека
    @PutMapping("/tracks/{id}")
    public ResponseEntity<?> updateTrack(@PathVariable Long id, @RequestBody MusicTrack updatedTrack) {
        for (int i = 0; i < musicLibrary.size(); i++) {
            MusicTrack track = musicLibrary.get(i);
            if (track.getId().equals(id)) {
                updatedTrack.setId(id);
                musicLibrary.set(i, updatedTrack);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Track updated successfully");
                response.put("track", updatedTrack);
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Удаление трека
    @DeleteMapping("/tracks/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable Long id) {
        boolean removed = musicLibrary.removeIf(track -> track.getId().equals(id));
        if (removed) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Track deleted successfully");
            response.put("totalTracks", musicLibrary.size());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Получение статистики
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTracks", musicLibrary.size());

        // Количество треков по жанрам
        Map<String, Long> genreStats = musicLibrary.stream()
                .collect(Collectors.groupingBy(MusicTrack::getGenre, Collectors.counting()));
        stats.put("tracksByGenre", genreStats);

        // Количество треков по годам
        Map<Integer, Long> yearStats = musicLibrary.stream()
                .collect(Collectors.groupingBy(MusicTrack::getYear, Collectors.counting()));
        stats.put("tracksByYear", yearStats);

        // Общая продолжительность музыки
        int totalDuration = musicLibrary.stream()
                .mapToInt(MusicTrack::getDuration)
                .sum();
        stats.put("totalDurationSeconds", totalDuration);

        // Форматированная продолжительность
        int totalMinutes = totalDuration / 60;
        int totalHours = totalMinutes / 60;
        int remainingMinutes = totalMinutes % 60;
        int remainingSeconds = totalDuration % 60;
        String formattedDuration = String.format("%d:%02d:%02d", totalHours, remainingMinutes, remainingSeconds);
        stats.put("totalDurationFormatted", formattedDuration);

        return ResponseEntity.ok(stats);
    }

    // Получение количества треков
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getTrackCount() {
        Map<String, Integer> response = new HashMap<>();
        response.put("count", musicLibrary.size());
        return ResponseEntity.ok(response);
    }
}