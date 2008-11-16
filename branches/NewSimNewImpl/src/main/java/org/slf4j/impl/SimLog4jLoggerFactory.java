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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import se.peertv.log.log4jBind.Log4jLoggerFactory;
import se.peertv.log.utils.LogPropertiesWriter;
import se.peertv.peertvsim.SimulableSystem;
import se.peertv.peertvsim.core.Reflection;

/**
 * Log4jLoggerFactory is an implementation of {@link ILoggerFactory} returning the appropriate named {@link SimLog4jLoggerAdapterContainer} instance.
 * 
 * @author Roberto;
 */
public class SimLog4jLoggerFactory implements ILoggerFactory {

	Map<String, Logger> loggerMap;

	private Log4jLoggerFactory factory;

	private LogPropertiesWriter writer;

	private Set<String> written;

	public SimLog4jLoggerFactory() {
		if (SimulableSystem.isSimulation()) {
			loggerMap = new HashMap<String, Logger>();
			written = new HashSet<String>();
			writer = LogPropertiesWriter.getInstance();
		} else {
			factory = new Log4jLoggerFactory();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.slf4j.ILoggerFactory#getLogger(java.lang.String)
	 */
	public Logger getLogger(String name) {
		Logger slf4jLogger = null;

		if (!SimulableSystem.isSimulation()) {
			slf4jLogger = factory.getLogger(name);
		} else {
			// protect against concurrent access of loggerMap
			synchronized (this) {
				// Logger for the specific threadgroup
				String threadGroup = Reflection.getExecutingGroup();
				// System.out.println("Name: " + name + " th: " + threadGroup);
				boolean className = false;
				String loggerName = null;
				/* Checks if it's a name of a class. If it's not, */
				try {
					Class.forName(name);
					loggerName = threadGroup;
					className = true;
				} catch (ClassNotFoundException e) {
					loggerName = threadGroup.concat(".").concat(name);
					if (!written.contains(name)) {
						writer.writeAppenderForNamedLoggerToStdAppender(name, threadGroup);
						written.add(name);
					}
				}

				// All loggers with the specified name
				slf4jLogger = loggerMap.get(name);
				// Get the map containing all the logs with this name
				if (slf4jLogger == null) {
					slf4jLogger = new SimLog4jLoggerAdapterContainer(name, className);
					loggerMap.put(name, slf4jLogger);
				}

				SimLog4jLoggerAdapterContainer container = (SimLog4jLoggerAdapterContainer) slf4jLogger;
				if (!container.loggers.containsKey(loggerName)) {
					org.apache.log4j.Logger log4jLogger;

					if (name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
						log4jLogger = LogManager.getRootLogger();
					} else {
						log4jLogger = LogManager.getLogger(loggerName);
					}
					container.loggers.put(loggerName, log4jLogger);
				}

			}
		}

		// if (name.contains("org.mortbay.log") || name.contains("org.apache.mina")) {
		// org.apache.log4j.Logger l = SimLog4jLoggerAdapterContainer.getRootLogger();
		// return new SimLog4jLoggerAdapterContainer(LogManager.getRootLobgger(), false);
		// }

		return slf4jLogger;
	}
}
