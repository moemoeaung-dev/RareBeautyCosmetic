package Main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterPage extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton;
    private JLabel loginLabel;
    private JLabel nameErrorLabel, emailErrorLabel, passwordErrorLabel, confirmPasswordErrorLabel;

    public RegisterPage() {
        setTitle("Rare Beauty Cosmetic Shop - Register");
        setSize(420, 450);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel("D:\\DS\\RareBeautyCosmetic\\bin\\resources\\login.jpg");
        backgroundPanel.setBackground(new Color(255, 182, 193, 230));
        backgroundPanel.setPreferredSize(new Dimension(400,400));
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel);

        // Transparent panel for register form
        JPanel registerPanel = new JPanel();
        // Semi-transparent blush pink login panel
        registerPanel.setBackground(new Color(255, 182, 193, 230));

        registerPanel.setPreferredSize(new Dimension(400, 400));
        registerPanel.setLayout(null);
        //registerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));

        JLabel titleLabel = new JLabel("Register", JLabel.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setForeground(new Color(120, 0, 60));
        titleLabel.setBounds(120, 15, 120, 30);
        registerPanel.add(titleLabel);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLabel.setForeground(new Color(120, 0, 60));
        nameLabel.setBounds(40, 80, 120, 25);
        registerPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameField.setForeground(new Color(120, 0, 60));
        nameField.setBounds(150, 80, 150, 25);
        registerPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        emailLabel.setForeground(new Color(120, 0, 60));
        emailLabel.setBounds(40, 140, 120, 25);
        registerPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(new Font("Monospaced", Font.BOLD, 16));
        emailLabel.setForeground(new Color(120, 0, 60));
        emailField.setBounds(150, 140, 150, 25);
        registerPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        passwordLabel.setForeground(new Color(120, 0, 60));
        passwordLabel.setBounds(40, 200, 120, 25);
        registerPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Monospaced", Font.BOLD, 16));
        passwordField.setForeground(new Color(120, 0, 60));
        passwordField.setBounds(150, 200, 150, 25);
        registerPanel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm:");
        confirmPasswordLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        confirmPasswordLabel.setForeground(new Color(120, 0, 60));
        confirmPasswordLabel.setBounds(40, 250, 120, 25);
        registerPanel.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Monospaced", Font.BOLD, 16));
        confirmPasswordField.setForeground(new Color(120, 0, 60));
        confirmPasswordField.setBounds(150, 250, 150, 25);
        registerPanel.add(confirmPasswordField);

     // Name Error
        nameErrorLabel = new JLabel("");
        nameErrorLabel.setForeground(Color.RED);
        nameErrorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        nameErrorLabel.setBounds(150, 110, 200, 15);
        registerPanel.add(nameErrorLabel);

        // Email Error
        emailErrorLabel = new JLabel("");
        emailErrorLabel.setForeground(Color.RED);
        emailErrorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailErrorLabel.setBounds(150, 170, 200, 15);
        registerPanel.add(emailErrorLabel);

        // Password Error
        passwordErrorLabel = new JLabel("");
        passwordErrorLabel.setForeground(Color.RED);
        passwordErrorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        passwordErrorLabel.setBounds(150, 230, 200, 15);
        registerPanel.add(passwordErrorLabel);

        // Confirm Password Error
        confirmPasswordErrorLabel = new JLabel("");
        confirmPasswordErrorLabel.setForeground(Color.RED);
        confirmPasswordErrorLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        confirmPasswordErrorLabel.setBounds(150, 280, 200, 15);
        registerPanel.add(confirmPasswordErrorLabel);

        
        registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.setBounds(120, 300, 130, 35);
        registerPanel.add(registerButton);

        loginLabel = new JLabel("Already a user? Login");
        loginLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        loginLabel.setForeground(new Color(120, 0, 60));
        loginLabel.setBounds(120, 340, 200, 25);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerPanel.add(loginLabel);

        // Add panel to background
        backgroundPanel.add(registerPanel);

        // Action Listeners
        registerButton.addActionListener(e -> registerUser());

        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose(); // close register form
                new LoginForm().setVisible(true); // open login form
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
    // Registration logic
    private void registerUser() {
    	  String name = nameField.getText().trim();
    	    String email = emailField.getText().trim();
    	    String password = new String(passwordField.getPassword());
    	    String confirmPassword = new String(confirmPasswordField.getPassword());

    	    boolean valid = true;

    	    // Reset errors
    	    nameErrorLabel.setText("");
    	    emailErrorLabel.setText("");
    	    passwordErrorLabel.setText("");
    	    confirmPasswordErrorLabel.setText("");

    	    nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
    	    emailField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
    	    passwordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
    	    confirmPasswordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

    	    // Name validation
    	    if (name.isEmpty()) {
    	        nameErrorLabel.setText("Name is required");
    	        nameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    	        valid = false;
    	    }

    	    // Email validation
    	    if (email.isEmpty()) {
    	        emailErrorLabel.setText("Email is required");
    	        emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    	        valid = false;
    	    } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
    	        emailErrorLabel.setText("Invalid email format");
    	        emailField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    	        valid = false;
    	    }

    	    // Password validation
    	    if (password.isEmpty()) {
    	        passwordErrorLabel.setText("Password is required");
    	        passwordField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    	        valid = false;
    	    }

    	    // Confirm Password validation
    	    if (confirmPassword.isEmpty()) {
    	        confirmPasswordErrorLabel.setText("Confirm passwordrequired");
    	        confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    	        valid = false;
    	    } else if (!password.equals(confirmPassword)) {
    	        confirmPasswordErrorLabel.setText("Passwords do not match");
    	        confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    	        valid = false;
    	    }

    	    // If all valid → insert into DB
    	    if (valid) {
    	        try (Connection conn = DBConnection.getConnection()) {
    	            String hashedPassword = PasswordUtils.hashPassword(password);
    	            String sql = "INSERT INTO customer (cname, email, password) VALUES (?, ?, ?)";
    	            PreparedStatement stmt = conn.prepareStatement(sql);
    	            stmt.setString(1, name);
    	            stmt.setString(2, email);
    	            stmt.setString(3, hashedPassword);
    	            stmt.executeUpdate();

    	            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
    	            dispose();
    	            new LoginForm().setVisible(true);
    	        } catch (Exception ex) {
    	            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    	            ex.printStackTrace();
    	        }
    	    }
    }

    // Background Panel class
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
            new RegisterPage().setVisible(true);
        });
    }
}

