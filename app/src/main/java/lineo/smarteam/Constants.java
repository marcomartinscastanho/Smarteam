package lineo.smarteam;

/**
 * Created by marco on 26/09/2016.
 */
public abstract class Constants {
    public static final char C_WIN = 'W';
    public static final char C_DRAW = 'D';
    public static final char C_DEFEAT = 'L';
    public static final char C_ABSENCE = '-';
    public static final char C_FAULT = 'F';

    public static final int MIN_PLAYERS_PER_MATCH = 6;
    public static final int MAX_PLAYERS_PER_MATCH = 22;

    public enum ThemeColor{
        RED("Red"),
        PINK("Pink"),
        PURPLE("Purple"),
        DEEP_PURPLE("Deep Purple"),
        INDIGO("Indigo"),
        BLUE("Blue"),
        LIGHT_BLUE("Light Blue"),
        CYAN("Cyan"),
        TEAL("Teal"),
        GREEN("Green"),
        LIGHT_GREEN("Light Green"),
        LIME("Lime"),
        YELLOW("Yellow"),
        AMBER("Amber"),
        ORANGE("Orange"),
        DEEP_ORANGE("Deep Orange"),
        BROWN("Brown"),
        GREY("Grey"),
        BLUE_GREY("Blue Grey");

        public final String themeColorName;
        ThemeColor(String name){
            this.themeColorName=name;
        }
    }
}
