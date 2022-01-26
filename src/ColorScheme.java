import java.awt.Component;
import java.awt.Color;

public enum ColorScheme {
    BLACK, BLUE, RED, GRAY, GREEN, WHITE; 

    public Color getColor () {
        // this refers to one of the objects BLACK, RED, GRAY...
        Color c = null;
        switch (this) {
            case BLACK:
                c = new Color(28, 34, 38);
                break;
            case RED:
                c = new Color(0xE10550);
                break;
            case GRAY:
                c = new Color(143, 155, 166);
                break;
            case GREEN:
                c = new Color(0x129468);
                break;
            case WHITE:
                c = new Color(0xFFFFFF);
                break;
            case BLUE:
                c = new Color(0x0000FF);
                break;
        }
        return c;
    }

    public static void setColor (Component comp, ColorScheme bg, ColorScheme fg) {
        comp.setBackground(bg.getColor());
        comp.setForeground(fg.getColor());
    }

    public static void setColor (Component comp, ColorScheme bg) {
        comp.setBackground(bg.getColor());
    }
}
