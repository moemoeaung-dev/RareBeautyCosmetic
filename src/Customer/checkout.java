package Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Main.LoginForm;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class checkout extends JFrame {
    public int customerId = LoginForm.loggedInCustomerId;

    public checkout(int customerId) {
        this.customerId = customerId;
        double totalPrice = Cart.getCartTotal(customerId);
        
        setTitle("Checkout Receipt");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel receiptPanel = new JPanel();
        receiptPanel.setLayout(new BorderLayout(10, 10));
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top panel - Total & Date
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(Color.WHITE);

        JLabel totalLabel = new JLabel("TOTAL: $" + String.format("%.2f", totalPrice));
        totalLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        totalLabel.setForeground(new Color(150, 0, 80));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        JLabel dateLabel = new JLabel("Date: " + dateTime);
        dateLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(150, 0, 80));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(totalLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        topPanel.add(dateLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        receiptPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel - Customer info
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameField.setForeground(new Color(150, 0, 80));
        JTextField phoneField = new JTextField();
        phoneField.setFont(new Font("Monospaced", Font.BOLD, 16));
        phoneField.setForeground(new Color(150, 0, 80));
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        addressArea.setForeground(new Color(150, 0, 80));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
     
        centerPanel.add(labeledField("Name:", nameField));
       // centerPanel.setForeground(new Color(150, 0, 80));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(labeledField("Phone Number:", phoneField));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        centerPanel.add(labeledField("Address:", addressScroll));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        receiptPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(Color.PINK);
        backBtn.setFont(new Font("Monospaced", Font.BOLD, 16));
        backBtn.setForeground(new Color(150, 0, 80));
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change appearance on hover (e.g., background color or text color)
                backBtn.setForeground(new Color(200, 0, 100)); // Lighter or different color
                backBtn.setFont(new Font("Monospaced", Font.BOLD, 16)); // Slightly bigger font, optional
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert appearance when not hovering
                backBtn.setForeground(new Color(150, 0, 80));
                backBtn.setFont(new Font("Monospaced", Font.BOLD, 16)); // Revert to original
            }
        });
        backBtn.addActionListener(e -> {
            dispose();
            new ViewCartPage(customerId);
        });

        JButton orderButton = new JButton("Place Order");
        orderButton.setBackground(Color.PINK);
        orderButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        orderButton.setForeground(new Color(150, 0, 80));
        orderButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change appearance on hover (e.g., background color or text color)
                orderButton.setForeground(new Color(200, 0, 100)); // Lighter or different color
                orderButton.setFont(new Font("Monospaced", Font.BOLD, 16)); // Slightly bigger font, optional
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert appearance when not hovering
                orderButton.setForeground(new Color(150, 0, 80));
                orderButton.setFont(new Font("Monospaced", Font.BOLD, 16)); // Revert to original
            }
        });
        orderButton.addActionListener(e -> processOrder(nameField.getText().trim(),
                                                         phoneField.getText().trim(),
                                                         addressArea.getText().trim(),
                                                         totalPrice,
                                                         dateTime));

        bottomPanel.add(backBtn);
        bottomPanel.add(orderButton);
        receiptPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(receiptPanel);
        setVisible(true);
    }

    private void processOrder(String name, String phone, String address, double totalPrice, String dateTime) {
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psOrderItem = null;
        PreparedStatement psCart = null;
        PreparedStatement psUpdateProduct = null;
        ResultSet rsCart = null;

        try {
            conn = Main.DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert into order table
            String insertOrder = "INSERT INTO `order_confirm` (cid, date, totalamount, phoneno, address) VALUES (?, ?, ?, ?, ?)";
            psOrder = conn.prepareStatement(insertOrder, PreparedStatement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, customerId);
            psOrder.setString(2, dateTime);
            psOrder.setDouble(3, totalPrice);
            psOrder.setString(4, phone);
            psOrder.setString(5, address);
            psOrder.executeUpdate();

            // 2. Get generated orderId
            ResultSet rsOrderId = psOrder.getGeneratedKeys();
            int orderId = 0;
            if (rsOrderId.next()) {
                orderId = rsOrderId.getInt(1);
            }
            rsOrderId.close();

            // 3. Get cart items
            String cartQuery = "SELECT ci.pid, ci.quantity, p.price, p.quantity as stock_quantity, (ci.quantity * p.price) AS subtotal " +
                               "FROM cart_item ci " +
                               "JOIN product p ON ci.pid = p.pid " +
                               "WHERE ci.cid = ?";
            psCart = conn.prepareStatement(cartQuery);
            psCart.setInt(1, customerId);
            rsCart = psCart.executeQuery();

            // 4. Insert into order_item and update product quantities
            String insertOrderItem = "INSERT INTO order_item (oid, pid, quantity, unitprice, subtotal) VALUES (?, ?, ?, ?, ?)";
            psOrderItem = conn.prepareStatement(insertOrderItem);
            
            String updateProductQuantity = "UPDATE product SET quantity = quantity - ? WHERE pid = ? AND quantity >= ?";
            psUpdateProduct = conn.prepareStatement(updateProductQuantity);

            while (rsCart.next()) {
                int productId = rsCart.getInt("pid");
                int quantity = rsCart.getInt("quantity");
                double unitPrice = rsCart.getDouble("price");
                double subtotal = rsCart.getDouble("subtotal");
                int stockQuantity = rsCart.getInt("stock_quantity");

                // Check if enough stock is available
                if (stockQuantity < quantity) {
                    throw new SQLException("Not enough stock for product ID: " + productId + 
                                         ". Available: " + stockQuantity + ", Requested: " + quantity);
                }

                // Insert order item
                psOrderItem.setInt(1, orderId);
                psOrderItem.setInt(2, productId);
                psOrderItem.setInt(3, quantity);
                psOrderItem.setDouble(4, unitPrice);
                psOrderItem.setDouble(5, subtotal);
                psOrderItem.addBatch();

                // Update product quantity
                psUpdateProduct.setInt(1, quantity);
                psUpdateProduct.setInt(2, productId);
                psUpdateProduct.setInt(3, quantity);
                psUpdateProduct.addBatch();
            }
            
            // Execute batches
            psOrderItem.executeBatch();
            psUpdateProduct.executeBatch();

            // 5. Delete cart items
            String deleteCart = "DELETE FROM cart_item WHERE cid = ?";
            PreparedStatement psDeleteCart = conn.prepareStatement(deleteCart);
            psDeleteCart.setInt(1, customerId);
            psDeleteCart.executeUpdate();
            psDeleteCart.close();

            conn.commit();

            // Success message
            JOptionPane.showMessageDialog(this,
                    "✅ Order Placed Successfully!\n\n" +
                    "Order ID: " + orderId + "\n" +
                    "Total: $" + String.format("%.2f", totalPrice) + "\n" +
                    "Thank you for your purchase!");

            dispose();
            new Home().setVisible(true); // Return to home page

        } catch (Exception ex) {
            try { 
                if (conn != null) conn.rollback(); 
            } catch (Exception roll) { 
                roll.printStackTrace(); 
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Order failed: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close all resources
            try { if (rsCart != null) rsCart.close(); } catch (Exception ignored) {}
            try { if (psOrder != null) psOrder.close(); } catch (Exception ignored) {}
            try { if (psOrderItem != null) psOrderItem.close(); } catch (Exception ignored) {}
            try { if (psCart != null) psCart.close(); } catch (Exception ignored) {}
            try { if (psUpdateProduct != null) psUpdateProduct.close(); } catch (Exception ignored) {}
            try { if (conn != null) { 
                conn.setAutoCommit(true); 
                conn.close(); 
            }} catch (Exception ignored) {}
        }
    }

    private JPanel labeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.PINK);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Monospaced", Font.BOLD, 14));
        label.setForeground(new Color(160, 0, 80));
        
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createLineBorder(Color.PINK));
        }
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        
            new checkout(LoginForm.loggedInCustomerId);
        });
    }
}