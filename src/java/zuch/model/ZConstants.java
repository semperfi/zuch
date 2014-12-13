/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zuch.model;

/**
 *
 * @author florent
 */
public interface ZConstants {
    
    public static final long DAY = 86400 * 1000 ; //milli sec in a day
    public static final long HOUR = 3600 * 1000 ; //milli sec in a hour
    public static final long MINUTE = 60 * 1000 ; //milli sec in a minute
    public static final long LENDING_LIFE_TIME = 3;
    public static final String APP_AUDIO_TYPE = "audio/mpeg";
    public static final String APP_AUDIO_EXT = ".mp3";
    public static String tokenQueryParam = "tk";
    public static String rangeTokenQueryParam = "rtk";
    public static int STREAM_BUFFERSIZE = 8 * 1024; //(8K) do not change this value, if so it will causes error in chrome
    public static int PART_FOR_FOOTPRINT = 5;
    
}
