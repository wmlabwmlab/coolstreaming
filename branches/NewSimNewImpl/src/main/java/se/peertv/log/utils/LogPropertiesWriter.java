package se.peertv.log.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

public class LogPropertiesWriter {

	private static final String LOG4J_DUMP_FILE = "./log4j.properties";

	private static LogPropertiesWriter instance;

	private PrintStream writer;

	private List<String> exclusions;

	public int headerLines = 0;

	private static File log4jfile;

	Level SCREEN_LOG_LEVEL, ROOT_LOG_LEVEL, MINA_LOG_LEVEL;
	

	StringBuilder headerBuilder;
	StringBuilder bodyLoggerBuilder;
	StringBuilder particularLoggerBuilder;

	private LogPropertiesWriter(PrintStream writer) {
		this.writer = writer;
		headerBuilder = new StringBuilder();
		bodyLoggerBuilder = new StringBuilder();
		particularLoggerBuilder = new StringBuilder();
	}

	public void initLogLevels(Level SCREEN_LOG_LEVEL, Level ROOT_LOG_LEVEL, Level MINA_LOG_LEVEL) {
		this.ROOT_LOG_LEVEL = ROOT_LOG_LEVEL;
		this.MINA_LOG_LEVEL = MINA_LOG_LEVEL;
		this.SCREEN_LOG_LEVEL = SCREEN_LOG_LEVEL;
	}

	public static LogPropertiesWriter getInstance() {
		if (instance == null) {
			log4jfile = new File(LOG4J_DUMP_FILE);
			PrintStream printStream = null;
			try {
				printStream = new PrintStream(log4jfile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			instance = new LogPropertiesWriter(printStream);
		}

		return instance;
	}

	public void writeHeader(String patterLayoutName, Level SCREEN_LOG_LEVEL, Level ROOT_LOG_LEVEL, Level MINA_LOG_LEVEL, String simPatternLayout) {

		writeHln("#-- Root Logger");
		if (SCREEN_LOG_LEVEL != Level.OFF) {
			writeHln("log4j.rootLogger=" + ROOT_LOG_LEVEL + ", myscreen");
		} else {
			writeHln("log4j.rootLogger=" + ROOT_LOG_LEVEL);
		}

		writeHln("");

		writeHln("# Mina");
		writeHln("log4j.logger.org.apache.mina=" + MINA_LOG_LEVEL);
		writeHln("log4j.logger.org.apachconfigFilenamee.mina.filter=OFF");
		writeHln("log4j.logger.org.apache.mina.filter.thread.ThreadPoolFilter=OFF");
		writeHln("");

		writeHln("# HTTP Client (publisher communication)");
		writeHln("log4j.logger.org.apache.commons.httpclient=OFF");
		writeHln("");

		writeHln("#--- Settings for the stdout appender ");
		writeHln("log4j.appender.myscreen=org.apache.log4j.ConsoleAppender");
		writeHln("log4j.appender.myscreen.layout=" + simPatternLayout);
		writeHln("log4j.appender.myscreen.layout.ConversionPattern=[%c{1}] %-5p %x - %m%n");
		writeHln("log4j.appender.myfile.Threshold=" + SCREEN_LOG_LEVEL);
		writeHln("");

		writer.flush();

	}

	public void writeAppenderForClass(Level level, String loggerName, String patternLayout, String fileName) {

		String[] strings = new String[] { loggerName };
		writeAppenderInternal(level, loggerName, strings, patternLayout, fileName);
	}

	public void writeAppenderForNamedLoggerToFile(Level level, String groupLoggerName, String loggerName, String patternLayout, String fileName) {

		String[] strings = new String[] { groupLoggerName, loggerName };
		writeAppenderInternal(level, loggerName, strings, patternLayout, fileName);
	}

	public void writeAppenderForNamedLoggerToStdAppender(String loggerName, String appenderName) {

		if (exclusions != null && exclusions.contains(loggerName)) {
			return;
		}

		writer.close();

		try {

			/*
			 * If we have a particular logger to write in the log4j file, we delete the log4j file and rewrite it with all the properties that were
			 * written before, but we also write the new logger in the middle of the log4j file, before the appenders. This follows the log4j property
			 * definition rule according to which loggers should defined before the corresponding appenders.
			 * 
			 */
			log4jfile.delete();
			log4jfile.createNewFile();
			writer = new PrintStream(log4jfile);

			// ROOT logger,etc..
			writer.print(headerBuilder.toString());

			// Other particular loggers
			writer.print(particularLoggerBuilder.toString());
			writePLln("log4j.logger." + loggerName + "=" + ROOT_LOG_LEVEL + ", " + appenderName);
			
			// Appenders and file loggers
			writer.print(bodyLoggerBuilder.toString());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		writer.flush();

		PropertyConfigurator.configure(LOG4J_DUMP_FILE);
	}

	private void writeAppenderInternal(Level level, String appenderName, String[] loggerNames, String patternLayout, String fileName) {

		// LoggerName should be threadGroup
		StringBuffer builder = new StringBuffer();
		for (String string : loggerNames) {
			builder.append(", ");
			builder.append(string);
		}
		writeBln("log4j.logger." + appenderName + "=" + level.toString() + "" + builder.toString());

		writeBln("log4j.appender." + appenderName + "=org.apache.log4j.FileAppender");
		writeBln("log4j.appender." + appenderName + ".File=" + fileName);

		writeBln("log4j.appender." + appenderName + ".layout=" + patternLayout);
		writeBln("log4j.appender." + appenderName + ".layout.ConversionPattern=" + "[%c{1}] %-5p %x - %m%n");
		writeBln("log4j.appender." + appenderName + ".threshold=" + level.toString());
		writeBln("");
		writer.flush();
	}

	/**
	 * Specifies which logger should not be written in the log4j property file as Named logger. This is the case when it already has a logger
	 * associated with them in the log4j property file.
	 */
	public void setLoggerExclusions(String... strings) {
		exclusions = Arrays.asList(strings);
	}

	private void writeHln(String str) {
		headerBuilder.append(str);
		writer.println(str);
		headerBuilder.append("\n");
	}

	private void writeBln(String str) {
		bodyLoggerBuilder.append(str);
		writer.println(str);
		bodyLoggerBuilder.append("\n");
	}

	private void writePLln(String str) {
		particularLoggerBuilder.append(str);
		writer.println(str);
		particularLoggerBuilder.append("\n");
	}

}