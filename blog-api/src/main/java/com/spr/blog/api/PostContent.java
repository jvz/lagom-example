package com.spr.blog.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Wither;

/**
 * Model for holding a blog post.
 *
 * @author Matt Sicker
 */
@Immutable
@JsonDeserialize
@Data
@Builder
@Wither
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public final class PostContent {

    @NonNull
    private final String title;
    @NonNull
    private final String body;
    @NonNull
    private final String author;

}
