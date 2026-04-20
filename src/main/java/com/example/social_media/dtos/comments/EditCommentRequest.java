package com.example.social_media.dtos.comments;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditCommentRequest {
    @NotNull(message = "Text cannot be blank.")
    @Size(min = 1, max = 255, message = "Text length must be between 1 and 255 characters long.")
    private String content;
}
