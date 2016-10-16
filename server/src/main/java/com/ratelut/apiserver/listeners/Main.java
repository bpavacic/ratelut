package com.ratelut.apiserver.listeners;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.ratelut.apiserver.storage.StorageModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Guice dependency injection module.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class Main extends GuiceServletContextListener {
    private static final String API_BASE = "/api/*";
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new StorageModule(),
                new ServletModule() {
                    @Override
                    protected void configureServlets() {
                        ResourceConfig rc = new PackagesResourceConfig(
                                com.ratelut.apiserver.services.ApiService.class
                                        .getPackage().getName());
                        for (Class<?> resource : rc.getClasses()) {
                            bind(resource);
                        }

                        serve(API_BASE).with(GuiceContainer.class);
                    }
                }
        );
    }
}
