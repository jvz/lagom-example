package com.spr.blog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.spr.blog.api.PostContent;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Holds the state of a blog post entity.
 *
 * @author Matt Sicker
 * @see BlogEntity
 */
@Immutable
@JsonDeserialize
@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class BlogState implements CompressedJsonable {

    /**
     * Default initial blog post state.
     */
    public static final BlogState EMPTY = new BlogState(Optional.empty());

    Optional<PostContent> content;
}
