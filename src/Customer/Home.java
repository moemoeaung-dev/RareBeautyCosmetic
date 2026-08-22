package Customer;

import javax.swing.*;
import java.awt.*;
public class Home extends Basepage {
	
	private Image backgroundImage;
    public Home() {
        super("Rare Beauty");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 15, 0); // spacing between texts

        // --- First fancy text ---
        JLabel fancyText1 = new JLabel("<html><center>"
                + "<h1 style='font-family: Segoe Script; font-size:48px; color:#D60072;'>Be Rare, Be Beautiful</h1>"
                + "<p style='font-family: Georgia; font-size:20px; color:#FFD6F0;'>"
                + "Celebrate individuality and let your inner light shine ✨</p>"
                + "</center></html>");
        gbc.gridy = 0;
        contentPanel.add(fancyText1, gbc);

        // --- Second fancy text ---
        JLabel fancyText2 = new JLabel("<html><center>"
                + "<h2 style='font-family: Segoe Script; font-size:36px; color:#D60072;'>Discover Our Collection</h2>"
                + "<p style='font-family: Georgia; font-size:18px; color:#FFF0F5;'>"
                + "Explore unique products that suit your style ✨</p>"
                + "</center></html>");
        gbc.gridy = 1;
        contentPanel.add(fancyText2, gbc);

        // --- Third fancy text ---
        JLabel fancyText3 = new JLabel("<html><center>"
                + "<h2 style='font-family: Segoe Script; font-size:36px; color:#D60072;'>Join Our Community</h2>"
                + "<p style='font-family: Georgia; font-size:12px; color:#FFFFFF;'>"
                + "Connect, share, and celebrate beauty together 💖</p>"
                + "</center></html>");
        gbc.gridy = 2;
        contentPanel.add(fancyText3, gbc);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        new Home().setVisible(true);
    }
}
