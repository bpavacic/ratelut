package com.ratelut.apiserver.updater;

/**
 * Checks if any of exchange rates need to be updated.
 *
 * Currently contains dummy implementation.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class UpdateRatesJob implements Runnable {
    // TODO(bobo): Remove
    volatile public static int counter = 0;

    @Override
    public void run() {
        counter++;
        System.out.println("Counter increased to " + counter);
    }
}
