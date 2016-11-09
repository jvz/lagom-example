package com.spr.blog.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import com.spr.blog.api.BlogService;
import com.spr.blog.api.PostContent;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import akka.Done;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Commands for manipulating blog post entities.
 *
 * @author Matt Sicker
 * @see BlogEntity
 */
public interface BlogCommand extends Jsonable {

    /**
     * @see BlogService#getPost(String)
     */
    enum GetPost implements BlogCommand, PersistentEntity.ReplyType<Optional<PostContent>> {
        INSTANCE
    }

    /**
     * @see BlogService#addPost()
     */
    @Immutable
    @JsonDeserialize
    @Data
    @AllArgsConstructor
    final class AddPost implements BlogCommand, CompressedJsonable, PersistentEntity.ReplyType<String> {
        @NonNull
        private final PostContent content;
    }

    /**
     * @see BlogService#updatePost(String)
     */
    @Immutable
    @JsonDeserialize
    @Data
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class UpdatePost implements BlogCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        @NonNull
        private final PostContent content;
    }
}
