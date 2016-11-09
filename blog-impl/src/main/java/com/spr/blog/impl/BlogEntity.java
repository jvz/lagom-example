package com.spr.blog.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

import akka.Done;

/**
 * Entity and behaviors for blog posts.
 *
 * @author Matt Sicker
 */
@SuppressWarnings("unchecked")
public class BlogEntity extends PersistentEntity<BlogCommand, BlogEvent, BlogState> {

    @Override
    public Behavior initialBehavior(final Optional<BlogState> snapshotState) {
        final BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(BlogState.EMPTY));
        addBehaviorForGetPost(b);
        addBehaviorForAddPost(b);
        addBehaviorForUpdatePost(b);
        return b.build();
    }

    private void addBehaviorForGetPost(final BehaviorBuilder b) {
        b.setReadOnlyCommandHandler(BlogCommand.GetPost.class,
                (cmd, ctx) -> ctx.reply(state().getContent()));
    }

    private void addBehaviorForAddPost(final BehaviorBuilder b) {
        b.setCommandHandler(BlogCommand.AddPost.class,
                (cmd, ctx) -> ctx.thenPersist(
                        new BlogEvent.PostAdded(entityId(), cmd.getContent()),
                        evt -> ctx.reply(entityId())
                )
        );
        b.setEventHandler(BlogEvent.PostAdded.class, evt -> new BlogState(Optional.of(evt.getContent())));
    }

    private void addBehaviorForUpdatePost(final BehaviorBuilder b) {
        b.setCommandHandler(BlogCommand.UpdatePost.class,
                (cmd, ctx) -> ctx.thenPersist(
                        new BlogEvent.PostUpdated(entityId(), cmd.getContent()),
                        evt -> ctx.reply(Done.getInstance())
                )
        );
        b.setEventHandler(BlogEvent.PostUpdated.class, evt -> new BlogState(Optional.of(evt.getContent())));
    }
}
