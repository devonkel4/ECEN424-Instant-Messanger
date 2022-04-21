public enum Color {
//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_BLACK = "\u001B[30m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
//    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_WHITE = "\u001B[36m";

    //Reset and blank
    RESET("\u001B[0m"),
    NONE(""),
    //Text colors
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    //BG colors
    BG_BLACK("\u001B[40m"),
    BG_RED("\u001B[41m"),
    BG_GREEN("\u001B[42m"),
    BG_YELLOW("\u001B[43m"),
    BG_BLUE("\u001B[44m"),
    BG_PURPLE("\u001B[45m"),
    BG_CYAN("\u001B[46m"),
    BG_WHITE("\u001B[47m"),
    //Formatting
    BOLD("\u001B[1m"),
    ITALIC("\u001B[3m"),
    UNDERLINE("\u001B[4m"),
    STRIKE("\u001B[9m"),
    REVERSE("\u001B[7m"),
    FRAME("\u001B[51m")
    ;
    //valued enum bs
    private final String color;
    public String getColor(){
        return color;
    }
    private Color(String color){
        this.color = color;
    }
    //Makes it easier to print (can directly do sout(ColorName) instead of ColorName.getColor()
    @Override
    public String toString() {
        return getColor();
    }
}
