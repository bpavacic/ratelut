package com.ratelut.apiserver.listeners;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Initializes Guice injector and bindings.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class ServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        // TODO(bobo): Add modules here.
        return Guice.createInjector();
    }
}
