package Admin;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import Customer.Shop;
import Main.DBConnection;

public class ProductManagement extends JFrame implements Product {

    JLabel productLabel, inventoryLabel, historyLabel, logoutLabel;
    JPanel contentPanel;
    private JTextField itemNameField, stockField, priceField;
    private JComboBox<String> categoryCombo;
    private Map<String, String> categoryMap = new HashMap<>();
    private JTextArea descriptionArea;
    private JLabel imageLabel;
    private File selectedImageFile;
    private ProductRefreshListener productRefreshListener;

    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    // constructor
    public ProductManagement() {
        setTitle("Product Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            conn = DBConnection.getConnection(); // ✅ DB connect
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        contentPanel = new JPanel(); // ✅ initialize
        contentPanel.setLayout(new GridBagLayout()); // optional

        buildInventoryForm(); // build UI
        loadCategories();
        setVisible(true);
    }

    private void buildInventoryForm() {
        // Remove the existing content panel setup
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        
        Color lightPink = new Color(255, 228, 235);
        Color buttonPink = new Color(255, 192, 203);
        
        // LEFT: Table Panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(500, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(new String[]{"ID", "Category ID", "Item Name", "Stock", "Price", "Description", "Image"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setBackground(lightPink);
        JScrollPane scrollPane = new JScrollPane(table);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        // CENTER: Form Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(lightPink);
        rightPanel.setPreferredSize(new Dimension(400, 500));

        int y = 30, h = 30, spacing = 60;

        rightPanel.add(makeLabel("Item", 30, y));
        itemNameField = makeField(160, y, rightPanel); y += spacing;

        rightPanel.add(makeLabel("Quantity", 30, y));
        stockField = makeField(160, y, rightPanel); y += spacing;

        rightPanel.add(makeLabel("Price", 30, y));
        priceField = makeField(160, y, rightPanel); y += spacing;
        
        rightPanel.add(makeLabel("Description",30,y));
        descriptionArea = new JTextArea();
        descriptionArea.setBounds(160, y, 220, 60);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        rightPanel.add(descriptionArea);
        y += spacing + 30;
        
        // Category combo
        rightPanel.add(makeLabel("Category", 30, y));
        categoryCombo = new JComboBox<>();
        categoryCombo.setBounds(160, y, 230, 35);
        categoryCombo.setBackground(Color.WHITE);
        rightPanel.add(categoryCombo);
        y += spacing;
        
        // Add image label
        rightPanel.add(makeLabel("Image", 30, y));
        imageLabel = new JLabel("No Image Selected");
        imageLabel.setBounds(160, y, 150, 30);
        rightPanel.add(imageLabel);
        
        // Add Browse button
        JButton browseButton = new JButton("Browse");
        browseButton.setBounds(320, y, 100, 30);
        browseButton.addActionListener(e -> selectImage());
        rightPanel.add(browseButton);
        y += spacing;

        // SOUTH: Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(lightPink);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));

        JButton addBtn = new JButton("Save");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton shopUIBtn = new JButton("ShopUI");
        JButton closeBtn = new JButton("Close");
        JButton resetBtn = new JButton("Reset");

        JButton[] allButtons = {addBtn, updateBtn, deleteBtn, shopUIBtn, resetBtn, closeBtn};
        for (JButton btn : allButtons) {
            btn.setBackground(buttonPink);
            buttonPanel.add(btn);
        }

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadInventoryData();
        
        // Action Listeners
        addBtn.addActionListener(e -> Additem());
        updateBtn.addActionListener(e -> Updateitem());
        deleteBtn.addActionListener(e -> deleteitem());
        resetBtn.addActionListener(e -> Reset());
        shopUIBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ShopView().setVisible(true);
            }
        });
        closeBtn.addActionListener(e -> dispose());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                itemNameField.setText(tableModel.getValueAt(row, 2).toString());
                stockField.setText(tableModel.getValueAt(row, 3).toString());
                priceField.setText(tableModel.getValueAt(row, 4).toString());
                descriptionArea.setText(tableModel.getValueAt(row, 5).toString());
                
                // Set category if available
                String categoryId = tableModel.getValueAt(row, 1).toString();
                for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
                    if (entry.getValue().equals(categoryId)) {
                        categoryCombo.setSelectedItem(entry.getKey());
                        break;
                    }
                }
                
                // Handle image display
                String imageInfo = tableModel.getValueAt(row, 6).toString();
                if (imageInfo.contains("bytes")) {
                    imageLabel.setText("Image loaded from DB");
                    selectedImageFile = null; // We're using DB image, not a new file
                } else {
                    imageLabel.setText("No Image Selected");
                    selectedImageFile = null;
                }
            }
        });
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            
            // Show image preview
            try {
                ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                imageLabel.setText(selectedImageFile.getName());
            } catch (Exception e) {
                imageLabel.setText("Preview error");
                imageLabel.setIcon(null);
            }
        }
    }

    private JLabel makeLabel(String text, int x, int y) {
        JLabel label = new JLabel(text + ":");
        label.setBounds(x, y, 120, 30);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }
    
    private JTextField makeField(int x, int y, JPanel parent) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 220, 30);
        parent.add(field);
        return field;
    }

    public void setProductRefreshListener(ProductRefreshListener listener) {
        this.productRefreshListener = listener;
    }

    private void loadInventoryData() {
        try {
            tableModel.setRowCount(0);
            String sql = "SELECT p.pid, p.categoryId, p.pname, p.quantity, p.price, p.description, p.image " +
                         "FROM product p ORDER BY p.pid";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("pid");
                int categoryid = rs.getInt("categoryId");
                String itemName = rs.getString("pname");
                int stock = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String description = rs.getString("description");
                
                // Handle Blob image
                Blob imageBlob = rs.getBlob("image");
                String imageInfo = (imageBlob != null) ? "Image (" + imageBlob.length() + " bytes)" : "No Image";
                
                tableModel.addRow(new Object[]{id, categoryid, itemName, stock, price, description, imageInfo});
            }
            
            rs.close();
            stmt.close();
            
            if (productRefreshListener != null) 
                productRefreshListener.onProductDataChanged();
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void Additem() {
        try {
            if (itemNameField.getText().isEmpty() || stockField.getText().isEmpty() || 
                priceField.getText().isEmpty() || selectedImageFile == null) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields and select an image.");
                return;
            }
            
            String selectedName = (String) categoryCombo.getSelectedItem();
            if (selectedName == null) {
                JOptionPane.showMessageDialog(this, "Please select a category.");
                return;
            }
            
            String selectedCategoryId = categoryMap.get(selectedName); 
            if (selectedCategoryId == null) {
                JOptionPane.showMessageDialog(this, "Invalid category selected.");
                return;
            }
            
            String sql = "INSERT INTO product (categoryId, pname, description, price, quantity, image) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            // Convert image file to Blob
            try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                pst.setInt(1, Integer.parseInt(selectedCategoryId));
                pst.setString(2, itemNameField.getText());
                pst.setString(3, descriptionArea.getText());
                pst.setDouble(4, Double.parseDouble(priceField.getText()));
                pst.setInt(5, Integer.parseInt(stockField.getText()));
                pst.setBinaryStream(6, fis, (int) selectedImageFile.length());
                
                int rowsAffected = pst.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Product inserted successfully!");
                    loadInventoryData();
                    Reset();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert product.");
                }
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and quantity.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading image file: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void Updateitem() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
                return;
            }
            
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String selectedName = (String) categoryCombo.getSelectedItem();
            if (selectedName == null) {
                JOptionPane.showMessageDialog(this, "Please select a category.");
                return;
            }
            
            String selectedCategoryId = categoryMap.get(selectedName); 
            if (selectedCategoryId == null) {
                JOptionPane.showMessageDialog(this, "Invalid category selected.");
                return;
            }
            
            String sql = "UPDATE product SET categoryId=?, pname=?, quantity=?, price=?, description=?";
            
            // Add image to query if a new image is selected
            if (selectedImageFile != null) {
                sql += ", image=? WHERE pid=?";
            } else {
                sql += " WHERE pid=?";
            }
            
            PreparedStatement pst = conn.prepareStatement(sql);
            
            int paramIndex = 1;
            pst.setInt(paramIndex++, Integer.parseInt(selectedCategoryId));
            pst.setString(paramIndex++, itemNameField.getText());
            pst.setInt(paramIndex++, Integer.parseInt(stockField.getText()));
            pst.setDouble(paramIndex++, Double.parseDouble(priceField.getText()));
            pst.setString(paramIndex++, descriptionArea.getText());
            
            // Handle image update
            if (selectedImageFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                    pst.setBinaryStream(paramIndex++, fis, (int) selectedImageFile.length());
                }
            }
            
            pst.setInt(paramIndex, id);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
                loadInventoryData();
                Reset();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update product.");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void deleteitem() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                return;
            }
            
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure to delete this item?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String sql = "DELETE FROM product WHERE pid=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                loadInventoryData();
                Reset();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product.");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            categoryCombo.removeAllItems();
            categoryMap.clear();
            
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT categoryId, name FROM category");
            
            while (rs.next()) {
                String id = rs.getString("categoryId");
                String name = rs.getString("name");
                categoryCombo.addItem(name);
                categoryMap.put(name, id);
            }
            
            rs.close();
            st.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void Reset() {
        itemNameField.setText("");
        stockField.setText("");
        priceField.setText("");
        descriptionArea.setText("");
        categoryCombo.setSelectedIndex(-1);
        selectedImageFile = null;
        imageLabel.setText("No Image Selected");
        imageLabel.setIcon(null);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductManagement::new);
    }
}