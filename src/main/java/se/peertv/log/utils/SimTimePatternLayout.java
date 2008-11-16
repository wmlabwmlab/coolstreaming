/**
 * 27 mar 2008
 * @author Andreas
 */
package se.peertv.log.utils;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import se.peertv.peertvsim.SimulableSystem;

/**
 * @author Roberto
 * 
 */
public class SimTimePatternLayout extends PatternLayout {

	@Override
	public String format(final LoggingEvent event) {
		return SimulableSystem.currentTimeMillis() + " " + super.format(event);
	}

}
