package Customer;

import javax.swing.*;

import Customer.LipstickPage.GlossyLipstickPage;
import Customer.LipstickPage.LipCreamPage;
import Customer.LipstickPage.MatteLipstickPage;
import Customer.LipstickPage.SubLipstickPage;
import Customer.LipstickPage.TintedLipOilPage;
import Main.DBConnection;
import Main.LoginForm;

import java.awt.*;
import java.awt.event.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Supplier;

public class Shop extends Basepage {
	private int customerId = LoginForm.loggedInCustomerId; 
    public Shop() {
        super("Shop");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // MAIN CONTENT PANEL
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Panel for lipstick subcategories (grid layout 2 columns)
        JPanel categoryPanel = new JPanel(new GridLayout(2, 2, 3, 2));
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        // Add lipstick types (inner classes)
        categoryPanel.add(createSubCategory("Lipstick", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\images\\IMG_0675.PNG", () -> new LipstickPage()));
        categoryPanel.add(createSubCategory("MakeUpBase", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\resources\\MakeUpBase\\MUBaseCV.PNG", () -> new MakeupbasePage()));
        categoryPanel.add(createSubCategory("Blushes", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\resources\\Blush\\BlushCV.PNG", () -> new BlushPage()));
        categoryPanel.add(createSubCategory("Highlighters", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\resources\\Highlighters\\HLightCV.PNG", () -> new HighlighterPage()));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(categoryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add everything into BasePage’s content area
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // Create subcategory small button panels (photo + label + hover effect)
    private JPanel createSubCategory(String name, String imagePath, Supplier<JFrame> nextPageSupplier) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(120, 120));
        panel.setBackground(Color.PINK);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 150, 180), 2, true));

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(330, 330, Image.SCALE_SMOOTH);//Image size
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText("No Image");
        }

        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                JFrame nextPage = nextPageSupplier.get();
                nextPage.setVisible(true);
            }
        });

        // Label
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(new Color(120, 20, 80));

        // Hover effect
        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 3, true));
            }
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createLineBorder(new Color(220, 150, 180), 2, true));
            }
        });

        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        new Shop();
    }
}
