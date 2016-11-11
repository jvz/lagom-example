package com.spr.blog.impl;

import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import com.spr.blog.api.PostContent;
import com.spr.blog.impl.BlogCommand.AddPost;
import com.spr.blog.impl.BlogCommand.GetPost;
import com.spr.blog.impl.BlogCommand.UpdatePost;
import com.spr.blog.impl.BlogEvent.PostAdded;
import com.spr.blog.impl.BlogEvent.PostUpdated;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.List;
import java.util.Optional;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Rule
    public TestName testName = new TestName();

    private PersistentEntityTestDriver<BlogCommand, BlogEvent, BlogState> driver;

    @Before
    public void setUp() throws Exception {
        // given a default BlogEntity
        driver = new PersistentEntityTestDriver<>(system, new BlogEntity(), testName.getMethodName());
    }

    @Test
    public void initialStateShouldBeEmpty() throws Exception {
        // when we send a GetPost command
        final Outcome<BlogEvent, BlogState> getPostOutcome = driver.run(GetPost.INSTANCE);

        // then no events should have been created
        assertThat(getPostOutcome.events()).isEmpty();

        // and the state should still be empty
        assertThat(getPostOutcome.state().getContent()).isNotPresent();

        // and we should get back an empty Optional to indicate that no post was found
        final Optional<PostContent> actual = getFirstReply(getPostOutcome);
        assertThat(actual).isNotPresent();
    }

    @Test
    public void addPost() throws Exception {
        // given entity ID of test name
        final String expectedEntityId = testName.getMethodName();

        // when we send an AddPost command
        final Outcome<BlogEvent, BlogState> addPostOutcome = driver.run(new AddPost(newPostContent()));

        // then a PostAdded event should be persisted
        final List<BlogEvent> events = addPostOutcome.events();
        assertThat(events).containsExactly(new PostAdded(expectedEntityId, newPostContent()));

        // and the state should contain that post content
        assertThat(addPostOutcome.state().getContent()).hasValue(newPostContent());

        // and the reply should give us the entity ID
        final String entityId = getFirstReply(addPostOutcome);
        assertThat(entityId).isEqualTo(expectedEntityId);

        // when we send a subsequent GetPost command
        final Outcome<BlogEvent, BlogState> getPostOutcome = driver.run(GetPost.INSTANCE);

        // then the reply should be our post content we added earlier
        final Optional<PostContent> content = getFirstReply(getPostOutcome);
        assertThat(content).hasValue(newPostContent());
    }

    @Test
    public void updatePost() throws Exception {
        // given entity ID of test name
        final String expectedEntityId = testName.getMethodName();

        // when we send an UpdatePost command
        final Outcome<BlogEvent, BlogState> updatePostOutcome = driver.run(new UpdatePost(newPostContent()));

        // then a PostUpdated event should be persisted
        final List<BlogEvent> events = updatePostOutcome.events();
        assertThat(events).containsExactly(new PostUpdated(expectedEntityId, newPostContent()));

        // and the state should contain the post content
        assertThat(updatePostOutcome.state().getContent()).hasValue(newPostContent());

        // and the reply should be Done
        final Done reply = getFirstReply(updatePostOutcome);
        assertThat(reply).isEqualTo(Done.getInstance());
    }

    @SuppressWarnings("unchecked")
    private static <T> T getFirstReply(final Outcome<?, ?> outcome) {
        return (T) outcome.getReplies().get(0);
    }

    private static PostContent newPostContent() {
        return new PostContent("z", "y", "x");
    }
}