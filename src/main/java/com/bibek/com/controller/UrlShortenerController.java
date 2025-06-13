package com.bibek.com.controller;

import com.bibek.com.dto.ShortUrlRequestDto;
import com.bibek.com.dto.ShortUrlResponseDto;
import com.bibek.com.service.UrlShortenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/url-shortener")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody ShortUrlRequestDto dto) {
        return ResponseEntity.ok(urlShortenerService.create(dto));
    }

    @GetMapping("/get-originalurl/{shortUrl}")
    public ResponseEntity<Optional<String>> getByOriginalUrl(@PathVariable String shortUrl) {
        return ResponseEntity.ok(urlShortenerService.getOriginalUrl(shortUrl));
    }


    @GetMapping
    public ResponseEntity<List<ShortUrlResponseDto>> getAll() {
        return ResponseEntity.ok(urlShortenerService.getAll());
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ShortUrlResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(urlShortenerService.getShortUrlById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        urlShortenerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
