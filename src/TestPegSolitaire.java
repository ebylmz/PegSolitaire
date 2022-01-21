import javax.imageio.plugins.tiff.ExifTIFFTagSet;
import javax.swing.JFrame;

public class TestPegSolitaire {
    public static void main(String[] args) {
        PegSolitaire game = new PegSolitaire();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setSize(500, 500);
        game.setVisible(true);
    }
}
