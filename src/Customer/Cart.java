package Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Main.DBConnection;
import java.sql.Blob;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Cart {

    public static class Item {
        public String name;
        public int productId; 
        public double price;
        public int quantity;
        public byte[] imageData; // Changed from String imagePath to byte[] for Blob

        public Item(String name, double price, int quantity, byte[] imageData) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.imageData = imageData;
        }

        public double getTotal() {
            return price * quantity;
        }
    }

    public static List<Item> getCartItems(int customerId) {
        List<Item> items = new ArrayList<>();
        
        // UPDATED SQL query - productId ကိုပါ select လုပ်မယ်
        String sql = "SELECT ci.quantity, p.pid, p.pname, p.price, p.image " + // p.pid ကို ထည့်မယ်
                     "FROM cart_item ci " +
                     "JOIN product p ON ci.pid = p.pid " +
                     "WHERE ci.cid = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("pid"); // ✅ productId ကိုဖတ်မယ်
                String name = rs.getString("pname");
                double price = rs.getDouble("price");
                int qty = rs.getInt("quantity");
                
                // Handle Blob image data
                Blob imageBlob = rs.getBlob("image");
                byte[] imageData = null;
                
                if (imageBlob != null) {
                    try (InputStream inputStream = imageBlob.getBinaryStream();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        imageData = outputStream.toByteArray();
                    }
                }

                // ✅ productId ကို ထည့်သွင်းမယ်
                Item item = new Item(name, price, qty, imageData);
                item.productId = productId; // productId ကို set လုပ်မယ်
                items.add(item);
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println("SQL Error in getCartItems: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing image data: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }


    // Remove item from cart method - ER diagram အရ
    public static boolean removeFromCart(int customerId, int productId) {
        String sql = "DELETE FROM cart_item WHERE cid = ? AND pid = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            ps.setInt(2, productId);
            int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update item quantity in cart - ER diagram အရ
    public static boolean updateCartQuantity(int customerId, int productId, int newQuantity) {
        String sql = "UPDATE cart_item SET quantity = ? WHERE cid = ? AND pid = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, newQuantity);
            ps.setInt(2, customerId);
            ps.setInt(3, productId);
            int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating cart quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get total cart value - ER diagram အရ
    public static double getCartTotal(int customerId) {
        String sql = "SELECT SUM(p.price * ci.quantity) as total " +
                     "FROM cart_item ci " +
                     "JOIN product p ON ci.pid = p.pid " +
                     "WHERE ci.cid = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting cart total: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }

    // Clear entire cart - ER diagram အရ
    public static boolean clearCart(int customerId) {
        String sql = "DELETE FROM cart_item WHERE cid = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}