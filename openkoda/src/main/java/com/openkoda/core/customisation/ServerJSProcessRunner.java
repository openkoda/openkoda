/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.customisation;

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.flow.mbean.LoggingEntriesStack;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.service.Services;
import org.apache.commons.io.output.NullWriter;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A wrapper of a started ServerJS thread.
 * An instance of this class is passed to a context of ServerJS processing and exposes a few useful methods
 * in order to enhance functionality, eg. logging, data manipulation, starting command line processes.
 *
 */
public class ServerJSProcessRunner implements LoggingComponent {

    /**
     * Should be true if the application runs on Windows
     */
    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

    /**
     * Writer for logs during ServerJs execution
     */
    public final Writer logWriter;

    /**
     * Services exposed to ServerJs Thread
     */
    private Services services;

    /**
     * Sinking logs stack for storing the ServerJS log entries
     * See {@link LoggingEntriesStack}
     */
    private LoggingEntriesStack<String> logStack = new LoggingEntriesStack<>(50);

    /**
     * All running ServerJs threads.
     */
    //TODO: make private or package
    public static final Map<Thread, LoggingEntriesStack<String>> serverJsThreads = new LinkedHashMap<>();


    /**
     * Constructor. The ServerJS can be enhanced with additional services, also can write log messages to a provided writer.
     * @param services Services that should be exposed in running ServerJS context
     * @param logWriter log writer
     */
    public ServerJSProcessRunner(Services services, Writer logWriter) {
        this.logWriter = logWriter == null ? new NullWriter() : logWriter;
        this.services = services;
        serverJsThreads.put(Thread.currentThread(), logStack);
        if (serverJsThreads.size() > 30) {
            Optional<Thread> t = serverJsThreads.keySet().stream().filter(a -> a.getState() == Thread.State.TERMINATED).findFirst();
            if (t.isPresent()) {
                serverJsThreads.remove(t.get());
            }
        }
    }

    /**
     * Helper method to convert String to Long, that can be used in ServerJS script.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     */
    public Long getLong(String val) {
        return Long.valueOf(val);
    }

    /**
     * Helper method to convert String to BigDecimal, that can be used in ServerJS script.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     */
    public BigDecimal getBigDecimal(String val) {
        return new BigDecimal(val);
    }


    /**
     * Helper method to emit given event with given object.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     * @param eventName name of the event, it must be a registered event enum,
     * see {@link com.openkoda.core.service.event.AbstractApplicationEvent#getEvent(String)}
     * @param object object to be sent together with the event
     */
    public boolean emitEventAsync(String eventName, Object object) {
        return services.applicationEvent.emitEventAsync(ApplicationEvent.getEvent(eventName), object);
    }

    /**
     * Helper method to convert String to BigDecimal, that can be used in ServerJS script.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     */
    public boolean sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            error("[sleep]", e);
            return false;
        }
        return true;
    }

    /**
     * Helper method to save a new file on the filesystem.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     */
    public boolean createFileWithContent(String filePath, String content) {
        try {
            File file = new File(filePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            error(e, "[createFileWithContent] could not create file {}", filePath);
            return false;
        }
    }


    /**
     * Helper method to log running ServerJS action
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     */
    public boolean log(Object logEntry) throws IOException, InterruptedException {
        log(logEntry + "");
        return true;
    }

    /**
     * Helper method to log running ServerJS action
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     */
    public boolean log(String logEntry) throws IOException, InterruptedException {
        logWriter.write(logEntry);
        logWriter.write("\n");
        logWriter.flush();
        logStack.log(LocalDateTime.now().toString(), logEntry);
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        return true;
    }

    /**
     * Helper method to invoke a command line process and run a callback function on the command's
     * output per each line of the output.
     * An example use case would be to 'cat filename.csv' and run a function to process the file line by line.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     * @param command see {@link #startProcess}
     */
    public boolean runCommandCallbackPerLine(String command, Function<String, Object> f) throws InterruptedException {

        try {
            Process process = startProcess(command);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    f.apply(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Helper method to invoke a command line process and run a callback function on the command's whole output.
     * An example use case would be to 'cat somedocument.xml' and run a function to process the xml file.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     * @param command see {@link #startProcess}
     */
    public boolean runCommandCallbackWhole(String command, Function<String, Object> f) throws InterruptedException {

        try {
            Process process = startProcess(command);
            CharArrayWriter result = new CharArrayWriter();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    result.append(line);
                }
            }
            f.apply(result.toString());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Helper method to invoke a command line process and wait until the process writes an output
     * that contains expectedOutput.
     * An example use case would be to 'tail /var/log/auth.log' and run a function that waits for particular user login.
     * It may seem unused, but can be invoked by the dynamic script, so do not delete!
     * @return PID of the process when the expectedOutput was found, -1 if the process finished and the expectedOutput
     * was not found, null on IOException
     * @param command see {@link #startProcess}
     */
    public Long runCommandWaitForOutputWithTimeout(String command, String expectedOutput, int timeout) throws InterruptedException {
        try {
            Process process = startProcess(command);
            LocalDateTime timeoutEnd = LocalDateTime.now().plusSeconds(timeout);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && timeoutEnd.isAfter(LocalDateTime.now())) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    if(line.toLowerCase().contains(expectedOutput.toLowerCase())) {
                        return process.pid();
                    }
                }
                return -1L;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Internal method to invoke a command line process.
     * The command should be a linux prompt.
     * On Linux, the command is attempted to be executed under bash.
     * On Windows, the command is attempted to be executed under bash in WSL.
     * @return started {@link Process} object
     */
    private Process startProcess(String commandString) throws IOException {
        Process process;
        String [] command = isWindows ?
                new String[] {"c:/Windows/System32/wsl.exe", "bash", "-c", commandString} :
                new String[] {"bash", "-c", commandString} ;
        ProcessBuilder p = new ProcessBuilder(command);
        process = p.start();
        return process;
    }

    /**
     * Method to interrupt one of the running ServerJs threads by the thread id.
     */
    public static boolean interruptThread(long threadId) {
        Optional<Thread> t = serverJsThreads.keySet().stream().filter(a -> a.getId() == threadId).findFirst();
        if (t.isPresent()) {
            t.get().interrupt();
        }
        return true;
    }

    /**
     * Removes one of the running ServerJS threads by the thread id but only if it was terminated.
     */
    public static boolean removeJsThread(long threadId) {
        return serverJsThreads.keySet().removeIf( a -> a.getState().equals(Thread.State.TERMINATED) && a.getId() == threadId);
    }

}
