package com.spr.blog.impl;

import com.spr.blog.api.BlogService;
import com.spr.blog.api.PostContent;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

            assertFalse(service.getPost("arbitrary nonexistent id").invoke().toCompletableFuture().get(5, TimeUnit.SECONDS).isPresent());

            final String id = service.addPost().invoke(PostContent.builder().title("Test").author("Guy").body("Hello, world!").build()).toCompletableFuture().get(5, TimeUnit.SECONDS);
            assertNotNull(id);

            final PostContent content = getBlogPost(service, id);
            assertEquals(PostContent.builder().title("Test").author("Guy").body("Hello, world!").build(), content);

            final PostContent updated = content.withBody("<h1>Hello, world!</h1>");
            service.updatePost(id).invoke(updated).toCompletableFuture().get(5, TimeUnit.SECONDS);

            final PostContent actual = getBlogPost(service, id);
            assertEquals(PostContent.builder().title("Test").author("Guy").body("<h1>Hello, world!</h1>").build(), actual);
        });
    }

    private static PostContent getBlogPost(final BlogService service, final String id) throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        return service.getPost(id).invoke().toCompletableFuture().get(5, TimeUnit.SECONDS).orElseThrow(AssertionError::new);
    }
}