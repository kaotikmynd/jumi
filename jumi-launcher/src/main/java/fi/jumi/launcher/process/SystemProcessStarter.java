// Copyright © 2011-2012, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.launcher.process;

import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.util.*;

@Immutable
public class SystemProcessStarter implements ProcessStarter {

    private final File javaExecutable = new File(System.getProperty("java.home"), "bin/java");

    @Override
    public Process startJavaProcess(File executableJar, File workingDir, List<String> jvmOptions, Properties systemProperties, String... args) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(workingDir);
        builder.redirectErrorStream(true);
        builder.command(buildCommand(executableJar, jvmOptions, systemProperties, args));
        return builder.start();
    }

    // package-private for testing
    List<String> buildCommand(File executableJar, List<String> jvmOptions, Properties systemProperties, String[] args) {
        List<String> command = new ArrayList<String>();
        command.add(javaExecutable.getAbsolutePath());
        command.addAll(jvmOptions);
        command.addAll(asJvmOptions(systemProperties));
        command.add("-jar");
        command.add(executableJar.getAbsolutePath());
        command.addAll(Arrays.asList(args));
        return command;
    }

    private static List<String> asJvmOptions(Properties systemProperties) {
        List<String> jvmOptions = new ArrayList<String>();
        for (Map.Entry<Object, Object> property : systemProperties.entrySet()) {
            String key = (String) property.getKey();
            String value = (String) property.getValue();
            jvmOptions.add("-D" + key + "=" + value);
        }
        return jvmOptions;
    }
}