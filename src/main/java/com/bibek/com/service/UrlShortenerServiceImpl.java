package com.bibek.com.service;

import com.bibek.com.dto.ShortUrlRequestDto;
import com.bibek.com.dto.ShortUrlResponseDto;
import com.bibek.com.exception.InvalidUrlException;
import com.bibek.com.model.ShortUrl;
import com.bibek.com.repository.ShortUrlRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final ShortUrlRepo shortUrlRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String URL_REGEX_VALIDATOR = "^(http|https)://([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}(/\\S*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX_VALIDATOR, Pattern.CASE_INSENSITIVE);


    public UrlShortenerServiceImpl(ShortUrlRepo shortUrlRepo, RedisTemplate<String, String> redisTemplate) {
        this.shortUrlRepo = shortUrlRepo;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public String create(ShortUrlRequestDto requestDto) {
//        Optional<ShortUrl> previousShortUrl = shortUrlRepo.findLatestByOriginalUrl(requestDto.getOriginalUrl());
//        if(previousShortUrl.isPresent() && previousShortUrl.get().getExpirationDate().isBefore(LocalDateTime.now())){
//            throw new RuntimeException("The Url time is expired, again create new.");
//        }


        // validate the url format
        validateUrl(requestDto);

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(requestDto.getOriginalUrl());
        shortUrl.setExpirationDate(LocalDateTime.now().plusMinutes(requestDto.getExpireInMinutes()));
        shortUrl = shortUrlRepo.save(shortUrl);

        String shortUrlCode = Base64.getUrlEncoder().withoutPadding().encodeToString(("id" + shortUrl.getId()).getBytes());
        shortUrl.setShortCode(shortUrlCode);
        redisTemplate.opsForValue().set(shortUrlCode, requestDto.getOriginalUrl(), Duration.ofMinutes(requestDto.getExpireInMinutes()));

        return shortUrlRepo.save(shortUrl).getShortCode();
    }

    private static void validateUrl(ShortUrlRequestDto requestDto) {
        Matcher matcher = URL_PATTERN.matcher(requestDto.getOriginalUrl());
        if (!matcher.matches()) {
            throw new InvalidUrlException("invalid url format");
        }
    }

    @Override
    public ShortUrlResponseDto getShortUrlById(UUID id) {
        ShortUrl shortUrl = shortUrlRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("URL not found"));
        return new ShortUrlResponseDto(shortUrl);
    }

    @Override
    public Optional<String> getOriginalUrl(String shortUrl) {
        // check if present in cache
        String cachedUrl = redisTemplate.opsForValue().get(shortUrl);
        if (cachedUrl != null) {
            incrementUrlAccessCount(shortUrl);
            return Optional.of(cachedUrl);
        }

        // check in db
        Optional<ShortUrl> optionalUrl = shortUrlRepo.findByShortCode(shortUrl);
        if (optionalUrl.isEmpty()) {
            return Optional.empty();
        }
        ShortUrl savedShortUrl = optionalUrl.get();
        if (savedShortUrl.getExpirationDate() != null && savedShortUrl.getExpirationDate().isBefore(LocalDateTime.now())) {
            return Optional.of("URL is expired. Create again with new Expiration time.");
        }

        //calculate remaining ttl from db expiration
        if (savedShortUrl.getExpirationDate() != null) {
            Duration ttl = Duration.between(LocalDateTime.now(), savedShortUrl.getExpirationDate());
            if (!ttl.isNegative() && !ttl.isZero()) {
                redisTemplate.opsForValue().set(shortUrl, savedShortUrl.getOriginalUrl(), ttl);
            }
        } else {
            redisTemplate.opsForValue().set(shortUrl, savedShortUrl.getOriginalUrl(), Duration.ofHours(1));
        }

        incrementUrlAccessCount(shortUrl);
        return Optional.of(savedShortUrl.getOriginalUrl());
    }

    private void incrementUrlAccessCount(String shortCode) {
        Optional<ShortUrl> shortUrl = shortUrlRepo.findByShortCode(shortCode);
        if (shortUrl.isPresent()) {
            shortUrl.get().setVisitCount(shortUrl.get().getVisitCount() + 1);
            shortUrlRepo.save(shortUrl.get());
        }

        redisTemplate.opsForValue().increment("count:" + shortCode, 1);
    }

    @Override
    public List<ShortUrlResponseDto> getAll() {
        List<ShortUrl> shortUrlList = shortUrlRepo.findAll();
        return shortUrlList.stream().map(ShortUrlResponseDto::new).toList();
    }

    @Override
    public void delete(UUID id) {
        ShortUrl shortUrl = shortUrlRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Short URL not found."));

        String shortCode = shortUrl.getShortCode();

        redisTemplate.delete(shortCode);
        redisTemplate.delete("count:" + shortCode);

        shortUrlRepo.deleteById(id);
    }
}
