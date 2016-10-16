package com.ratelut.apiserver.listeners;

import com.google.common.collect.ImmutableList;
import com.ratelut.apiserver.updater.UpdateRatesJob;
import org.joda.time.Duration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initializes scheduler to execute periodic jobs.
 *
 * The listener is manually declared in web.xml.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class BackgroundJobManager implements ServletContextListener {
    private static List<JobDefinition> JOBS = ImmutableList.of(
            new JobDefinition(Duration.standardMinutes(1), new UpdateRatesJob()));

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Initializing periodic jobs scheduler.");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Schedule all jobs.
        for (JobDefinition job : JOBS) {
            scheduler.scheduleAtFixedRate(job.job, 0, job.duration.getMillis(),
                    TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Deinitializing periodic jobs scheduler");
        scheduler.shutdownNow();
    }

    private static class JobDefinition {
        final Duration duration;
        final Runnable job;

        public JobDefinition(Duration duration, Runnable job) {
            this.duration = duration;
            this.job = job;
        }
    }
}