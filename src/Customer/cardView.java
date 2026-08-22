package Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.*;
import javax.imageio.ImageIO;
import Main.DBConnection;
import Main.LoginForm;
import java.util.ArrayList;
import java.util.List;

public class cardView extends JPanel {
	
    private int pid;
    private String name;
    private int stock;
    private double price;
    private String description;
    private Blob imageBlob;
    private int customerId;
    private ImageIcon productImage;

    public cardView(int pid, String name, int stock, double price, String description, Blob imageBlob) {
    	
        this.pid = pid;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.imageBlob = imageBlob;
        this.customerId = LoginForm.loggedInCustomerId;
        
        // Pre-load the image when creating the card
        this.productImage = loadProductImage(240, 180);

        initializeUI();
    }

    private void initializeUI() {
    	
        setLayout(new BorderLayout());
        setBackground(new Color(249, 187, 191));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(249, 187, 191), 1),
                BorderFactory.createEmptyBorder(10, 5, 10, 10)
        ));
        setPreferredSize(new Dimension(260, 380));
        setMaximumSize(new Dimension(260, 380));

        add(createProductCard(), BorderLayout.CENTER);
    }

    // ==================== STATIC METHODS FOR DATABASE OPERATIONS ====================

    public static List<cardView> getAllProducts() {
        List<cardView> products = new ArrayList<>();
        
        String query = "SELECT p.pid, p.pname, p.description, p.price, p.quantity, p.image " +
                      "FROM product p " +
                      "WHERE p.quantity > 0 " +
                      "ORDER BY p.pname";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int pid = rs.getInt("pid");
                String name = rs.getString("pname");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("quantity");
                Blob imageBlob = rs.getBlob("image");
                
                cardView productCard = new cardView(pid, name, stock, price, description, imageBlob);
                products.add(productCard);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading products: " + e.getMessage());
        }
        
        return products;
    }

    // ==================== INSTANCE METHODS ====================

    private JPanel createProductCard() {
    	
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.PINK);
        
        // ---------- IMAGE ----------
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(240, 180));
        
        if (productImage != null) {
            imageLabel.setIcon(productImage);
        } else {
            imageLabel.setIcon(createPlaceholderImage(240, 180));
        }

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.PINK);
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        card.add(imagePanel);

        // ---------- CONTENT ----------
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(249, 187, 191));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLabel.setForeground(new Color(150, 0, 80));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(nameLabel);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setForeground(new Color(150, 0, 80));
        descArea.setMaximumSize(new Dimension(220, 50));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setBorder(null);
        descArea.setHighlighter(null);
        

        content.add(descArea);

        JPanel priceStock = new JPanel();
        priceStock.setLayout(new BoxLayout(priceStock, BoxLayout.Y_AXIS));
        priceStock.setBackground(new Color(249, 187, 191));
        priceStock.setMaximumSize(new Dimension(220, 220));

        JLabel priceLabel = new JLabel("$" + String.format("%,.2f", price));
        priceLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        priceLabel.setForeground(new Color(150, 0, 80));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stockLabel = new JLabel(stock + " in stock");
        stockLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        stockLabel.setForeground(stock > 10 ? new Color(150, 0, 80) : new Color(220, 38, 38));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        priceStock.add(Box.createVerticalGlue());
        priceStock.add(priceLabel);
        priceStock.add(Box.createVerticalStrut(5));
        priceStock.add(stockLabel);
        priceStock.add(Box.createVerticalGlue());

        content.add(Box.createVerticalStrut(5));
        content.add(priceStock);
        card.add(content);

        // ---------- BUTTON ----------
        JButton cartBtn = new JButton("Add to Cart");
        cartBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        cartBtn.setBackground(Color.PINK);
        cartBtn.setForeground(new Color(150, 0, 80));
        cartBtn.setFocusPainted(false);
        cartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cartBtn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        cartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartBtn.addActionListener(e -> showQuantityDialog());
        
        cartBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cartBtn.setBackground(new Color(255, 75, 150));
                cartBtn.setForeground(new Color(150, 0, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cartBtn.setBackground(Color.PINK);
                cartBtn.setForeground(new Color(150, 0, 80));
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(249, 187, 191));
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(cartBtn);
        btnPanel.add(Box.createHorizontalGlue());

        card.add(Box.createVerticalGlue());
        card.add(btnPanel);

        return card;
    }
    
    private ImageIcon loadProductImage(int width, int height) {
        try {
            if (imageBlob != null) {
                long blobLength = imageBlob.length();
                if (blobLength > 0) {
                    byte[] imageData = imageBlob.getBytes(1, (int) blobLength);
                    InputStream in = new ByteArrayInputStream(imageData);
                    BufferedImage originalImage = ImageIO.read(in);
                    
                    if (originalImage != null) {
                        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading product image for product ID " + pid + ": " + e.getMessage());
            e.printStackTrace();
        }
        return createPlaceholderImage(width, height);
    }

    private ImageIcon createPlaceholderImage(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = placeholder.createGraphics();
        GradientPaint gradient = new GradientPaint(0, 0, new Color(249, 187, 191), width, height, new Color(200, 200, 200));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);
        g2.setColor(new Color(150, 150, 150));
        g2.fillRect(width/4, height/4, width/2, height/2);
        g2.setColor(new Color(249, 187, 191));
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        String text = "No Image";
        int textWidth = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (width - textWidth)/2, height/2 + 5);
        g2.dispose();
        return new ImageIcon(placeholder);
    }

    private void showQuantityDialog() {
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, stock, 1));
        JPanel panel = new JPanel();
        panel.add(new JLabel("Quantity:"));
        panel.add(qtySpinner);
        int option = JOptionPane.showConfirmDialog(this, panel,
                "Select quantity for " + name, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int qty = (Integer) qtySpinner.getValue();
            addToCart(pid, customerId, qty);
        }
    }

    private void addToCart(int pid, int customerId, int qty) {
        if (customerId <= 0) {
            JOptionPane.showMessageDialog(this, "Please login first!");
            return;
        }
        if (qty > stock) {
            JOptionPane.showMessageDialog(this, "Not enough stock available!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT cartId, quantity FROM cart_item WHERE pid=? AND cid=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, pid);
            checkPs.setInt(2, customerId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int existingQty = rs.getInt("quantity");
                int cartId = rs.getInt("cartId");
                String updateSql = "UPDATE cart_item SET quantity=? WHERE cartId=?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, existingQty + qty);
                updatePs.setInt(2, cartId);
                updatePs.executeUpdate();
                updatePs.close();
                showSuccess("Updated " + name + " to " + (existingQty + qty) + " qty in cart!");
            } else {
                String insertSql = "INSERT INTO cart_item (pid, cid, quantity) VALUES (?, ?, ?)";
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                insertPs.setInt(1, pid);
                insertPs.setInt(2, customerId);
                insertPs.setInt(3, qty);
                insertPs.executeUpdate();
                insertPs.close();
                showSuccess("Added " + name + " (Qty: " + qty + ") to cart!");
            }
            rs.close();
            checkPs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding to cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== UTILITY METHODS ====================
    public int getPid() { return pid; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public ImageIcon getProductImage() { return productImage; }

    // ==================== NEW: GRID PANEL ====================
    public static class ProductListPanel extends JPanel {
        public ProductListPanel(List<cardView> products) {
        	
            setLayout(new BorderLayout());
            JPanel cardsContainer = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 columns, unlimited rows
            cardsContainer.setBackground(new Color(249, 187, 191));
            for (cardView productCard : products) {
                cardsContainer.add(productCard);
            }
            JScrollPane scrollPane = new JScrollPane(cardsContainer);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);
        }
    }

    // ==================== TEST MAIN ====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Product Grid - 4 Columns with Scroll");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);

            List<cardView> products = cardView.getAllProducts();
            ProductListPanel productListPanel = new ProductListPanel(products);
            frame.add(productListPanel);

            frame.setVisible(true);
        });
    }
}