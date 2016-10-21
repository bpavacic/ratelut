package com.ratelut.apiserver.listeners;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ratelut.apiserver.storage.Storage;
import com.ratelut.apiserver.storage.StorageModule;
import com.ratelut.apiserver.updater.UpdateRatesJob;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Initializes scheduler to execute periodic jobs.
 *
 * The listener is manually declared in web.xml.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class BackgroundJobManager implements ServletContextListener {
    private Storage storage;
    private ScheduledExecutorService scheduler;

    public BackgroundJobManager() {
        // TODO(bobo): Check if there is a way to initialize the module elsewhere.
        Injector injector = Guice.createInjector(new StorageModule());
        this.storage = Preconditions.checkNotNull(injector.getInstance(Storage.class));
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Initializing periodic jobs scheduler.");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule jobs.
        scheduler.scheduleAtFixedRate(new UpdateRatesJob(storage), 0,
                Duration.ofMinutes(1).toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }
}
