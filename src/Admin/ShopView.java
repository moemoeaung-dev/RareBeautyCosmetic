package Admin;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import Main.DBConnection;

public class ShopView extends JFrame {
    private JPanel productPanel;
    private JComboBox<String> categoryCombo;
    
    public ShopView() {
        setTitle("Rare Beauty - Product Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Product panel with scroll
        productPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        productPanel.setBackground(new Color(255, 250, 250));
        productPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        // Load all products initially
        loadProducts(0);
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(new Color(255, 245, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Back button
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);
        
        backButton.addActionListener(e -> {
            dispose();
            new admindashboard();
        });
        
        // Title label - centered
        JLabel titleLabel = new JLabel("Rare Beauty Products", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(128, 0, 128));
        
        // Filter panel with better UI
        JPanel filterPanel = createFilterPanel();
        
        // Add components to header
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(new Color(255, 245, 250));
        
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(new Color(108, 117, 125));
        
        // Styled combo box
        categoryCombo = new JComboBox<>();
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        categoryCombo.setPreferredSize(new Dimension(180, 35));
        
        loadCategories(categoryCombo);
        
        categoryCombo.addActionListener(e -> {
            String selectedCategory = (String) categoryCombo.getSelectedItem();
            if (selectedCategory.equals("All Categories")) {
                loadProducts(0);
            } else {
                int categoryId = getCategoryId(selectedCategory);
                loadProducts(categoryId);
            }
        });
        
        // Refresh button
        JButton refreshButton = new JButton("🔄");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        refreshButton.setBackground(new Color(40, 167, 69));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setToolTipText("Refresh Products");
        refreshButton.setFocusPainted(false);
        
        refreshButton.addActionListener(e -> {
            String selectedCategory = (String) categoryCombo.getSelectedItem();
            if (selectedCategory.equals("All Categories")) {
                loadProducts(0);
            } else {
                int categoryId = getCategoryId(selectedCategory);
                loadProducts(categoryId);
            }
            JOptionPane.showMessageDialog(this, "Products refreshed!");
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(categoryCombo);
        filterPanel.add(refreshButton);
        
        return filterPanel;
    }
    
    private void loadCategories(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem("All Categories");
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT categoryId, name FROM category ORDER BY name";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                comboBox.addItem(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }
    
    private int getCategoryId(String categoryName) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT categoryId FROM category WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("categoryId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private void loadProducts(int categoryId) {
        productPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql;
            PreparedStatement ps;
            
            if (categoryId == 0) {
                sql = "SELECT p.pid, p.pname, p.quantity, p.price, p.description, p.image, c.name as category_name " +
                      "FROM product p LEFT JOIN category c ON p.categoryId = c.categoryId " +
                      "ORDER BY p.pname";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT p.pid, p.pname, p.quantity, p.price, p.description, p.image, c.name as category_name " +
                      "FROM product p LEFT JOIN category c ON p.categoryId = c.categoryId " +
                      "WHERE p.categoryId = ? ORDER BY p.pname";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryId);
            }
            
            ResultSet rs = ps.executeQuery();
            int productCount = 0;
            
            while (rs.next()) {
                int pid = rs.getInt("pid");
                String name = rs.getString("pname");
                int stock = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String desc = rs.getString("description");
                Blob imageBlob = rs.getBlob("image");
                String categoryName = rs.getString("category_name");
                
                cardView card = new cardView(pid, name, stock, price, desc, imageBlob);
                productPanel.add(card);
                productCount++;
            }
            
            if (productCount == 0) {
                JPanel noProductsPanel = new JPanel(new BorderLayout());
                noProductsPanel.setBackground(new Color(255, 250, 250));
                
                JLabel noProductsLabel = new JLabel("No products found in this category", SwingConstants.CENTER);
                noProductsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                noProductsLabel.setForeground(Color.GRAY);
                noProductsLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
                
                noProductsPanel.add(noProductsLabel, BorderLayout.CENTER);
                productPanel.add(noProductsPanel);
            }
            
            rs.close();
            ps.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Load Error: " + e.getMessage());
            
            // Show error message in UI
            JLabel errorLabel = new JLabel("Error loading products. Please try again.", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            errorLabel.setForeground(Color.RED);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
            productPanel.add(errorLabel);
        }
        
        productPanel.revalidate();
        productPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ShopView();
        });
    }
}