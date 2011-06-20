// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package net.orfjackal.jumi.core;

import java.util.concurrent.*;

public class SuiteStateCollector implements SuiteListener {
    private volatile SuiteResults state = new SuiteResults();
    private final CountDownLatch finished = new CountDownLatch(1);

    public SuiteResults getState() {
        return state;
    }

    public void onSuiteStarted() {
    }

    public void onSuiteFinished() {
        state = state.withFinished(true);
        finished.countDown();
    }

    public void awaitSuiteFinished() throws InterruptedException {
        finished.await();
    }

    public boolean awaitSuiteFinished(long timeout, TimeUnit unit) throws InterruptedException {
        return finished.await(timeout, unit);
    }
}