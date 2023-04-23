package com.falsepattern.rple.api;

public class LightConstants {
    public static final int COLOR_CHANNEL_RED = 0;
    public static final int COLOR_CHANNEL_GREEN = 1;
    public static final int COLOR_CHANNEL_BLUE = 2;
    // @formatter:off
    //                                                  black red green brown blue purple cyan lightgray gray pink lime yellow lightblue magenta orange white
    public static final int[] redColors   = new int[] {     0, 15,    0,    8,   0,    10,   0,       10,   5,  15,   8,    15,        0,     15,    15,   15 };
    public static final int[] greenColors = new int[] {     0,  0,   15,    3,   0,     0,  15,       10,   5,  10,  15,    15,        8,      0,    12,   15 };
    public static final int[] blueColors  = new int[] {     0,  0,    0,    0,  15,    15,  15,       10,   5,  13,   0,     0,       15,     15,    10,   15 };
    public static final int[][] colors = new int[][]{redColors, greenColors, blueColors};
    // @formatter:on
}
