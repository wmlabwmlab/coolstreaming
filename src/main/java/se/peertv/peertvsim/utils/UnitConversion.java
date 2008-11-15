/*
 * UnitConversion.java
 *
 * Created on September 12, 2007, 2:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package se.peertv.peertvsim.utils;

/**
 *
 * @author meemo
 */
public class UnitConversion
{   
    private UnitConversion()
    {
    }
    
    private static final int bitsInByte             = 8;
    private static final int minutesInHour          = 60;
    private static final int secondsInMinute        = 60;
    private static final int milliSecondsInSecond   = 1000;
    private static final int kilo                   = 1000;
 
    public static double byteToBit(double bytes)
    {
        return bytes * bitsInByte;
    }
    
    public static double bitToByte(double bits)
    {
        return bits / bitsInByte;
    }
    
    public static double kiloToUnity(double kilos)
    {
        return kilos * kilo;
    }

    public static double unityToKilo(double bytes)
    {
        return bytes / kilo;
    }
    
    public static double kilobitToByte(double kilobits)
    {
        return  kiloToUnity(bitToByte(kilobits));
    }

    public static double byteToKilobit(double bytes)
    {
        return byteToBit(unityToKilo(bytes));
    }
    
    public static double hourToMinute(double hours)
    {
        return hours * minutesInHour;
    }
    
    public static double minuteToHour(double minutes)
    {
        return minutes / minutesInHour;
    }
    
    public static double minuteToSeconds(double minutes)
    {
        return minutes * secondsInMinute;
    }
    
    public static double secondToMinute(double seconds)
    {
        return seconds / secondsInMinute;
    }
    
    public static double secToMilliSec(double seconds)
    {
        return seconds * milliSecondsInSecond;
    }
    
    public static double hourToMilliSec(double hours)     
    {
        return secToMilliSec(hourToSecond(hours));
    }
        
    public static double milliSecToSecond(double milliSec)
    {
        return milliSec / milliSecondsInSecond;
    }

    public static double hourToSecond(double hours)
    {
        return minuteToSeconds(hourToMinute(hours));
    }

    public static double secondToHour(double seconds)
    {
        return minuteToHour(secondToMinute(seconds));
    }
    
    public static double milliSecToHour(int milliSec) 
    {
        return secondToHour(milliSecToSecond(milliSec));
    }
}
