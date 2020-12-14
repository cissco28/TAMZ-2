package com.example.pong_app;

public final class GameSet {
    public static final double PLAYER_SPEED = 20;
    public static final int PLAYER_SECTIONS = 11;
    public static final double BALL_SPEED_INCREASE = -1.25;
    public static final double PLAYER_GRAVITY = 0.5;

    public static final double[] PLAYER_SECTION_ANGLES = new double[] {-60 * Math.PI / 180, -48 * Math.PI / 180,
            -36 * Math.PI / 180, -24 * Math.PI / 180,
            -12 * Math.PI / 180, 0, 12 * Math.PI / 180,
            24* Math.PI / 180, 36 * Math.PI / 180,
            48 * Math.PI / 180, 60 * Math.PI / 180};

    public static final double[] PLAYERUP_SECTION_ANGLES = new double[] {60 * Math.PI / 180, 54 * Math.PI / 180,
            48 * Math.PI / 180, 42 * Math.PI / 180,
            36 * Math.PI / 180, 30 * Math.PI / 180,
            24 * Math.PI / 180, 18 * Math.PI / 180,
            12 * Math.PI / 180, 6 * Math.PI / 180,
            0 * Math.PI / 180};

    public static final double[] PLAYERDOWN_SECTION_ANGLES = new double[] {0 * Math.PI / 180, 6 * Math.PI / 180,
            12 * Math.PI / 180, 18 * Math.PI / 180,
            24 * Math.PI / 180, 30 * Math.PI / 180,
            36 * Math.PI / 180, 42 * Math.PI / 180,
            48 * Math.PI / 180, 54 * Math.PI / 180,
            60 * Math.PI / 180};
}
