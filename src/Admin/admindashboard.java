package Admin;
import javax.swing.*;

import Customer.Shop;
import Main.LoginForm;

import java.awt.*;
public class admindashboard extends JFrame {
    private Image backgroundImage;
    
    public admindashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // ==== Background Panel ====
        backgroundImage = new ImageIcon("C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\UIimages\\r.jpg").getImage();
        
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());
        
        // ==== Resize RareBeauty Logo ====
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\UIimages\\Ricon.jpg");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaledLogo);
        
        // ==== Title Label with Icon ====
        JLabel titleLabel = new JLabel("Admin Dashboard", logoIcon, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe Script", Font.BOLD, 40));
        titleLabel.setForeground(new Color(150, 0, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Control icon position relative to text
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);  // text to the right of icon
        titleLabel.setVerticalTextPosition(SwingConstants.CENTER);   // vertically centered
        
        // ==== Main content panel that holds both title and buttons ====
        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 30));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0)); // Add top margin
        
        // Add title to the main content panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        mainContentPanel.add(titlePanel, BorderLayout.NORTH);
        
        // ==== Center Panel wrapper (to center fixed-size panel) ====
        JPanel centerWrapper = new JPanel(new GridBagLayout()); // centers the panel
        centerWrapper.setOpaque(false);
        
        // ==== Center Panel with fixed size (2x2 buttons) ====
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 50, 50));
        centerPanel.setPreferredSize(new Dimension(800, 250)); // fixed size panel
        centerPanel.setOpaque(false);
        
        JButton btnProduct = createStyledButton("Product Management");
        JButton btnShop = createStyledButton("Shop UI");
        JButton btnUserAccount = createStyledButton("User Account Management");
        JButton btnViewOrder = createStyledButton("View Customer Order");
        
        centerPanel.add(btnProduct);
        centerPanel.add(btnShop);
        centerPanel.add(btnUserAccount);
        centerPanel.add(btnViewOrder);
        
        centerWrapper.add(centerPanel);
        mainContentPanel.add(centerWrapper, BorderLayout.CENTER);
        
        // Add the main content panel to the background
        bgPanel.add(mainContentPanel, BorderLayout.CENTER);
        
        // ==== Logout Button at Bottom-Right ====
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        southPanel.setOpaque(false);
        JButton btnLogout = createStyledButton("Logout");
        btnLogout.setBackground(new Color(240, 180, 180));
        southPanel.add(btnLogout);
        bgPanel.add(southPanel, BorderLayout.SOUTH);
        
        // Set as content pane
        setContentPane(bgPanel);
        
        // ==== Action Listeners ====
        btnProduct.addActionListener(e -> switchToPage("ProductManagement"));
        btnShop.addActionListener(e -> switchToPage("ShopManagement"));
        btnUserAccount.addActionListener(e -> switchToPage("UserAccounts"));
        btnViewOrder.addActionListener(e -> switchToPage("Orders Overview"));
        btnLogout.addActionListener(e -> switchToPage("Login Page"));
        setMinimumSize(new Dimension(600, 400));
        setVisible(true);
    }
    
    // Styled button (unchanged)
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 50)); // fixed size
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 115, 129));
        button.setForeground(new Color(150, 0, 80));
        button.setFont(new Font("Monospaced", Font.BOLD, 16));
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 150, 150), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 0, 80));
                button.setForeground(new Color(255, 232, 242));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 115, 129));
                button.setForeground(new Color(150, 0, 80));
            }
        });
        
        return button;
    }
    
    private void switchToPage(String pageName) {
        JFrame page = null;
        
        switch (pageName) {
            case "ProductManagement":
                page = new ProductManagement();
                break;
            case "ShopManagement":
                page = new ShopView();
                break;
            case "UserAccounts":
                page = new UserManagement();
                break;
            case "Orders Overview":
                page= new AdminViewOrdersPage();
                break;
            case "Login Page":
                page=new LoginForm();
                break;
        }
        
        if (page != null) {
            page.setSize(800, 500);
            page.setLocationRelativeTo(null);
            page.setVisible(true);
            this.dispose();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new admindashboard().setVisible(true));
    }
}