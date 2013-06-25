package uk.co.thomasc.wordmaster.util;

import org.junit.Test;
import org.junit.Assert;

public class TimeUtilTest {

	@Test
	public void timeSinceTest() {
		long fiveSeconds = TimeUtil.now() - 5;
		String timeString = TimeUtil.timeSince(fiveSeconds);
		Assert.assertEquals("5s", timeString);
		
		long sixMinutes = TimeUtil.now() - (6 * 60);
		timeString = TimeUtil.timeSince(sixMinutes);
		Assert.assertEquals("6m", timeString);
	}
	
}
