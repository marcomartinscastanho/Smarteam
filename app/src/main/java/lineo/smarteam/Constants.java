package lineo.smarteam;

/**
 * Created by marco on 26/09/2016.
 */
public abstract class Constants {
    public enum Result{
        WIN("W"),
        DRAW("D"),
        DEFEAT("L"),
        ABSENCE("-"),
        FAULT("F");

        public final String resultCharacter;
        Result(String resChar){
            this.resultCharacter=resChar;
        }
        public String getChar(){
            return resultCharacter;
        }
    }

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
        public String getColorName(){
            return themeColorName;
        }
    }
}
