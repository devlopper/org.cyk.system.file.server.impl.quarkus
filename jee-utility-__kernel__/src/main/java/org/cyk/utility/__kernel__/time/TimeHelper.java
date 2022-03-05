package org.cyk.utility.__kernel__.time;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;

public interface TimeHelper {

	static LocalDateTime getLocalDateTimeFromMilliseconds(Long milliseconds) {
		if(milliseconds == null)
			return null;
		return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	static LocalDate getLocalDateFromMilliseconds(Long milliseconds) {
		if(milliseconds == null)
			return null;
		return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	static Date getDateFromMilliseconds(Long milliseconds) {
		if(milliseconds == null)
			return null;
		return new Date(milliseconds);
	}
	
	static LocalDateTime parseLocalDateTimeFromDate(Date date) {
		if(date == null)
			return null;
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	static LocalDateTime parseLocalDateTimeFromString(String string) {
		if(StringHelper.isBlank(string))
			return null;
		return  LocalDateTime.parse(string);
	}
	
	static String formatLocalDateTimeFromString(String string,String pattern) {
		if(StringHelper.isBlank(string))
			return null;
		pattern = ValueHelper.defaultToIfBlank(pattern, "dd/MM/yyyy");
		LocalDateTime localDateTime =  parseLocalDateTimeFromString(string);
		if(localDateTime == null)
			return null;
		return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static Duration getDurationTillNow(String string) {
		if(StringHelper.isBlank(string))
			return null;
		return Duration.between(parseLocalDateTimeFromString(string), LocalDateTime.now());
	}
	
	static void pause(Long numberOfMillisecond) {
		try {
			Thread.sleep(numberOfMillisecond);
		} catch (InterruptedException exception) {
			LogHelper.log(exception, TimeHelper.class);
		}
	}
	
	static String formatLocalDateTime(LocalDateTime localDateTime,String pattern) {
		if(localDateTime == null)
			return null;
		return DateTimeFormatter.ofPattern(pattern, Locale.FRENCH).format(localDateTime);
	}
	
	static String formatLocalDateTime(LocalDateTime localDateTime) {
		if(localDateTime == null)
			return null;
		return formatLocalDateTime(localDateTime, "dd/MM/yyyy à HH:mm");
	}
	
	static String formatLocalDateTimeTillSecond(LocalDateTime localDateTime) {
		return formatLocalDateTime(localDateTime, "dd/MM/yyyy à HH:mm:ss");
	}
	
	static String formatLocalDateTimeTillMillisecond(LocalDateTime localDateTime) {
		return formatLocalDateTime(localDateTime, "dd/MM/yyyy à HH:mm:ss.S");
	}
	
	static String formatLocalDate(LocalDate localDate,String pattern) {
		if(localDate == null)
			return null;
		return DateTimeFormatter.ofPattern(pattern, Locale.FRENCH).format(localDate);
	}
	
	static String formatLocalDate(LocalDate localDate) {
		if(localDate == null)
			return null;
		return formatLocalDate(localDate, "dd/MM/yyyy");
	}
	
	static String formatDate(Date date,String pattern) {
		if(date == null)
			return null;
		return new SimpleDateFormat(pattern,Locale.FRENCH).format(date);
	}
	
	static String formatDate(Date date) {
		if(date == null)
			return null;
		return formatDate(date, "dd/MM/yyyy");
	}
	
	static String formatDuration(Long numberOfMilliseconds) {
		String string = Duration.ofMillis(numberOfMilliseconds).toString();
		string = string.substring(2);
		return string;
	}

	static Long toMillisecond(LocalDate date) {
		if(date == null)
			return null;
		return date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	
	static Long toMillisecond(LocalDateTime localDateTime) {
		if(localDateTime == null)
			return null;
		return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	static Date convertLocalDateToDate(LocalDate localDate) {
		if(localDate == null)
			return null;
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	static Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
		if(localDateTime == null)
			return null;
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
	static Date convertLocalTimeToDate(LocalTime localTime) {
		if(localTime == null)
			return null;
		return Date.from(localTime.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
	}
}