package com.ratelut.apiserver.listeners;

import com.ratelut.apiserver.updater.UpdateRatesJob;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Initializes job scheduler to execute periodic jobs.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class BackgroundJobManager implements ServletContextListener {
    @Inject UpdateRatesJob updateRatesJob;
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Initializing periodic jobs scheduler.");

        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule jobs.
        scheduler.scheduleAtFixedRate(updateRatesJob, 0,
                Duration.ofMinutes(1).toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
    }
}
