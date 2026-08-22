package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AdminViewOrdersPage extends JFrame {
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public AdminViewOrdersPage() {
        setTitle("Admin - View All Orders");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel headerLabel = new JLabel("All Customer Orders", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(60, 60, 60));
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.add(headerLabel, BorderLayout.WEST);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadOrdersData());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup - Removed Number column, Added Unit Price column
        String[] columns = {
            "Order ID", "Customer", "Product", 
            "Unit Price", "Quantity", "Order Date", 
            "Phone", "Address", "Total Amount"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ordersTable.setRowHeight(40);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ordersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // Set column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Product
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Unit Price
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Quantity
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Order Date
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Phone
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Address
        ordersTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Total Amount

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            dispose();
            new admindashboard().setVisible(true);
        });

        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadOrdersData();
        setVisible(true);
    }

    private void loadOrdersData() {
        try (Connection conn = Main.DBConnection.getConnection()) {
            // Clear existing data
            tableModel.setRowCount(0);

            // SQL query - Added unitprice
            String query = "SELECT " +
                "oc.oid, " +
                "c.cname as customer_name, " +
                "p.pname as product_name, " +
                "oi.unitprice, " +  // Added unit price
                "oi.quantity, " +
                "oc.date as order_date, " +
                "oc.phoneno, " +
                "oc.address, " +
                "oc.totalamount " +
                "FROM order_confirm oc " +
                "JOIN order_item oi ON oc.oid = oi.oid " +
                "JOIN product p ON oi.pid = p.pid " +
                "JOIN customer c ON oc.cid = c.cid " +
                "ORDER BY oc.oid ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

            while (rs.next()) {
                int orderId = rs.getInt("oid");
                String customerName = rs.getString("customer_name");
                String productName = rs.getString("product_name");
                double unitPrice = rs.getDouble("unitprice"); // Get unit price
                int quantity = rs.getInt("quantity");
                String orderDateStr = rs.getString("order_date");
                String phone = rs.getString("phoneno");
                String address = rs.getString("address");
                double totalAmount = rs.getDouble("totalamount");

                // Format order date
                String formattedDate = orderDateStr;
                try {
                    Date orderDate = (Date) inputFormat.parse(orderDateStr);
                    formattedDate = outputFormat.format(orderDate);
                } catch (Exception e) {
                    // Use original date string if parsing fails
                }

                // Add row to table
                tableModel.addRow(new Object[]{
                    orderId,              // Order ID
                    customerName,
                    productName,
                    String.format("$%.2f", unitPrice), // Unit Price
                    quantity,
                    formattedDate,
                    phone,
                    address,
                    String.format("$%.2f", totalAmount)
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No orders found in the system.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminViewOrdersPage();
        });
    }
}