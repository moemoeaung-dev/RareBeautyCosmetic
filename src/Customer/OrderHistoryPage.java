package Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderHistoryPage extends Basepage {
    private int customerId;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public OrderHistoryPage(int customerId) {
    	super("History");
    	setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.customerId = customerId;

        setTitle("Order History");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.PINK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        //mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Order History", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        headerLabel.setForeground(new Color(150, 0, 80));
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Product Image", "Product", "Price", "Qty", "Order Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class; // Image column
                return Object.class;
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Monospaced", Font.PLAIN, 14));
       orderTable.setForeground(new Color(150, 0, 80));
        orderTable.setRowHeight(80); // Increased row height for images
        orderTable.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 18));
        orderTable.getTableHeader().setForeground(new Color(150, 0, 80));
        orderTable.getTableHeader().setBackground(new Color(255, 143, 163));
        
        // Set column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Image
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Product name
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Price
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(50);  // Qty
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Order date

        // Center align all columns except product name
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setBackground(new Color(255, 217, 234));
        centerRenderer.setForeground(new Color(150, 0, 80));
        centerRenderer.setFont(new Font("Monospaced",Font.PLAIN,14));
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            if (i != 1) { // Skip product name column
                orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Left align product column
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBackground(new Color(255, 217, 234));
        leftRenderer.setForeground(new Color(150, 0, 80));
        leftRenderer.setFont(new Font("Monospaced",Font.PLAIN,14));
        
        orderTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);

        // Custom renderer for image column
        orderTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        scrollPane.setBackground(new Color(255, 240, 245)); // Match main panel background
        
        // Scroll bar တွေကို transparent လုပ်ပြီး mouseover မှသာ ပေါ်အောင်လုပ်ခြင်း
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setPreferredSize(new Dimension(0, 0)); // Scroll bar size ကို သုညလုပ်ခြင်း
        
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setPreferredSize(new Dimension(0, 0)); // Scroll bar size ကို သုညလုပ်ခြင်း
        
        // Mouse listener to show scroll bars on mouseover
        scrollPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                verticalScrollBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
                horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
                scrollPane.revalidate();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Scroll bar များကို ပြန်ဖျောက်ရန်၊ ဒါပေမယ့် mouse က scroll bar ပေါ်မှာရှိနေရင် မဖျောက်ရ
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mousePos, scrollPane);
                if (!verticalScrollBar.getBounds().contains(mousePos) && 
                    !horizontalScrollBar.getBounds().contains(mousePos)) {
                    verticalScrollBar.setPreferredSize(new Dimension(0, Integer.MAX_VALUE));
                    horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 0));
                    scrollPane.revalidate();
                }
            }
        });
        
        // Scroll bar များအတွက်လည်း mouse listener ထည့်ခြင်း
        verticalScrollBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Delay နည်းနည်းပေးပြီးမှ စစ်ဆေးခြင်း
                Timer timer = new Timer(500, event -> {
                    Point mousePos = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(mousePos, scrollPane);
                    if (!verticalScrollBar.getBounds().contains(mousePos) && 
                        !horizontalScrollBar.getBounds().contains(mousePos) &&
                        !scrollPane.getViewport().getBounds().contains(mousePos)) {
                        verticalScrollBar.setPreferredSize(new Dimension(0, Integer.MAX_VALUE));
                        horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 0));
                        scrollPane.revalidate();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadOrderHistory();
        setVisible(true);
    }

    // Custom cell renderer for images
    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setBackground(new Color(255, 217, 234));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (value instanceof ImageIcon) {
                label.setIcon((ImageIcon) value);
            } else {
                label.setText("No Image");
                label.setForeground(new Color(255, 217, 234));
            }
            
            if (isSelected) {
                label.setBackground(new Color(255, 217, 234));
                label.setOpaque(true);
            } else {
                label.setBackground(new Color(255, 217, 234));
                label.setOpaque(true);
            }
            
            return label;
        }
    }

    private void loadOrderHistory() {
        try (Connection conn = Main.DBConnection.getConnection()) {
            // Clear existing data
            tableModel.setRowCount(0);

            // SQL query to get order history with product images
            String query = "SELECT " +
                "p.pname as product_name, " +
                "oi.unitprice as price, " +
                "oi.quantity as quantity, " +
                "oc.date as order_date, " +
                "p.image as product_image " +
                "FROM order_confirm oc " +
                "JOIN order_item oi ON oc.oid = oi.oid " +
                "JOIN product p ON oi.pid = p.pid " +
                "WHERE oc.cid = ? " +
                "ORDER BY oc.date DESC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

            while (rs.next()) {
                String productName = rs.getString("product_name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String orderDateStr = rs.getString("order_date");
                Blob imageBlob = rs.getBlob("product_image");

                // Format order date
                String formattedDate = orderDateStr;
                try {
                    Date orderDate = inputFormat.parse(orderDateStr);
                    formattedDate = outputFormat.format(orderDate);
                } catch (Exception e) {
                    // Use original date string if parsing fails
                }

                // Process product image
                ImageIcon productImage = null;
                if (imageBlob != null) {
                    try {
                        byte[] imageData = imageBlob.getBytes(1, (int) imageBlob.length());
                        ImageIcon originalIcon = new ImageIcon(imageData);
                        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        productImage = new ImageIcon(scaledImage);
                    } catch (Exception e) {
                        System.out.println("Error loading image: " + e.getMessage());
                    }
                }

                // Add row to table
                tableModel.addRow(new Object[]{
                    productImage,
                    productName,
                    String.format("$%.2f", price),
                    quantity,
                    formattedDate
                });
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No order history found.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading order history: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            
            // Add sample data for demonstration
            addSampleData();
        }
    }

    private void addSampleData() {
        // Add sample data with dummy images
        ImageIcon sampleImage = createSampleImage();
        
        String[] sampleProducts = {
            "Apple Airpods Pro",
            "Apple Airpods Pro", 
            "Apple Airpods Pro"
        };

        for (int i = 0; i < sampleProducts.length; i++) {
            tableModel.addRow(new Object[]{
                sampleImage,
                sampleProducts[i],
                "$249.99",
                "2",
                "Mar 23, 2021 14:30"
            });
        }
    }

    private ImageIcon createSampleImage() {
        // Create a simple placeholder image
        Image image = new ImageIcon().getImage();
        return new ImageIcon(image);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Test with a customer ID
            new OrderHistoryPage(1);
        });
    }
}