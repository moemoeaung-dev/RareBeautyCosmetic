package Customer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.function.Supplier;
import Main.DBConnection;
import Main.LoginForm;

public class LipstickPage extends Basepage {
    private int customerId = LoginForm.loggedInCustomerId; 
    
    public LipstickPage() {
        super("Lipstick Categories");
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
        categoryPanel.add(createSubCategory("Matte", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\images/Matte.png", () -> new MatteLipstickPage()));
        categoryPanel.add(createSubCategory("Glossy", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\images/Gl.jpg", () -> new GlossyLipstickPage()));
        categoryPanel.add(createSubCategory("Lip Cream", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\images/Cree.png", () -> new LipCreamPage()));
        categoryPanel.add(createSubCategory("Tinted Lip Oil", "C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\images/LLo.png", () -> new TintedLipOilPage()));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(categoryPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add everything into BasePage's content area
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

    // ---------------- INNER CLASSES ---------------- //

    class MatteLipstickPage extends SubLipstickPage {
        public MatteLipstickPage() {
            super("Matte Lipsticks", new Color(255, 240, 240), 1);
        }
    }

    class GlossyLipstickPage extends SubLipstickPage {
        public GlossyLipstickPage() {
            super("Glossy Lipsticks", new Color(240, 255, 240), 2);
        }
    }

    class LipCreamPage extends SubLipstickPage {
        public LipCreamPage() {
            super("Lip Creams", new Color(240, 240, 255), 3);
        }
    }

    class TintedLipOilPage extends SubLipstickPage {
        public TintedLipOilPage() {
            super("Tinted Lip Oils", new Color(255, 250, 230), 4);
        }
    }

    // Generic SubLipstickPage (for reuse)
    abstract class SubLipstickPage extends JFrame {
        protected JPanel productPanel;

        public SubLipstickPage(String title, Color bgColor, int categoryId) {
            super(title);
            setSize(1000, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setResizable(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            
            // Create a main panel with BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(bgColor);
            
            // Create button panel for top
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(new Color(255, 182, 193, 230));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Create and add Back button
            JButton backBtn = createButton("Back");
            backBtn.addActionListener(e -> {
                dispose();
                new LipstickPage().setVisible(true);
            });
            
            // Create and add Cart button
            JButton viewCartBtn = createButton("Cart");
            viewCartBtn.addActionListener(e -> {
                dispose();
                new ViewCartPage(customerId).setVisible(true);
            });
            
            buttonPanel.add(viewCartBtn);
            buttonPanel.add(backBtn);
            
            // Add button panel to the top
            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            
            // Product grid - Use GridBagLayout for 4 columns
            productPanel = new JPanel(new GridLayout(0, 4, 20, 20));
           productPanel.setBackground(new Color(255, 217, 234));
           productPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
          //productPanel.setBackground(new Color(249, 187, 191));
            
        // Scroll pane for products
           JScrollPane scrollPane = new JScrollPane(productPanel);
           scrollPane.setBorder(null);
           scrollPane.getViewport().setBackground(Color.PINK);
           scrollPane.getVerticalScrollBar().setUnitIncrement(16);

           // Scroll bar တွေကို auto-hide လုပ်ခြင်း
           JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
           verticalScrollBar.setOpaque(false);
           verticalScrollBar.setPreferredSize(new Dimension(0, 0)); // Scroll bar size ကို သုညလုပ်ခြင်း

           // Custom scrollbar UI
           verticalScrollBar.setUI(new BasicScrollBarUI() {
               @Override
               protected void configureScrollBarColors() {
                   this.thumbColor = new Color(240, 132, 144);
                   this.trackColor = new Color(255, 217, 234);
               }
               
               @Override
               protected JButton createDecreaseButton(int orientation) {
                   return createInvisibleButton();
               }
               
               @Override
               protected JButton createIncreaseButton(int orientation) {
                   return createInvisibleButton();
               }
               
               private JButton createInvisibleButton() {
                   JButton button = new JButton();
                   button.setPreferredSize(new Dimension(0, 0));
                   button.setMinimumSize(new Dimension(0, 0));
                   button.setMaximumSize(new Dimension(0, 0));
                   return button;
               }
           });

         

           // Scroll bar အတွက်လည်း mouse listener ထည့်ခြင်း
           verticalScrollBar.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseExited(MouseEvent e) {
                   // Delay နည်းနည်းပေးပြီးမှ စစ်ဆေးခြင်း
                   Timer timer = new Timer(500, event -> {
                       Point mousePos = MouseInfo.getPointerInfo().getLocation();
                       SwingUtilities.convertPointFromScreen(mousePos, scrollPane);
                       if (!verticalScrollBar.getBounds().contains(mousePos) &&
                           !scrollPane.getViewport().getBounds().contains(mousePos)) {
                           verticalScrollBar.setPreferredSize(new Dimension(0, Integer.MAX_VALUE));
                           scrollPane.revalidate();
                       }
                   });
                   timer.setRepeats(false);
                   timer.start();
               }
           });

           // Horizontal scroll bar ကို လုံးဝဖျောက်ခြင်း
           scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // Add scroll pane to center
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // Add main panel to frame
            add(mainPanel);
            
            // Load products
            loadProducts(categoryId);
        }
        
        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setBackground(new Color(255, 182, 193, 230));
            button.setForeground(new Color(150, 0, 80));
            button.setFont(new Font("Monospaced", Font.BOLD, 18));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add hover effect
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    button.setBackground(new Color(255, 75, 150));
                }
                public void mouseExited(MouseEvent evt) {
                    button.setBackground(new Color(255, 182, 193, 230));
                }
            });
            
            return button;
        }

        private void loadProducts(int categoryId) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT pid, pname, quantity, price, description, image FROM product WHERE categoryId = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryId);
                ResultSet rs = ps.executeQuery();

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                while (rs.next()) {
                    int pid = rs.getInt("pid");
                    String name = rs.getString("pname");
                    int stock = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    String desc = rs.getString("description");
                    Blob imageBlob = rs.getBlob("image");
                    
                    // Create cardView instance
                    cardView card = new cardView(pid, name, stock, price, desc, imageBlob);
                    
                    // Add to product panel with GridBagConstraints
                    productPanel.add(card, gbc);
                    
                    // Update grid position for 4 columns
                    gbc.gridx++;
                    if (gbc.gridx >= 4) { // 4 columns
                        gbc.gridx = 0;
                        gbc.gridy++;
                    }
                }

                // Add filler to push everything to the top
                gbc.gridx = 0;
                gbc.gridy++;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                productPanel.add(Box.createGlue(), gbc);

                rs.close();
                ps.close();
                
                // Refresh the UI
                productPanel.revalidate();
                productPanel.repaint();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "DB Load Error: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LipstickPage().setVisible(true);
        });
    }
}