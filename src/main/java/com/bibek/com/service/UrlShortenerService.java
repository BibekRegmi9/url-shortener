package com.bibek.com.service;

import com.bibek.com.dto.ShortUrlRequestDto;
import com.bibek.com.dto.ShortUrlResponseDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UrlShortenerService {
    String create(ShortUrlRequestDto requestDto);
    ShortUrlResponseDto getShortUrlById(UUID id);
    Optional<String> getOriginalUrl(String shortUrl);
    List<ShortUrlResponseDto> getAll();
    void delete(UUID id);
}
