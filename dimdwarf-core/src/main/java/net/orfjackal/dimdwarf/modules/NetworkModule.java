// Copyright © 2008-2010 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://dimdwarf.sourceforge.net/LICENSE

package net.orfjackal.dimdwarf.modules;

import com.google.inject.*;
import net.orfjackal.dimdwarf.controller.Controller;
import net.orfjackal.dimdwarf.mq.MessageReceiver;
import net.orfjackal.dimdwarf.net.*;
import net.orfjackal.dimdwarf.services.*;

public class NetworkModule extends ServiceModule {

    public NetworkModule() {
        super("Network");
    }

    protected void configure() {
        bindControllerTo(NetworkController.class);
        bindServiceTo(NetworkService.class);
        bindMessageQueueOfType(Object.class);
    }

    @Provides
    ControllerRegistration controllerRegistration(Provider<Controller> controller) {
        return new ControllerRegistration(serviceName, controller);
    }

    @Provides
    ServiceRegistration serviceRegistration(Provider<ServiceContext> context,
                                            Provider<ServiceRunnable> service) {
        return new ServiceRegistration(serviceName, context, service);
    }

    @Provides
    ServiceRunnable service(Service service, MessageReceiver<Object> toService) {
        return new ServiceMessageLoop(service, toService);
    }
}
