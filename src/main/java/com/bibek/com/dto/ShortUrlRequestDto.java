package com.bibek.com.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlRequestDto {
    private String originalUrl;
    private Integer expireInMinutes;
}
