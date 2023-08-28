package com.example.princesstown.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostByLocationRequestDto {
    private Double latitude;
    private Double longitude;
    private Double radius;
}
