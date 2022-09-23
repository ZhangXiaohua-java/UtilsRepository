package cloud.huel.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
	
	
	public static String getLocalStandardTime() {
		ZonedDateTime now = ZonedDateTime.now();
		return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	}

	public static void main(String[] args) {
		getLocalStandardTime();
	}
	
}
