/*
 * Copyright (c) 2004-2005 SLF4J.ORG
 * Copyright (c) 2004-2005 QOS.ch
 *
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute, and/or sell copies of  the Software, and to permit persons
 * to whom  the Software is furnished  to do so, provided  that the above
 * copyright notice(s) and this permission notice appear in all copies of
 * the  Software and  that both  the above  copyright notice(s)  and this
 * permission notice appear in supporting documentation.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR  A PARTICULAR PURPOSE AND NONINFRINGEMENT
 * OF  THIRD PARTY  RIGHTS. IN  NO EVENT  SHALL THE  COPYRIGHT  HOLDER OR
 * HOLDERS  INCLUDED IN  THIS  NOTICE BE  LIABLE  FOR ANY  CLAIM, OR  ANY
 * SPECIAL INDIRECT  OR CONSEQUENTIAL DAMAGES, OR  ANY DAMAGES WHATSOEVER
 * RESULTING FROM LOSS  OF USE, DATA OR PROFITS, WHETHER  IN AN ACTION OF
 * CONTRACT, NEGLIGENCE  OR OTHER TORTIOUS  ACTION, ARISING OUT OF  OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Except as  contained in  this notice, the  name of a  copyright holder
 * shall not be used in advertising or otherwise to promote the sale, use
 * or other dealings in this Software without prior written authorization
 * of the copyright holder.
 *
 */

package org.slf4j.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import se.peertv.peertvsim.core.Reflection;

/**
 * A wrapper over {@link org.apache.log4j.Logger org.apache.log4j.Logger} in conformance with the {@link Logger} interface. Note that the logging
 * levels mentioned in this class refer to those defined in the <a href="http://logging.apache.org/log4j/docs/api/org/apache/log4j/Level.html"><code>org.apache.log4j.Level</code></a>
 * class.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public final class SimLog4jLoggerAdapterContainer extends MarkerIgnoringBase implements LocationAwareLogger {


	@Override
	public String toString() {

		return name + "........";
	}

	public final Map<String, org.apache.log4j.Logger> loggers;

	private final String name;

	private final boolean className;

	/**
	 * Following the pattern discussed in pages 162 through 168 of "The complete log4j manual".
	 */
	final static String FQCN = SimLog4jLoggerAdapterContainer.class.getName();

	// WARN: Log4jLoggerAdapter constructor should have only package access so that
	// only Log4jLoggerFactory be able to create one.
	SimLog4jLoggerAdapterContainer(String name, boolean className) {
		this.name = name;
		this.className = className;
		loggers = new HashMap<String, org.apache.log4j.Logger>();
	}

	public String getName() {
		return getLog().getName();
	}

	/**
	 * Is this getLog() instance enabled for the TRACE level?
	 * 
	 * @return True if this Logger is enabled for level TRACE, false otherwise.
	 */
	public boolean isTraceEnabled() {
		return getLog().isTraceEnabled();
	}

	/**
	 * Log a message object at level TRACE.
	 * 
	 * @param msg -
	 *            the message object to be logged
	 */
	public void trace(String msg) {
		NDCPush();
		getLog().log(FQCN, Level.TRACE, msg, null);
		NDC.pop();
	}

	/**
	 * Log a message at level TRACE according to the specified format and argument.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for level TRACE.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void trace(String format, Object arg) {
		if (getLog().isTraceEnabled()) {
			String msgStr = MessageFormatter.format(format, arg);
			NDCPush();
			getLog().log(FQCN, Level.TRACE, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level TRACE according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the TRACE level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg1
	 *            the first argument
	 * @param arg2
	 *            the second argument
	 */
	public void trace(String format, Object arg1, Object arg2) {
		if (getLog().isTraceEnabled()) {
			String msgStr = MessageFormatter.format(format, arg1, arg2);
			NDCPush();
			getLog().log(FQCN, Level.TRACE, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level TRACE according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the TRACE level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void trace(String format, Object[] argArray) {
		if (getLog().isTraceEnabled()) {
			String msgStr = MessageFormatter.arrayFormat(format, argArray);
			NDCPush();
			getLog().log(FQCN, Level.TRACE, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log an exception (throwable) at level TRACE with an accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void trace(String msg, Throwable t) {
		NDCPush();
		getLog().log(FQCN, Level.TRACE, msg, t);
		NDC.pop();
	}

	/**
	 * Is this getLog() instance enabled for the DEBUG level?
	 * 
	 * @return True if this Logger is enabled for level DEBUG, false otherwise.
	 */
	public boolean isDebugEnabled() {
		return getLog().isDebugEnabled();
	}

	/**
	 * Log a message object at level DEBUG.
	 * 
	 * @param msg -
	 *            the message object to be logged
	 */
	public void debug(String msg) {
		NDCPush();
		getLog().log(FQCN, Level.DEBUG, msg, null);
		NDC.pop();
	}

	/**
	 * Log a message at level DEBUG according to the specified format and argument.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for level DEBUG.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void debug(String format, Object arg) {
		if (getLog().isDebugEnabled()) {
			String msgStr = MessageFormatter.format(format, arg);
			NDCPush();
			getLog().log(FQCN, Level.DEBUG, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level DEBUG according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the DEBUG level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg1
	 *            the first argument
	 * @param arg2
	 *            the second argument
	 */
	public void debug(String format, Object arg1, Object arg2) {
		if (getLog().isDebugEnabled()) {
			String msgStr = MessageFormatter.format(format, arg1, arg2);
			NDCPush();
			getLog().log(FQCN, Level.DEBUG, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level DEBUG according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the DEBUG level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void debug(String format, Object[] argArray) {
		if (getLog().isDebugEnabled()) {
			String msgStr = MessageFormatter.arrayFormat(format, argArray);
			NDCPush();
			getLog().log(FQCN, Level.DEBUG, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log an exception (throwable) at level DEBUG with an accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void debug(String msg, Throwable t) {
		NDCPush();
		getLog().log(FQCN, Level.DEBUG, msg, t);
		NDC.pop();
	}

	/**
	 * Is this getLog() instance enabled for the INFO level?
	 * 
	 * @return True if this Logger is enabled for the INFO level, false otherwise.
	 */
	public boolean isInfoEnabled() {
		return getLog().isInfoEnabled();
	}

	/**
	 * Log a message object at the INFO level.
	 * 
	 * @param msg -
	 *            the message object to be logged
	 */
	public void info(String msg) {
		NDCPush();
		getLog().log(FQCN, Level.INFO, msg, null);
		NDC.pop();
	}

	/**
	 * Log a message at level INFO according to the specified format and argument.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the INFO level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void info(String format, Object arg) {
		if (getLog().isInfoEnabled()) {
			String msgStr = MessageFormatter.format(format, arg);
			NDCPush();
			getLog().log(FQCN, Level.INFO, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at the INFO level according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the INFO level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg1
	 *            the first argument
	 * @param arg2
	 *            the second argument
	 */
	public void info(String format, Object arg1, Object arg2) {
		if (getLog().isInfoEnabled()) {
			String msgStr = MessageFormatter.format(format, arg1, arg2);
			NDCPush();
			getLog().log(FQCN, Level.INFO, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level INFO according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the INFO level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void info(String format, Object[] argArray) {
		if (getLog().isInfoEnabled()) {
			String msgStr = MessageFormatter.arrayFormat(format, argArray);
			NDCPush();
			getLog().log(FQCN, Level.INFO, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log an exception (throwable) at the INFO level with an accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void info(String msg, Throwable t) {
		NDCPush();
		getLog().log(FQCN, Level.INFO, msg, t);
		NDC.pop();
	}

	/**
	 * Is this getLog() instance enabled for the WARN level?
	 * 
	 * @return True if this Logger is enabled for the WARN level, false otherwise.
	 */
	public boolean isWarnEnabled() {
		return getLog().isEnabledFor(Level.WARN);
	}

	/**
	 * Log a message object at the WARN level.
	 * 
	 * @param msg -
	 *            the message object to be logged
	 */
	public void warn(String msg) {
		NDCPush();
		getLog().log(FQCN, Level.WARN, msg, null);
		NDC.pop();
	}

	/**
	 * Log a message at the WARN level according to the specified format and argument.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the WARN level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void warn(String format, Object arg) {
		if (getLog().isEnabledFor(Level.WARN)) {
			String msgStr = MessageFormatter.format(format, arg);
			NDCPush();
			getLog().log(FQCN, Level.WARN, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at the WARN level according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the WARN level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg1
	 *            the first argument
	 * @param arg2
	 *            the second argument
	 */
	public void warn(String format, Object arg1, Object arg2) {
		if (getLog().isEnabledFor(Level.WARN)) {
			String msgStr = MessageFormatter.format(format, arg1, arg2);
			NDCPush();
			getLog().log(FQCN, Level.WARN, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level WARN according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the WARN level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void warn(String format, Object[] argArray) {
		if (getLog().isEnabledFor(Level.WARN)) {
			String msgStr = MessageFormatter.arrayFormat(format, argArray);
			NDCPush();
			getLog().log(FQCN, Level.WARN, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log an exception (throwable) at the WARN level with an accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void warn(String msg, Throwable t) {
		NDCPush();
		getLog().log(FQCN, Level.WARN, msg, t);
		NDC.pop();
	}

	/**
	 * Is this getLog() instance enabled for level ERROR?
	 * 
	 * @return True if this Logger is enabled for level ERROR, false otherwise.
	 */
	public boolean isErrorEnabled() {
		return getLog().isEnabledFor(Level.ERROR);
	}

	/**
	 * Log a message object at the ERROR level.
	 * 
	 * @param msg -
	 *            the message object to be logged
	 */
	public void error(String msg) {
		NDCPush();
		getLog().log(FQCN, Level.ERROR, msg, null);
		NDC.pop();
	}

	/**
	 * Log a message at the ERROR level according to the specified format and argument.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the ERROR level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void error(String format, Object arg) {
		if (getLog().isEnabledFor(Level.ERROR)) {
			String msgStr = MessageFormatter.format(format, arg);
			NDCPush();
			getLog().log(FQCN, Level.ERROR, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at the ERROR level according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the ERROR level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param arg1
	 *            the first argument
	 * @param arg2
	 *            the second argument
	 */
	public void error(String format, Object arg1, Object arg2) {
		if (getLog().isEnabledFor(Level.ERROR)) {
			String msgStr = MessageFormatter.format(format, arg1, arg2);
			NDCPush();
			getLog().log(FQCN, Level.ERROR, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log a message at level ERROR according to the specified format and arguments.
	 * 
	 * <p>
	 * This form avoids superfluous object creation when the getLog() is disabled for the ERROR level.
	 * </p>
	 * 
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void error(String format, Object[] argArray) {
		if (getLog().isEnabledFor(Level.ERROR)) {
			String msgStr = MessageFormatter.arrayFormat(format, argArray);
			NDCPush();
			getLog().log(FQCN, Level.ERROR, msgStr, null);
			NDC.pop();
		}
	}

	/**
	 * Log an exception (throwable) at the ERROR level with an accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void error(String msg, Throwable t) {
		NDCPush();
		getLog().log(FQCN, Level.ERROR, msg, t);
		NDC.pop();
	}

	public void log(Marker marker, String callerFQCN, int level, String msg, Throwable t) {
		Level log4jLevel;
		switch (level) {
		case LocationAwareLogger.TRACE_INT:
			log4jLevel = Level.TRACE;
			break;
		case LocationAwareLogger.DEBUG_INT:
			log4jLevel = Level.DEBUG;
			break;
		case LocationAwareLogger.INFO_INT:
			log4jLevel = Level.INFO;
			break;
		case LocationAwareLogger.WARN_INT:
			log4jLevel = Level.WARN;
			break;
		case LocationAwareLogger.ERROR_INT:
			log4jLevel = Level.ERROR;
			break;
		default:
			throw new IllegalStateException("Level number " + level + " is not recognized.");
		}
		NDCPush();
		getLog().log(callerFQCN, log4jLevel, msg, t);
		NDC.pop();
	}

	public org.apache.log4j.Logger getLog() {
		String threadGroup = Reflection.getExecutingGroup();

		if (!loggers.containsKey(threadGroup)) {
			org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(threadGroup);
			loggers.put(threadGroup, logger);
		}

		if (!className) {
			threadGroup = name + "." + threadGroup;
			if (!loggers.containsKey(threadGroup)) {
				org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(threadGroup);
				loggers.put(threadGroup, logger);
			}

		}
		return loggers.get(threadGroup);
	}

	public static org.apache.log4j.Logger getRootLogger() {
		return org.apache.log4j.Logger.getRootLogger();
	}

	private void NDCPush() {
		NDC.push(name);
	}
}
