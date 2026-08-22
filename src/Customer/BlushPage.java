package Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.function.Supplier;

import Main.DBConnection;
import Main.LoginForm;

public class BlushPage extends JFrame {
	private int customerId = LoginForm.loggedInCustomerId; 
    public BlushPage() {
        setTitle("Rare Beauty - Blush Categories");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Background
        JLabel bgLabel = new JLabel(new ImageIcon("assets/shop_bg.jpg"));
        setContentPane(bgLabel);
        bgLabel.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Select Your Blush Type", SwingConstants.CENTER);
        title.setFont(new Font("Segoe Script", Font.BOLD, 34));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        bgLabel.add(title, BorderLayout.NORTH);

        // Subcategory panel
        JPanel categoryPanel = new JPanel(new GridLayout(0, 2, 40, 30));
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        // Add subcategories (inner classes)
        categoryPanel.add(createSubCategory("Soft Pinch Liquid Blush", "assets/liquidblush.jpg", () -> new LiquidBlushPage()));
        categoryPanel.add(createSubCategory("Stay Vulnerable Cream Blush", "assets/creamblush.jpg", () -> new CreamBlushPage()));
        categoryPanel.add(createSubCategory("Soft Pinch Luminous Powder Blush", "assets/powderblush.jpg", () -> new LuminousBlushPage()));
        categoryPanel.add(createSubCategory("Soft Pinch Matte Bouncy Blush", "assets/matteblush.jpg", () -> new MatteBlushPage()));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(categoryPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        bgLabel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JButton backButton = new JButton("Back to Shop");
        backButton.addActionListener(e -> {
            dispose();
            new Shop();
        });
        bottomPanel.add(backButton);

        JButton homeButton = new JButton("Back to Home");
        homeButton.addActionListener(e -> {
            dispose();
            new Home();
        });
        bottomPanel.add(homeButton);

        bgLabel.add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createSubCategory(String name, String imagePath, Supplier<JFrame> nextPageSupplier) {
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
            public void mouseClicked(MouseEvent e) {
                dispose();
                JFrame nextPage = nextPageSupplier.get();
                nextPage.setVisible(true);
            }
        });

        JLabel nameLabel = new JLabel(name + " ", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(120, 20, 80));
        nameLabel.setBounds(0, 180, 400, 30);

        panel.add(imageLabel);
        panel.add(nameLabel);
        return panel;
    }

    // ---------------- INNER CLASSES ---------------- //

    class LiquidBlushPage extends SubCategoryPage {
        public LiquidBlushPage() {
            super("Soft Pinch Liquid Blush", new Color(255, 240, 220), 9);
        }
    }

    class CreamBlushPage extends SubCategoryPage {
        public CreamBlushPage() {
            super("Stay Vulnerable Cream Blush", new Color(230, 245, 255), 10);
        }
    }

    class LuminousBlushPage extends SubCategoryPage {
        public LuminousBlushPage() {
            super("Soft Pinch Luminous Powder Blush", new Color(240, 230, 250), 11);
        }
    }

    class MatteBlushPage extends SubCategoryPage {
        public MatteBlushPage() {
            super("Soft Pinch Matte Bouncy Blush", new Color(250, 235, 210), 12);
        }
    }

    // Generic SubCategoryPage class
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
                new BlushPage();
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

     // SubLipstickPage class ထဲက loadProducts method ကိုလည်း ပြင်ဆင်ရန်
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
        new BlushPage();
    }
}

