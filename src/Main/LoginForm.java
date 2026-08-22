package Main;
import javax.swing.*;

import Admin.admindashboard;
import Customer.Home;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {
	// Add these labels as class members
	private JLabel emailErrorLabel;
	private JLabel passwordErrorLabel;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;
    public static int loggedInCustomerId = -1;
    
    public LoginForm() {
        setTitle("Rare Beauty Cosmetic Shop - Login");
        setSize(430, 420);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        
        // Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel("");
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBackground(new Color(255, 182, 193, 230));
        add(backgroundPanel);

        // Transparent panel for login form
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 182, 193, 230));
        loginPanel.setPreferredSize(new Dimension(360, 360));
        loginPanel.setLayout(null);
        //loginPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));
        
        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 26));
        titleLabel.setForeground(new Color(120, 0, 60));
        titleLabel.setBounds(120, 15, 100, 30);
        loginPanel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        emailLabel.setForeground(new Color(120, 0, 60));
        emailLabel.setBounds(10, 60, 120, 25);
        loginPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(new Font("Monospaced", Font.BOLD, 16));
        emailField.setForeground(new Color(120, 0, 60));;
        emailField.setBounds(120, 60, 180, 25);
        loginPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        passwordLabel.setForeground(new Color(120, 0, 60));;
        passwordLabel.setBounds(10, 100, 120, 25); 
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Monospaced", Font.BOLD, 16));
        passwordField.setForeground(new Color(120, 0, 60));;
        passwordField.setBounds(120, 100, 180, 25);
        loginPanel.add(passwordField);
        
     // Email Error Label
        emailErrorLabel = new JLabel("");
        emailErrorLabel.setForeground(Color.RED);
        emailErrorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailErrorLabel.setBounds(120, 79, 200, 20);
        loginPanel.add(emailErrorLabel);

        // Password Error Label
        passwordErrorLabel = new JLabel("");
        passwordErrorLabel.setForeground(Color.RED);
        passwordErrorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        passwordErrorLabel.setBounds(120, 125, 200, 20);
        loginPanel.add(passwordErrorLabel);


        loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.setBounds(120, 170, 100, 30);
        loginPanel.add(loginButton);
        
        

        registerLabel = new JLabel("Don't have an account? Register!",JLabel.CENTER);
        registerLabel.setBounds(30, 240, 280, 30);
        registerLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        registerLabel.setForeground(new Color(120, 0, 60));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginPanel.add(registerLabel);

        // Center panel
        backgroundPanel.add(loginPanel);

        // Action Listeners
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            boolean valid = true;

            // Reset errors
            emailErrorLabel.setText("");
            passwordErrorLabel.setText("");

            // Email validation
            if (email.isEmpty()) {
                emailErrorLabel.setText("Email is required");
                valid = false;
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                emailErrorLabel.setText("Invalid email format");
                valid = false;
            }

            // Password validation
            if (password.isEmpty()) {
                passwordErrorLabel.setText("Password is required");
                valid = false;
            }

            if (valid) {
                if (authenticateUser(email, password)) {
                    // successful login is already handled
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password!");
                }
            }
        });


        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterPage().setVisible(true);
            }
        });
    }
    void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(120, 0, 60));
        btn.setFont(new Font("Monospaced", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        //btn.setFont();
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(214, 0, 114));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(120, 0, 60));
            }
        });
    }
    private boolean authenticateUser(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT cid, password, email FROM customer WHERE email=?";
            assert conn != null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password"); // Use correct column name
                int customerId = rs.getInt("cid"); // Use correct column name
                String userEmail = rs.getString("email"); // Use correct column name
                
//                // Debug output
//                System.out.println("Email from DB: " + userEmail);
//                System.out.println("Password from DB: " + storedHash);
//                System.out.println("Input password: " + password);
                
                if (PasswordUtils.verifyPassword(password, storedHash)) {
                    loggedInCustomerId = customerId;
                    
                    if (userEmail.equals("admin@gmail.com")) {
                        JOptionPane.showMessageDialog(this, "Welcome Admin!");
                        dispose();
                        new admindashboard().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Welcome User!");
                        dispose();
                        new Home().setVisible(true);
                    }
                    return true;
                } else {
                    System.out.println("Password verification failed");
                }
            } else {
                System.out.println("No user found with email: " + email);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return false;
    }

    // Background Panel with Image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.out.println("Background image not found.");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}