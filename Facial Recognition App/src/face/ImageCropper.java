package face;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitar grafic pentru selectarea manuală de regiuni pătrat 128x128 dintr-o imagine mare.
 * Utilizatorul selectează cu mouse-ul poziția, iar subimaginea este salvată în folderul
 * `train_images/positive` sau `train_images/negative`, în funcție de etichetă.
 
 *Clasa ImageCropper.java este un instrument opțional, utilizat pentru selecția manuală a patch-urilor 128x128 din imagini mari, atunci când setul de date nu este complet sau trebuie ajustat manual. Aceasta permite generarea rapidă de imagini pozitive sau negative pentru antrenare.
 */
public class ImageCropper extends JFrame {
    private BufferedImage image;
    private String saveDir = "train_images/";
    private int count = 0;

	/**
	 * Creează o fereastră care încarcă imaginea dată și permite selectarea de patch-uri
	 * 128x128 prin clic și drag cu mouse-ul.
	 *
	 * @param imagePath calea către imaginea sursă din care se vor extrage pătratele
	 * @param label eticheta asociată (ex: „positive” sau „negative”) – determină folderul de salvare
	 */
    public ImageCropper(String imagePath, String label) {
        try {
            image = ImageIO.read(new File(imagePath));
            setTitle("Selectează pătrate 128x128 – " + label);
            setSize(image.getWidth(), image.getHeight());
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            File folder = new File(saveDir + label);
            if (!folder.exists()) folder.mkdirs();

            JPanel panel = new JPanel() {
                Point start = null;
                Point current = null;

                {
                    addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            start = e.getPoint();
                        }

                        public void mouseReleased(MouseEvent e) {
                            if (start == null) return;
                            int x = Math.max(0, Math.min(start.x, image.getWidth() - 128));
                            int y = Math.max(0, Math.min(start.y, image.getHeight() - 128));
                            try {
                                BufferedImage crop = image.getSubimage(x, y, 128, 128);
                                String path = saveDir + label + "/img" + (count++) + ".jpg";
                                ImageIO.write(crop, "jpg", new File(path));
                                System.out.println("Salvat: " + path);
                            } catch (Exception ex) {
                                System.err.println("Eroare la salvare: " + ex.getMessage());
                            }
                            start = null;
                            current = null;
                            repaint();
                        }
                    });

                    addMouseMotionListener(new MouseMotionAdapter() {
                        public void mouseDragged(MouseEvent e) {
                            current = e.getPoint();
                            repaint();
                        }
                    });
                }
				
				/**
				 * Redesenare grafică a conținutului imaginii și a pătratului verde de selecție
				 * care urmează să fie salvat.
				 *
				 * @param g obiectul grafic pentru desenare în panel
				 */
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, null);
                    if (start != null) {
                        int x = Math.max(0, Math.min(start.x, image.getWidth() - 128));
                        int y = Math.max(0, Math.min(start.y, image.getHeight() - 128));
                        g.setColor(Color.GREEN);
                        g.drawRect(x, y, 128, 128);
                    }
                }
            };
            setContentPane(panel);
            setVisible(true);
        } catch (Exception e) {
            System.err.println("Eroare la încărcarea imaginii: " + e.getMessage());
        }
    }
	
	/**
	 * Punctul de intrare pentru rularea independentă.
	 * Verifică parametrii și lansează interfața grafică cu imaginea specificată.
	 *
	 * @param args vector cu două elemente: calea imaginii și tipul etichetei („positive”/„negative”)
	 */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Folosire: java face.ImageCropper <cale_imagine> <pozitive/negative>");
            return;
        }
        new ImageCropper(args[0], args[1]);
    }
}
