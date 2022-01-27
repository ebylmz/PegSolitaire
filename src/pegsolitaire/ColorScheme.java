package pegsolitaire;


import java.awt.Component;
import javax.swing.ImageIcon;
import java.awt.Color;

public enum ColorScheme {
    BLACK (new Color(28, 34, 38)),
    BLUE (new Color(0xCDE0FF)),
    RED (new Color(0xE10550)),
    GRAY (new Color(143, 155, 166)),
    GREEN (new Color(0x129468)),
    WHITE (new Color(0xFFFFFF));

    private final Color color;

    private ColorScheme (Color c) {
        color = c;
    }

    /**
     * 
     private ColorScheme (ImageIcon icon) {
         this.icon = icon;
        }
    */

    public Color getColor () {return color;}

    public static void setColor (Component comp, ColorScheme bg, ColorScheme fg) {
        comp.setBackground(bg.getColor());
        comp.setForeground(fg.getColor());
    }

    public static void setColor (Component comp, ColorScheme bg) {
        comp.setBackground(bg.getColor());
    }
}
