package uk.co.thomasc.wordmaster.util;

public class TimeUtil {
	
	public static String timeSince(long time) {
		long diff = (System.currentTimeMillis() / 1000) - time;
		if (diff / 60 == 0) {
			return diff + "s";
		} else if (diff / 3600 == 0) {
			return (diff / 60) + "m";
		} else if (diff / 86400 == 0) {
			return (diff / 3600) + "h";
		} else {
			return (diff / 86400) + "d";
		}
	}
	
}