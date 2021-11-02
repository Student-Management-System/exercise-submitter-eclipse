package net.ssehub.teaching.exercise_submitter.eclipse.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods for working with timestamps.
 * 
 * @author Adam
 */
public class TimeUtils {
    
    private static final DateTimeFormatter STANDARD = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static final DateTimeFormatter NO_COLONS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * No instances.
     */
    private TimeUtils() {
    }
    
    /**
     * Converts an {@link Instant} without a timezone to a timestamp in the current default timezone.
     *  
     * @param instant The instant to convert.
     * 
     * @return The local timestamp.
     */
    public static ZonedDateTime instantToLocalTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault());
    }
    
    /**
     * Converts an instant (without a timezone) to a timestamp string in the current default timezone.
     * 
     * @param instant The instant to convert.
     * 
     * @return The time as a string representation.
     */
    public static String instantToLocalString(Instant instant) {
        return STANDARD.format(instantToLocalTime(instant));
    }
    
    /**
     * Converts an instant (without a timezone) to a timestamp string in the current default timezone.
     * 
     * @param instant The instant to convert.
     * 
     * @return The time as a string representation, without any colons (safe to use as eclipse project name).
     */
    public static String instantToLocalStringNoColons(Instant instant) {
        return NO_COLONS.format(instantToLocalTime(instant));
    }
    
}
