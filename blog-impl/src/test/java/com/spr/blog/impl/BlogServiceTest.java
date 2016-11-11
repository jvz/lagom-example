package com.spr.blog.impl;

import com.spr.blog.api.BlogService;
import com.spr.blog.api.PostContent;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the blog microservice.
 *
 * @author Matt Sicker
 */
public class BlogServiceTest {
    @Test
    public void testBlogServices() throws Exception {
        withServer(defaultSetup().withCassandra(true), server -> {
            final BlogService service = server.client(BlogService.class);

            unknownBlogIdShouldBeEmpty(service);

            final String id = addPostAndGetId(service);
            verifyPostIdHasContent(service, id, newPostContent());

            updatePostShouldUpdateContent(service, id, newPostContent().withBody("Test body"));
        });
    }

    private static void unknownBlogIdShouldBeEmpty(final BlogService service) throws Exception {
        // when we look up an arbitrary unknown blog post
        final Optional<PostContent> response =
                getOrTimeout(service.getPost("unknown entity id").invoke());

        // then the response should be empty
        assertThat(response).isNotPresent();
    }

    private static String addPostAndGetId(final BlogService service) throws Exception {
        // when we add a post
        final String id = getOrTimeout(service.addPost().invoke(newPostContent()));

        // then the id should be a UUID
        assertThat(id).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

        return id;
    }

    private static void updatePostShouldUpdateContent(final BlogService service, final String id,
                                                      final PostContent content) throws Exception {
        // when we update a post
        getOrTimeout(service.updatePost(id).invoke(content));

        // then the contents should be updated
        verifyPostIdHasContent(service, id, content);
    }

    private static void verifyPostIdHasContent(final BlogService service, final String id,
                                               final PostContent content) throws Exception {
        // when we look up the post ID
        final Optional<PostContent> response = getOrTimeout(service.getPost(id).invoke());

        // then the post content should match
        assertThat(response).hasValue(content);
    }

    private static PostContent newPostContent() {
        return new PostContent("Hello, world!", "This is a test blog entry", "msicker");
    }

    private static <T> T getOrTimeout(final CompletionStage<T> stage)
            throws InterruptedException, ExecutionException, TimeoutException {
        return stage.toCompletableFuture().get(5, TimeUnit.SECONDS);
    }
}