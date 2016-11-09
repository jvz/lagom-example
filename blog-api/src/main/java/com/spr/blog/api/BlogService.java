package com.spr.blog.api;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import akka.Done;
import akka.NotUsed;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * Exposes the blog microservice API. This API is adapted from the Lagom documentation.
 *
 * @author Matt Sicker
 */
public interface BlogService extends Service {

    /**
     * Gets a blog post for the given ID. Example:
     * curl http://localhost:9000/api/blog/12345678-1234-1234-1234-1234567890ab
     *
     * @param id id of the blog post to get
     */
    ServiceCall<NotUsed, Optional<PostContent>> getPost(String id);

    /**
     * Creates a new blog post and returns the ID of the newly created post. Example:
     * curl -H 'content-type: application/json' -X POST
     * -d '{"title": "Some Title", "body": "Some body", "author": "Some Guy"}'
     * http://localhost:9000/api/blog/
     */
    ServiceCall<PostContent, String> addPost();

    /**
     * Submits a blog post for the given ID. Example:
     * curl -H 'content-type: application/json' -X PUT
     * -d '{"title": "Some Title", "body": "Some body", "author": "Some Guy"}'
     * http://localhost:9000/api/blog/12345678-1234-1234-1234-1234567890ab
     *
     * @param id id of blog post to updatePost
     */
    ServiceCall<PostContent, Done> updatePost(String id);

    @Override
    default Descriptor descriptor() {
        return named("blog").withCalls(
                restCall(Method.GET, "/api/blog/:id", this::getPost),
                restCall(Method.POST, "/api/blog/", this::addPost),
                restCall(Method.PUT, "/api/blog/:id", this::updatePost)
        ).withAutoAcl(true);
    }
}
