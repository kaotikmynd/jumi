// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.test;

import org.junit.rules.Timeout;

public class Timeouts {

    public static final int END_TO_END_TEST = 5000;
    public static final int ASSERTION = 1000;

    public static Timeout forEndToEndTest() {
        return new Timeout(END_TO_END_TEST);
    }
}
