package com.spr.blog.impl;

import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import com.spr.blog.api.PostContent;
import com.spr.blog.impl.BlogCommand.AddPost;
import com.spr.blog.impl.BlogCommand.GetPost;
import com.spr.blog.impl.BlogCommand.UpdatePost;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;

import static org.junit.Assert.*;

/**
 * Blog entity tests.
 *
 * @author Matt Sicker
 */
public class BlogEntityTest {

    private static ActorSystem system;

    @BeforeClass
    public static void beforeClass() {
        system = ActorSystem.create("BlogEntityTest");
    }

    @AfterClass
    public static void afterClass() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testBlogPost() throws Exception {
        final PersistentEntityTestDriver<BlogCommand, BlogEvent, BlogState> driver =
                new PersistentEntityTestDriver<>(system, new BlogEntity(), "test-1");

        final Outcome<BlogEvent, BlogState> initialBlogPost = driver.run(GetPost.INSTANCE);
        assertFalse(((Optional<?>) initialBlogPost.getReplies().get(0)).isPresent());

        final Outcome<BlogEvent, BlogState> updatedBlogPost = driver.run(new UpdatePost(new PostContent("A", "B", "C")));
        assertEquals(Done.getInstance(), updatedBlogPost.getReplies().get(0));

        final Outcome<BlogEvent, BlogState> getUpdatedBlogPost = driver.run(GetPost.INSTANCE);
        assertEquals(new PostContent("A", "B", "C"), getContent(getUpdatedBlogPost));

        final Outcome<BlogEvent, BlogState> addBlogPost = driver.run(new AddPost(new PostContent("z", "y", "x")));
        final String id = (String) addBlogPost.getReplies().get(0);
        assertEquals("test-1", id); // our driver only holds one entity, so this doesn't create a new entity id

        final Outcome<BlogEvent, BlogState> addedBlogPost = driver.run(GetPost.INSTANCE);
        assertEquals(new PostContent("z", "y", "x"), getContent(addedBlogPost));
    }

    @SuppressWarnings("unchecked")
    private static PostContent getContent(final Outcome<BlogEvent, BlogState> outcome) {
        return ((Optional<PostContent>) outcome.getReplies().get(0)).orElseThrow(AssertionError::new);
    }
}