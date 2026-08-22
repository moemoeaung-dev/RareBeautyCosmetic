package Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.function.Supplier;
import Main.DBConnection;
import Main.LoginForm;

public class HighlighterPage extends Basepage {
	private int customerId = LoginForm.loggedInCustomerId; 
    public HighlighterPage() {
        super("Highlighter Categories"); // Page title
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // MAIN CONTENT PANEL
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Subcategory grid
        JPanel categoryPanel = new JPanel(new GridLayout(0, 2, 40, 30));
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Add highlighter subcategories using inner class
        categoryPanel.add(CreateSubCategory("Liquid Glow Highlighter", "assets/liquidglow.jpg", () -> new LiquidGlowPage()));
        categoryPanel.add(CreateSubCategory("Cream Radiance Highlighter", "assets/creamradiance.jpg", () -> new CreamRadiancePage()));
        categoryPanel.add(CreateSubCategory("Powder Shimmer Highlighter", "assets/powershimmer.jpg", () -> new PowderShimmerPage()));
        categoryPanel.add(CreateSubCategory("Bouncy Illuminator Highlighter", "assets/bounceilluminator.jpg", () -> new BouncyIlluminatorPage()));


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

    // 🔹 Reusable SubCategory Card Creator
    private JPanel CreateSubCategory(String name, String imagePath, Supplier<JFrame> nextPageSupplier) {
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(400, 250));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(240, 170, 200), 2, true));

        JLabel imageLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(360, 160, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText("No Image");
        }
        imageLabel.setBounds(20, 10, 360, 160);
        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                JFrame nextPage = nextPageSupplier.get();
                nextPage.setVisible(true);
            }
        });

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(120, 20, 80));
        nameLabel.setBounds(0, 180, 400, 30);

        panel.add(imageLabel);
        panel.add(nameLabel);
        return panel;
    }

    // =========================
    // 🔹 Inner Classes for SubCategories
    // =========================
    class LiquidGlowPage extends SubCategoryPage {
        public LiquidGlowPage() {
            super("Liquid Glow Highlighter", new Color(255, 240, 220), 13); // categoryId = 7
        }
    }

    class CreamRadiancePage extends SubCategoryPage {
        public CreamRadiancePage() {
            super("Cream Radiance Highlighter", new Color(230, 245, 255), 14); // categoryId = 8
        }
    }

    class PowderShimmerPage extends SubCategoryPage {
        public PowderShimmerPage() {
            super("Powder Shimmer Highlighter", new Color(240, 230, 250), 15); // categoryId = 9
        }
    }

    class BouncyIlluminatorPage extends SubCategoryPage {
        public BouncyIlluminatorPage() {
            super("Bouncy Illuminator Highlighter", new Color(250, 235, 210), 16); // categoryId = 10
        }
    }

    // =========================
    // 🔹 Abstract SubCategory Page
    // =========================
    abstract class SubCategoryPage extends JFrame {
        protected JPanel productPanel;

        public SubCategoryPage(String title, Color bgColor, int categoryId) {
            setTitle(title);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            getContentPane().setBackground(bgColor);
            setLayout(new BorderLayout());

            // Product grid
            productPanel = new JPanel(new GridLayout(2, 3, 20, 20));
            productPanel.setBackground(bgColor);
            productPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

            JScrollPane scrollPane = new JScrollPane(productPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);

            // Back button
            JButton backBtn = new JButton("Back");
            backBtn.addActionListener(e -> {
                dispose();
                new HighlighterPage(); // back to HighlighterPage
            });

            JButton viewCartBtn = new JButton(" View Cart");
            viewCartBtn.setBackground(new Color(255, 105, 180)); // pastel pink
            viewCartBtn.setForeground(Color.WHITE);
            viewCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            viewCartBtn.setFocusPainted(false);
            viewCartBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            viewCartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add hover effect
            viewCartBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    viewCartBtn.setBackground(new Color(255, 75, 150)); // slightly darker on hover
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    viewCartBtn.setBackground(new Color(255, 105, 180));
                }
            });

            viewCartBtn.addActionListener(e -> new ViewCartPage(customerId));


            JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            topRightPanel.setBackground(new Color(255, 245, 230));
            topRightPanel.add(viewCartBtn);
            add(topRightPanel, BorderLayout.NORTH);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(bgColor);
            bottomPanel.add(backBtn);
            add(bottomPanel, BorderLayout.SOUTH);

            loadProducts(categoryId);
            setVisible(true);
        }

        // 🔹 Load Products by Category ID
     // SubLipstickPage class ထဲက loadProducts method ကိုလည်း ပြင်ဆင်ရန်
        private void loadProducts(int categoryId) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT pid, pname, quantity, price, description, image FROM product WHERE categoryId = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int pid = rs.getInt("pid");
                    String name = rs.getString("pname");
                    int stock = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    String desc = rs.getString("description");
                    Blob imageBlob = rs.getBlob("image"); // Blob အဖြစ်ယူမယ်
                    
                    cardView card = new cardView(pid, name, stock, price, desc, imageBlob);
                    productPanel.add(card);
                }

                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB Load Error: " + e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        new HighlighterPage().setVisible(true);
    }
}
