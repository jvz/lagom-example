package com.spr.blog.impl;

import com.google.inject.AbstractModule;

import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.spr.blog.api.BlogService;

/**
 * Google Guice module.
 *
 * @author Matt Sicker
 */
public class BlogModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(BlogService.class, BlogServiceImpl.class));
    }
}
