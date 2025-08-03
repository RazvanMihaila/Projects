package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import com.formdev.flatlaf.FlatLightLaf;
/**
 * Interfață grafică principală a aplicației de recunoaștere facială.
 * Permite utilizatorului să:
 * - antreneze detectorul de cap
 * - captureze imagini pentru o persoană
 * - antreneze clasificatorul pentru acea persoană
 * - ruleze recunoașterea facială live
 * - deschidă folderul imaginilor capturate pentru revizuire
 */
public class FaceRecognitionApp {
    private static final String JAVA_CMD = "java -cp \"bin;opencv-4110.jar;flatlaf-3.6.jar\" -Djava.library.path=\"D:/opencv/opencv/build/java/x64\" ";


	/**
	 * Punctul de pornire al aplicației. Inițializează interfața grafică Swing
	 * și lansează execuția într-un fir de execuție separat.
	 *
	 * @param args neutilizat
	 */
    public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
		} catch (Exception ex) {
			System.err.println("Nu s-a putut aplica tema FlatLaf.");
		}

        SwingUtilities.invokeLater(FaceRecognitionApp::createGUI);
    }

	/**
	 * Creează și configurează fereastra principală Swing, adaugă toate butoanele
	 * și câmpurile necesare pentru interacțiunea cu aplicația.
	 * Leagă fiecare buton de acțiunea sa corespunzătoare prin `runCommand(...)`.
	 */
    private static void createGUI() {
		// Aplica temă FlatLaf
		try {
			UIManager.setLookAndFeel(new FlatLightLaf()); // sau FlatDarculaLaf pentru dark mode
		} catch (Exception ex) {
			System.err.println("Eroare la setarea temei: " + ex);
		}

		JFrame frame = new JFrame("Recunoaștere Facială - Aplicație");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(520, 360);
		frame.setLayout(new BorderLayout(15, 15));
		frame.getContentPane().setBackground(Color.white);

		Font font = new Font("Segoe UI", Font.PLAIN, 14);

		JTextField nameField = new JTextField();
		JTextField countField = new JTextField();
		nameField.setFont(font);
		countField.setFont(font);

		JButton trainCapBtn = new JButton("1. Antrenează detector cap");
		JButton captureBtn = new JButton("2. Capturează imagini");
		JButton trainPersonBtn = new JButton("3. Antrenează persoană");
		JButton recognizeBtn = new JButton("4. Recunoaștere live");
		JButton editImagesBtn = new JButton("Modifică imaginile salvate");

		// Setare font uniform
		JButton[] buttons = {trainCapBtn, captureBtn, trainPersonBtn, recognizeBtn, editImagesBtn};
		for (JButton btn : buttons) {
			btn.setFont(font);
			btn.setPreferredSize(new Dimension(250, 35));
		}

		// Panel pentru input
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(new JLabel("Nume persoană:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(new JLabel("Număr imagini:"), gbc);
		gbc.gridx = 1;
		inputPanel.add(countField, gbc);

		// Panel pentru butoane
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80)); // margini laterale

		for (int i = 0; i < buttons.length - 1; i++) { // nu adăugăm ultimul buton aici
			JButton btn = buttons[i];
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);
			btn.setMaximumSize(new Dimension(300, 40)); // dimensiune fixă
			centerPanel.add(btn);
			centerPanel.add(Box.createVerticalStrut(10)); // spațiu între ele
		}
		
		JLabel progressLabel = new JLabel(" ");
		progressLabel.setFont(font);
		progressLabel.setForeground(Color.GRAY);
		progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.add(progressLabel);

		
		editImagesBtn.setMaximumSize(new Dimension(300, 35));
		JPanel southPanel = new JPanel();
		southPanel.setOpaque(false);
		southPanel.add(editImagesBtn);



		// Așezare în fereastră
		frame.add(inputPanel, BorderLayout.NORTH);
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.add(southPanel, BorderLayout.SOUTH);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// === Listeners ===
		trainCapBtn.addActionListener(e -> runCommand(JAVA_CMD + "face.HeadDetectorTrainer"));

		captureBtn.addActionListener(e -> {
			String name = nameField.getText().trim();
			String countStr = countField.getText().trim();

			if (!name.isEmpty() && !countStr.isEmpty()) {
				int count;
				try {
					count = Integer.parseInt(countStr);
				} catch (NumberFormatException ex) {
					showError("Numărul imaginilor trebuie să fie un număr întreg.");
					return;
				}

				progressLabel.setText("Capturare în curs...");
				captureBtn.setEnabled(false);

				new Thread(() -> {
					try {
						camera.ImageCollector collector = new camera.ImageCollector(name, count, (captured) -> {
							SwingUtilities.invokeLater(() -> progressLabel.setText("Imagini capturate: " + captured + "/" + count));
						});
						collector.run();
					} catch (Exception ex) {
						SwingUtilities.invokeLater(() -> showError("Eroare la capturare: " + ex.getMessage()));
					} finally {
						SwingUtilities.invokeLater(() -> {
							progressLabel.setText("Capturare finalizată.");
							captureBtn.setEnabled(true);
						});
					}
				}).start();
			} else {
				showError("Introdu numele persoanei și numărul de imagini.");
			}
		});


		trainPersonBtn.addActionListener(e -> {
			String name = nameField.getText().trim();
			if (!name.isEmpty()) {
				runCommand(JAVA_CMD + "svm.PersonTrainer " + name);
			} else {
				showError("Introdu numele persoanei pentru antrenare.");
			}
		});

		recognizeBtn.addActionListener(e -> runCommand(JAVA_CMD + "face.LiveRecognizer"));

		editImagesBtn.addActionListener(e -> {
			String name = nameField.getText().trim();
			if (!name.isEmpty()) {
				File folder = new File("data/collected/" + name);
				if (folder.exists()) {
					try {
						Desktop.getDesktop().open(folder);
					} catch (IOException ex) {
						showError("Nu s-a putut deschide folderul: " + ex.getMessage());
					}
				} else {
					showError("Folderul nu există pentru: " + name);
				}
			} else {
				showError("Introdu mai întâi un nume pentru a deschide folderul respectiv.");
			}
		});
	}


	/**
	 * Rulează o comandă externă într-un nou proces folosind Command Prompt.
	 * Este utilizată pentru a porni modulele externe (captură, antrenare, recunoaștere).
	 *
	 * @param command comanda completă care urmează să fie executată
	 */
    private static void runCommand(String command) {
        try {
            Runtime.getRuntime().exec("cmd /c start " + command);
        } catch (Exception ex) {
            showError("Eroare la lansarea comenzii: " + ex.getMessage());
        }
    }

	/**
	 * Afișează un mesaj de eroare utilizatorului într-o fereastră de tip pop-up.
	 *
	 * @param message textul mesajului de afișat
	 */
    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Eroare", JOptionPane.ERROR_MESSAGE);
    }
}

