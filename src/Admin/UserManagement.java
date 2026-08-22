package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class UserManagement extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private Connection conn;

    public UserManagement() {
        // JFrame setup
        setTitle("User Account Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(250, 240, 245));
        setLayout(new BorderLayout());

        // Build UI
        createUI();

        // Connect database and load table
        connectDatabase();
        loadDataFromDB();

        // Show window
        setVisible(true);
    }

    private void createUI() {
        // Title label
        JLabel title = new JLabel("User Account Management", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setForeground(new Color(150, 40, 90));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Name", "Email", "Password"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 210, 225));
        table.getTableHeader().setForeground(new Color(70, 20, 40));
        table.setSelectionBackground(new Color(240, 170, 190));
        table.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        deleteButton.setBackground(new Color(200, 70, 120));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        deleteButton.addActionListener(this::deleteSelectedRows);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(250, 235, 240));
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void connectDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/rarebeauty"; // change to your DB
            String user = "root";
            String password = "root";
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDataFromDB() {
        try {
            model.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cid, cname, email, password FROM customer"); // change table if needed

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("cid"),
                        rs.getString("cname"),
                        rs.getString("email"),
                        rs.getString("password")
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSelectedRows(ActionEvent e) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length > 0) {
            try {
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int id = (int) table.getValueAt(selectedRows[i], 0);
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM customer WHERE cid=?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    model.removeRow(selectedRows[i]);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No row selected!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserManagement::new);
    }
}
