// Copyright © 2011-2013, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core;

import fi.jumi.api.drivers.TestId;
import fi.jumi.core.api.*;

@SuppressWarnings("CodeBlock2Expr")
public class SuiteMother {

    public static final TestFile TEST_FILE = TestFile.fromClassName("com.example.DummyTest");
    public static final String TEST_CLASS = TEST_FILE.getClassName();
    public static final String TEST_CLASS_NAME = "DummyTest";

    public static void emptySuite(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.end();
    }


    // passing and failing tests

    public static void onePassingTest(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME);
        suite.runFinished(run1);

        suite.end();
    }

    public static void oneFailingTest(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.failingTest(run1, TestId.ROOT, TEST_CLASS_NAME,
                new Throwable("dummy exception")
        );
        suite.runFinished(run1);

        suite.end();
    }

    public static void nestedFailingAndPassingTests(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME, () -> {
            suite.test(run1, TestId.of(0), "testOne");
            suite.failingTest(run1, TestId.of(1), "testTwo", new Throwable("dummy exception"));
        });
        suite.runFinished(run1);

        suite.end();
    }


    // multiple runs

    public static void twoPassingRuns(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME, () -> {
            suite.test(run1, TestId.of(0), "testOne");
        });
        suite.runFinished(run1);

        RunId run2 = suite.nextRunId();
        suite.runStarted(run2, TEST_FILE);
        suite.test(run2, TestId.ROOT, TEST_CLASS_NAME, () -> {
            suite.test(run2, TestId.of(1), "testTwo");
        });
        suite.runFinished(run2);

        suite.end();
    }

    public static void twoInterleavedRuns(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        RunId run2 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.runStarted(run2, TEST_FILE);
        listener.onTestFound(TEST_FILE, TestId.of(0), "testOne");
        listener.onTestFound(TEST_FILE, TestId.of(1), "testTwo");
        listener.onTestStarted(run1, TestId.of(0));
        listener.onTestStarted(run2, TestId.of(1));
        listener.onTestFinished(run1);
        listener.onTestFinished(run2);
        suite.runFinished(run1);
        suite.runFinished(run2);

        suite.end();
    }


    // standard output

    public static void printsToStdout(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME, () -> {
            suite.printOut(run1, "printed to stdout\n");
        });
        suite.runFinished(run1);

        suite.end();
    }

    public static void printsToStderr(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME, () -> {
            suite.printErr(run1, "printed to stderr\n");
        });
        suite.runFinished(run1);

        suite.end();
    }

    public static void printsToStdoutWithoutNewlineAtEnd(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME, () -> {
            suite.printOut(run1, "this doesn't end with newline");
        });
        suite.runFinished(run1);

        suite.end();
    }

    public static void printsAfterTestRunFinished(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.findAllTestFiles(TEST_FILE);

        RunId run1 = suite.nextRunId();
        suite.runStarted(run1, TEST_FILE);
        suite.test(run1, TestId.ROOT, TEST_CLASS_NAME, () -> {
        });
        suite.runFinished(run1);
        suite.printOut(run1, "printed to stdout "); // any warnings should happen only once, even though printed many times
        suite.printOut(run1, "after run\n");
        suite.printErr(run1, "and the same for stderr\n");

        suite.end();
    }


    // internal errors

    public static void internalError(SuiteListener listener) {
        EventBuilder suite = new EventBuilder(listener);
        suite.begin();
        suite.internalError("the internal error message", new Throwable("dummy exception"));
        suite.end();
    }
}
