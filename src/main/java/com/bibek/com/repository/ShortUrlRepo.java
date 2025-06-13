package com.bibek.com.repository;

import com.bibek.com.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShortUrlRepo extends JpaRepository<ShortUrl, UUID> {

    Optional<ShortUrl> findByShortCode(String shortUrl);


}
