package Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import Main.DBConnection;
import java.util.List;

public class ViewCartPage extends Basepage {
    private JLabel subtotalLabel;
    private int customerId;
    private List<Cart.Item> cartItems;
    private JPanel productPanel;
   

    public ViewCartPage(int customerId) {
    	super("Cart");
    	setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.customerId = customerId;
        this.cartItems = Cart.getCartItems(customerId);

        setTitle("Your Cart");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(249, 187, 191));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(249, 187, 191));
        
        JLabel cartLabel = new JLabel("Your Shopping Cart");
        cartLabel.setFont(new Font("Segoe Script", Font.BOLD, 26));
        cartLabel.setForeground(new Color(150, 0, 80));
        cartLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(cartLabel, BorderLayout.WEST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Product list panel
        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.PINK);

        // Initialize subtotalLabel first
        subtotalLabel = new JLabel("$0.00");
        subtotalLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        subtotalLabel.setForeground(new Color(150, 0, 80));
        
        refreshCartItems();

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.PINK);
     // Scroll bars ကို လုံးဝမပေါ်အောင် set လုပ်မယ်
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        updateSubtotal();
        setVisible(true);
    }

    private void refreshCartItems() {
        productPanel.removeAll();
        cartItems = Cart.getCartItems(customerId);
        
        if (cartItems.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(Color.PINK);
            emptyPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
            
            JLabel emptyLabel = new JLabel("Your cart is empty", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
            emptyLabel.setForeground(new Color(150, 0, 80));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            
            productPanel.add(emptyPanel);
        } else {
            for (Cart.Item item : cartItems) {
                productPanel.add(createCartItemRow(item));
            }
        }
        
        productPanel.revalidate();
        productPanel.repaint();
        updateSubtotal();
    }

    private JPanel createCartItemRow(Cart.Item item) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.PINK),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        row.setBackground(Color.PINK);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Image
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(70, 70));
        
        if (item.imageData != null && item.imageData.length > 0) {
            try {
                ImageIcon icon = new ImageIcon(item.imageData);
                Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                imgLabel.setIcon(null);
            }
        }
        
        if (imgLabel.getIcon() == null) {
            imgLabel.setText("No Image");
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setForeground(new Color(150, 150, 150));
        }
        row.add(imgLabel, gbc);

        // Product info
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        row.add(nameLabel, gbc);

        gbc.gridy = 1;
        JLabel priceLabel = new JLabel("$" + String.format("%.2f", item.price) + " each");
        priceLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        priceLabel.setForeground(new Color(100, 100, 100));
        row.add(priceLabel, gbc);

        // Quantity controls - Using Spinner
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Create spinner with current quantity
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(item.quantity, 1, 100, 1));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) quantitySpinner.getEditor();
        editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        editor.getTextField().setEditable(false);
        
        quantitySpinner.setPreferredSize(new Dimension(80, 30));
        quantitySpinner.addChangeListener(e -> {
            int newQuantity = (Integer) quantitySpinner.getValue();
            if (newQuantity != item.quantity) {
                updateQuantity(item, newQuantity);
            }
        });
        row.add(quantitySpinner, gbc);

        // Total price
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        JLabel totalLabel = new JLabel("$" + String.format("%.2f", item.getTotal()));
        totalLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        totalLabel.setForeground(new Color(150, 0, 80));
        row.add(totalLabel, gbc);

        // Remove button - Fixed with proper action
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        JButton removeBtn = new JButton("\uD83D\uDDD1");
        removeBtn.setFont(new Font("monospace", Font.BOLD, 32));
        removeBtn.setBorderPainted(false);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.setToolTipText("Remove from cart");
        
        // Store the actual product ID from the item object
        removeBtn.putClientProperty("productId", item.productId);
        removeBtn.putClientProperty("productName", item.name);
        
        removeBtn.addActionListener(e -> {
            int productId = (Integer) removeBtn.getClientProperty("productId");
            String productName = (String) removeBtn.getClientProperty("productName");
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + productName + " from cart?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (removeItemFromCart(productId)) {
                    refreshCartItems();
                    JOptionPane.showMessageDialog(this, "Item removed from cart");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove item");
                }
            }
        });
        
        // Add hover effect for remove button
        removeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeBtn.setForeground(new Color(220, 53, 69));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeBtn.setForeground(new Color(150, 0, 80));
            }
        });
        
        row.add(removeBtn, gbc);

        return row;
    }

    private void updateQuantity(Cart.Item item, int newQuantity) {
        if (Cart.updateCartQuantity(customerId, item.productId, newQuantity)) {
            item.quantity = newQuantity;
            refreshCartItems();
           // JOptionPane.showMessageDialog(this, "Quantity updated successfully");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update quantity");
            // Reset the spinner to the original value
            refreshCartItems();
        }
    }

    private boolean removeItemFromCart(int productId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM cart_item WHERE cid = ? AND pid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            return false;
        }
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        bottomPanel.setBackground(Color.PINK);

        // Back button
        JButton backBtn = new JButton("Back");
        backBtn.setBackground(Color.PINK);
        backBtn.setForeground(new Color(150, 0, 80));
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 18));
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(180, 40));
        backBtn.addActionListener(e -> {
            dispose();
            new LipstickPage();
        });
        bottomPanel.add(backBtn, BorderLayout.WEST);
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change appearance on hover (e.g., background color or text color)
                backBtn.setForeground(new Color(200, 0, 100)); // Lighter or different color
                backBtn.setFont(new Font("Monospaced", Font.BOLD, 20)); // Slightly bigger font, optional
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert appearance when not hovering
                backBtn.setForeground(new Color(150, 0, 80));
                backBtn.setFont(new Font("Monospaced", Font.BOLD, 18)); // Revert to original
            }
        });

        // Subtotal panel
        JPanel subtotalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        subtotalPanel.setBackground(Color.PINK);
        
        JLabel subtotalText = new JLabel("Subtotal: ");
        subtotalText.setFont(new Font("Monospaced", Font.BOLD, 18));
        subtotalText.setForeground(new Color(150, 0, 80));
        
        subtotalPanel.add(subtotalText);
        subtotalPanel.add(subtotalLabel);
        bottomPanel.add(subtotalPanel, BorderLayout.CENTER);

        // Checkout button
        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setBackground(Color.PINK);
        checkoutBtn.setForeground(new Color(150, 0, 80));
        checkoutBtn.setFont(new Font("Monospaced", Font.BOLD, 18));
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutBtn.setPreferredSize(new Dimension(180, 40));
        checkoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change appearance on hover (e.g., background color or text color)
                checkoutBtn.setForeground(new Color(200, 0, 100)); // Lighter or different color
                checkoutBtn.setFont(new Font("Monospaced", Font.BOLD, 20)); // Slightly bigger font, optional
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert appearance when not hovering
                checkoutBtn.setForeground(new Color(150, 0, 80));
                checkoutBtn.setFont(new Font("Monospaced", Font.BOLD, 18)); // Revert to original
            }
        });

        checkoutBtn.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!");
            } else {
                dispose();
                new checkout(customerId);
            }
        });
        bottomPanel.add(checkoutBtn, BorderLayout.EAST);

        return bottomPanel;
    }

    private void updateSubtotal() {
        if (subtotalLabel == null) {
            return;
        }
        
        double subtotal = 0;
        for (Cart.Item item : cartItems) {
            subtotal += item.getTotal();
        }
        subtotalLabel.setText("$" + String.format("%.2f", subtotal));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int demoCustomerId = 6;
            new ViewCartPage(demoCustomerId);
        });
    }
}