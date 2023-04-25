/*
MIT License

Copyright (c) 2014-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

package com.openkoda.core.flow;

import com.openkoda.core.flow.mbean.LoggingEntriesStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.Assert;
import org.springframework.util.ResizableByteArrayOutputStream;

import java.io.PrintStream;
import java.util.*;

/**
 * Interface that makes logging easier. In order to use it, just let a class
 * implement it and all functions are available and working out-of-box.
 * 
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public interface LoggingComponent {

   static final Map<Class, Logger> loggers = new HashMap<Class, Logger>();
   static final List<Class> availableLoggers = new ArrayList<>();
   static final Set<Class> debugLoggers = new HashSet<>();
   static final Logger debugLogger = LoggerFactory.getLogger( "jmxDebug" );

   static final LoggingEntriesStack<Date> debugStack = new LoggingEntriesStack<>(500);

   /**
    * @return logger for class implementing the interface. Can be overridden in
    *         order to provide precreated logger.
    * 
    */
   default Logger getLogger(boolean createIfNotExists) {
      Logger l = loggers.get( getClass() );
      if ( l == null && createIfNotExists ) {
         l = LoggerFactory.getLogger( getClass() );
         loggers.put( getClass() , l );
         availableLoggers.add( getClass() );
      }
      return l;
   }

   /**
    * @return logger for class implementing the interface. Can be overridden in
    *         order to provide precreated logger.
    * 
    */
   default Logger getLogger() {
      return getLogger( true );
   }

   /**
    * Logger method for debug.
    * 
    * @param format Format string, use {} for placeholders. Eg.: "User id: {}"
    * @param arguments to fill placeholders
    */
   default void debug(String format, Object... arguments) {
      Logger l = getLogger();
      l.debug( format , arguments );
      if ( isDebugLogger() ) {
         logToDebugStack(null, format , arguments );
      }
   }

   default String formatMessage(String format, Object... arguments) {
      FormattingTuple ft = MessageFormatter.arrayFormat( format , arguments );
      return ft.getMessage();
   }

   default void logToDebugStack(Throwable t, String message, Object... arguments) {
      PrintStream ps = null;
      message = formatMessage(message, arguments);
      if ( t != null ) {
         try {
            ResizableByteArrayOutputStream buffer = new ResizableByteArrayOutputStream(8 * 128);
            ps = new PrintStream(buffer);
            ps.append(message);
            ps.append("\n");
            t.printStackTrace(ps);
            message = buffer.toString();
         } finally {
            if (ps != null) {
               ps.close();
            }
         }
      }
      debugLogger.debug( message );
      debugStack.log(new Date(), getClass().getSimpleName() + " - " + message );
   }

   default void trace(String format, Object... arguments) {
      Logger l = getLogger();
      l.trace( format , arguments );
      if ( isDebugLogger() ) {
         logToDebugStack( null, format , arguments );
      }
   }

   default boolean isDebugLogger() {
      return debugLoggers.contains( getClass() );
   }

   /**
    * Logger method for info.
    * 
    * @param format Format string, use {} for placeholders. Eg.: "User id: {}"
    * @param arguments to fill placeholders
    */
   default void info(String format, Object... arguments) {
      getLogger().info( format , arguments );
   }

   /**
    * Logger method for warn.
    * 
    * @param format Format string, use {} for placeholders. Eg.: "User id: {}"
    * @param arguments to fill placeholders
    */
   default void warn(String format, Object... arguments) {
      getLogger().warn( format , arguments );
      logToDebugStack( null, format , arguments );
   }

   /**
    * Logger method for warn with underlying exception.
    * 
    * @param message - message to put into log file.
    * @param throwable - exception to provide stack trace.
    */
   default void warn(String message, Throwable throwable) {
      getLogger().warn( message , throwable );
      logToDebugStack(throwable, message);
   }

   /**
    * Logger method for error.
    * 
    * @param format Format string, use {} for placeholders. Eg.: "User id: {}"
    * @param arguments to fill placeholders
    */
   default void error(String format, Object... arguments) {
      getLogger().error( format , arguments );
      logToDebugStack(null, format , arguments );
   }


   /**
    * Logger method for error.
    *
    * @param throwable - exception to provide stack trace.
    * @param format Format string, use {} for placeholders. Eg.: "User id: {}"
    * @param arguments to fill placeholders
    */
   default void error(Throwable throwable, String format, Object... arguments) {
      getLogger().error( format , arguments );
      getLogger().error("[error] cause:",  throwable );
      logToDebugStack(throwable, format , arguments );
   }

   /**
    * Logger method for error with underlying exception.
    * 
    * @param message - message to put into log file.
    * @param throwable - exception to provide stack trace.
    */
   default void error(String message, Throwable throwable) {
      getLogger().error( message , throwable );
      logToDebugStack( throwable, message );
   }

   default Map<Date, String> getDebugEntries() {
      return debugStack;
   }

   default void notNull(Object o) {
      Assert.notNull( o );
   }

   default void isTrue(Boolean b) {
      Assert.isTrue( b );
   }

   default List<Class> getAvailableLoggers() {
      return availableLoggers;
   }

   default Set<Class> getDebugLoggers() {
      return debugLoggers;
   }

}
