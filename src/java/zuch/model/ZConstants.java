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
    
    public static String ARTWORK_DEFAULT_PATH = "/zuch/images/duke.png";
    
    //system folders
    public static String WIN_TMP_PATH= "C:/zuch/tmp/";
    public static String WIN_EN_INDEX_PATH = "C:/zuch/en_search_index/";
    public static String WIN_FR_INDEX_PATH = "C:/zuch/fr_search_index/";
    public static String WIN_SP_INDEX_PATH = "C:/zuch/sp_search_index/";
    public static String WIN_EN_SPELLCHK_PATH = "C:/zuch/en_spellchecker_data/";
    public static String WIN_FR_SPELLCHK_PATH = "C:/zuch/fr_spellchecker_data/";
    public static String WIN_SP_SPELLCHK_PATH = "C:/zuch/sp_spellchecker_data/";
    public static String WIN_IMAGE_PATH = "C:/zuch/images/";
    public static String WIN_TRACK_PATH = "C:/zuch/tracks/";
    public static String WIN_SAMPLE_PATH = "C:/zuch/samples/";
    
    public static String NIX_TMP_PATH= "/usr/zuch/tmp/";
    public static String NIX_EN_INDEX_PATH = "/usr/zuch/en_search_index/";
    public static String NIX_FR_INDEX_PATH = "/usr/zuch/fr_search_index/";
    public static String NIX_SP_INDEX_PATH = "/usr/zuch/sp_search_index/";
    public static String NIX_EN_SPELLCHK_PATH = "/usr/zuch/en_spellchecker_data/";
    public static String NIX_FR_SPELLCHK_PATH = "/usr/zuch/fr_spellchecker_data/";
    public static String NIX_SP_SPELLCHK_PATH = "/usr/zuch/sp_spellchecker_data/";
    public static String NIX_IMAGE_PATH = "/usr/zuch/images/";
    public static String NIX_TRACK_PATH = "/usr/zuch/tracks/";
    public static String NIX_SAMPLE_PATH = "/usr/zuch/samples/";
            
    
}
