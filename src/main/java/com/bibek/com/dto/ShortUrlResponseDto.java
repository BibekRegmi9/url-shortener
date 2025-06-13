package com.bibek.com.dto;

import com.bibek.com.model.ShortUrl;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlResponseDto {
    private UUID id;
    private String originalUrl;
    private String shortCode;
    private Long visitCount;
    private String expirationDate;

    public ShortUrlResponseDto(ShortUrl shortUrl){
        this.id = shortUrl.getId();
        this.originalUrl = shortUrl.getOriginalUrl();
        this.shortCode = shortUrl.getShortCode();
        this.visitCount = shortUrl.getVisitCount() != null ? shortUrl.getVisitCount() : 0L;
        this.expirationDate = shortUrl.getExpirationDate().toString();
    }
}
