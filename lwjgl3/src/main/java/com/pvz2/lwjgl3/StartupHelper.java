/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Note, the above license and copyright applies to this file only.
package com.pvz2.lwjgl3;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;

import org.lwjgl.system.JNI;
import org.lwjgl.system.linux.UNISTD;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StartupHelper {

	private StartupHelper() {}

	private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

	public static boolean isLinuxNvidia() {
		String[] drivers = new File("/proc/driver").list(
			(dir, path) -> path.toUpperCase(Locale.ROOT).contains("NVIDIA")
		);
		if (drivers == null) return false;
		return drivers.length > 0;
	}

	public static boolean startNewJvmIfRequired() {
		return startNewJvmIfRequired(true);
	}

	public static boolean startNewJvmIfRequired(boolean inheritIO) {
		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (osName.contains("mac")) return startNewJvm0(/*isMac =*/ true, inheritIO);
		if (osName.contains("windows")) {

			String programData = System.getenv("ProgramData");
			if (programData == null) programData = "C:\\Temp";
			String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
			String prevUser = System.getProperty("user.name", "libGDX_User");
			System.setProperty("java.io.tmpdir", programData + "\\libGDX-temp");
			System.setProperty(
				"user.name",
				("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_')
			);
			Lwjgl3NativesLoader.load();
			System.setProperty("java.io.tmpdir", prevTmpDir);
			System.setProperty("user.name", prevUser);
			return false;
		}
		return startNewJvm0(/*isMac =*/ false, inheritIO);
	}

	private static final String MAC_JRE_ERR_MSG = "A Java installation could not be found. " +
        "If you are distributing this " +
        "app with a bundled JRE, be sure to set the '-XstartOnFirstThread' argument manually!";
	private static final String LINUX_JRE_ERR_MSG = "A Java installation could not be found. " +
        "If you are distributing this " +
        "app with a bundled JRE, be sure to set the environment variable '__GL_THREADED_OPTIMIZATIONS' to '0'!";
	private static final String CHILD_LOOP_ERR_MSG = "The current JVM process is a spawned child JVM process, but" +
        " StartupHelper has attempted to spawn another child JVM process! This is a broken state, and should not" +
        " normally happen! Your game may crash or not function properly!";

	public static boolean startNewJvm0(boolean isMac, boolean inheritIO) {
		long processID = getProcessID(isMac);
		if (!isMac) {
			if (!isLinuxNvidia()) return false;
			if ("0".equals(System.getenv("__GL_THREADED_OPTIMIZATIONS"))) return false;
		} else {
			if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) return false;
			long objcMsgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
			long nsThread = ObjCRuntime.objc_getClass("NSThread");
			long currentThread = JNI.invokePPP(nsThread,
                ObjCRuntime.sel_getUid("currentThread"), objcMsgSend);
			boolean isMainThread = JNI.invokePPZ(currentThread,
                ObjCRuntime.sel_getUid("isMainThread"), objcMsgSend);
			if (isMainThread) return false;
			if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + processID))) return false;}
		if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
			System.err.println(CHILD_LOOP_ERR_MSG);
			return false;}
		List<String> jvmArgs = new ArrayList<>();
		String javaExecPath = System.getProperty("java.home") + "/bin/java";
		if (!(new File(javaExecPath).exists())) {
			System.err.println(getJreErrMsg(isMac));
			return false;}
		jvmArgs.add(javaExecPath);
		if (isMac) jvmArgs.add("-XstartOnFirstThread");
		jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
		jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
		jvmArgs.add("-cp");
		jvmArgs.add(System.getProperty("java.class.path"));
		String mainClass = System.getenv("JAVA_MAIN_CLASS_" + processID);
		if (mainClass == null) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			if (trace.length > 0) mainClass = trace[trace.length - 1].getClassName();
			else {
				System.err.println("The main class could not be determined.");
				return false;
			}
		}
		jvmArgs.add(mainClass);
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
			if (!isMac) processBuilder.environment().put("__GL_THREADED_OPTIMIZATIONS", "0");
			if (!inheritIO) processBuilder.start();
			else processBuilder.inheritIO().start().waitFor();
		} catch (Exception e) {
			System.err.println("There was a problem restarting the JVM.");
			e.printStackTrace();
		}
		return true;
	}

	private static String getJreErrMsg(boolean isMac) {
		if (isMac) return MAC_JRE_ERR_MSG;
		else return LINUX_JRE_ERR_MSG;
	}

	private static long getProcessID(boolean isMac) {
		if (isMac) return LibC.getpid();
		else return UNISTD.getpid();
	}
}
