package com.spr.blog.impl;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.spr.blog.api.BlogService;
import com.spr.blog.api.PostContent;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import akka.Done;
import akka.NotUsed;

/**
 * Service implementation for the blog microservice. This service is essentially a wrapper for the
 * persistence entity API.
 *
 * @author Matt Sicker
 */
public class BlogServiceImpl implements BlogService {

    private final PersistentEntityRegistry registry;

    @Inject
    public BlogServiceImpl(final PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(BlogEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, Optional<PostContent>> getPost(final String id) {
        return request -> registry.refFor(BlogEntity.class, id)
                .ask(BlogCommand.GetPost.INSTANCE);
    }

    @Override
    public ServiceCall<PostContent, String> addPost() {
        return content -> registry.refFor(BlogEntity.class, UUID.randomUUID().toString())
                .ask(new BlogCommand.AddPost(content));
    }

    @Override
    public ServiceCall<PostContent, Done> updatePost(final String id) {
        return content -> registry.refFor(BlogEntity.class, id)
                .ask(new BlogCommand.UpdatePost(content));
    }
}
