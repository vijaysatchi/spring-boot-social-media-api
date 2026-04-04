package com.example.social_media.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    @Size(max = 255, message="Name must be at most 255 characters long.")
    private String caption; //kind of also a temporary requirement
}
