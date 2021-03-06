// Copyright © 2011-2013, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.core.util;

import fi.jumi.actors.eventizers.EventToString;

import java.lang.reflect.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class SpyListener<T> implements InvocationHandler {

    static final String ERROR_MARKER = "     ^^^^^^^^^^^^^^";

    private final Class<T> listenerType;
    private final List<Call> expectations = new ArrayList<>();
    private final List<Call> actualCalls = new ArrayList<>();
    private List<Call> current = expectations;

    public SpyListener(Class<T> listenerType) {
        this.listenerType = listenerType;
    }

    public T getListener() {
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{listenerType}, this);
        return listenerType.cast(proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args == null) {
            args = new Object[0];
        }
        current.add(new Call(method.getName(), args));
        return null;
    }

    public void replay() {
        if (current != expectations) {
            throw new IllegalStateException("replay() has already been called");
        }
        current = actualCalls;
    }

    public void verify() {
        if (current != actualCalls) {
            throw new IllegalStateException("replay() was not called");
        }
        StringBuilder message = new StringBuilder();
        message.append("not all expectations were met\n");

        message.append("Expected:\n");
        for (int i = 0; i < expectations.size(); i++) {
            message.append(listItem(i, expectations));
            if (!matchesAt(i)) {
                message.append(ERROR_MARKER + "\n");
            }
        }

        message.append("but was:\n");
        for (int i = 0; i < actualCalls.size(); i++) {
            message.append(listItem(i, actualCalls));
            if (!matchesAt(i)) {
                message.append(ERROR_MARKER + "\n");
            }
        }

        assertThat(message.toString(), actualCalls.equals(expectations));
    }

    private boolean matchesAt(int i) {
        return i < actualCalls.size() &&
                i < expectations.size() &&
                expectations.get(i).equals(actualCalls.get(i));
    }

    private static String listItem(int i, List<Call> list) {
        return "  " + (i + 1) + ". " + list.get(i) + "\n";
    }


    private static class Call {
        private final String methodName;
        private final Object[] args;

        public Call(String methodName, Object... args) {
            this.methodName = methodName;
            this.args = args;
        }

        @Override
        public String toString() {
            return EventToString.format("", methodName, args).substring(1);
        }

        @Override
        public boolean equals(Object obj) {
            Call that = (Call) obj;
            return this.methodName.equals(that.methodName) && argsMatch(that);
        }

        private boolean argsMatch(Call that) {
            if (this.args.length != that.args.length) {
                return false;
            }
            for (int i = 0; i < this.args.length; i++) {
                Object arg1 = this.args[i];
                Object arg2 = that.args[i];
                if (arg1 instanceof Throwable) {
                    if (!sameTypeAndMessage((Throwable) arg1, (Throwable) arg2)) {
                        return false;
                    }
                } else if (!Objects.equals(arg1, arg2)) {
                    return false;
                }
            }
            return true;
        }

        private boolean sameTypeAndMessage(Throwable t1, Throwable t2) {
            return Objects.equals(t1.getClass(), t2.getClass()) &&
                    Objects.equals(t1.getMessage(), t2.getMessage());
        }
    }
}
