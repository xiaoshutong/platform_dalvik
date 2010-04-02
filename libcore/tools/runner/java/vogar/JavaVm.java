/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vogar;

import java.io.File;
import java.util.List;

/**
 * A local Java virtual machine like Harmony or the RI.
 */
final class JavaVm extends Vm {

    private final File javaHome;

    JavaVm(Integer debugPort, File sdkJar, List<String> javacArgs, int monitorPort,
            File localTemp, File javaHome, List<String> additionalVmArgs,
            boolean cleanBefore, boolean cleanAfter) {
        super(new EnvironmentHost(cleanBefore, cleanAfter, debugPort, localTemp),
                sdkJar, javacArgs, additionalVmArgs, monitorPort);
        this.javaHome = javaHome;
    }

    @Override protected void postCompileRunner() {
    }

    @Override protected void postCompile(Action action) {
    }

    @Override protected VmCommandBuilder newVmCommandBuilder(
            File workingDirectory) {
        String java = javaHome == null ? "java" : new File(javaHome, "bin/java").getPath();
        return new VmCommandBuilder()
                .vmCommand(java)
                .workingDir(workingDirectory);
    }
    @Override protected Classpath getRuntimeSupportClasspath(Action action) {
        Classpath classpath = new Classpath();
        classpath.addAll(environment.classesDir(action));
        classpath.addAll(this.classpath);
        classpath.addAll(environment.runnerClassesDir());
        classpath.addAll(runnerClasspath);
        /*
         * For javax.net.ssl tests dependency on Bouncy Castle for
         * creating a self-signed X509 certificate. Needs to be run
         * with an openjdk, not a sunjdk, which expects a signed jar
         * to authenticate security providers. For example:
         *
         * --java-home /usr/lib/jvm/java-6-openjdk
         */
        classpath.addAll(new File("/usr/share/java/bcprov.jar"));
        return classpath;
    }
}
