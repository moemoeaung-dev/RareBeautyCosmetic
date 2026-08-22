package Customer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import Main.LoginForm;

public class Basepage extends JFrame {
    protected JPanel contentPanel;
    private Image backgroundImage;
    public Basepage(String titleText) {
    	setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
           
           setTitle("Rare Beauty");
           setExtendedState(JFrame.MAXIMIZED_BOTH);
           setLayout(new BorderLayout());

           // --- Load & scale background image ---
           backgroundImage = new ImageIcon("C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\UIimages\\r.jpg").getImage();
           JPanel bgPanel = new JPanel() {
               @Override
               protected void paintComponent(Graphics g) {
                   super.paintComponent(g);
                   // Scale background image to fit the panel
                   g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
               }
           };
           bgPanel.setLayout(new BorderLayout());
           setContentPane(bgPanel);

           // --- Top navigation panel ---
           JPanel navPanel = new JPanel(new BorderLayout());
           navPanel.setBackground(new Color(255, 182, 193, 230));
           navPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

           // --- Left side: logo + title ---
           JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
           leftPanel.setOpaque(false);

           try {
               BufferedImage logoImg = ImageIO.read(new File("C:\\Users\\Asus\\Desktop\\RareBeautyCosmetic\\src\\UIimages\\Ricon.jpg"));
               Image scaledLogo = logoImg.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
               JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
               leftPanel.add(logoLabel);
           } catch (Exception e) {
               System.out.println("Logo not found");
           }

           JLabel title = new JLabel(titleText);
           title.setFont(new Font("Segoe Script", Font.BOLD, 26));
           title.setForeground(new Color(150, 0, 80));
           leftPanel.add(title);

           navPanel.add(leftPanel, BorderLayout.WEST);

           // --- Navigation items ---
           JPanel navItemsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 0));
           navItemsPanel.setOpaque(false);

           String[] menuItems = {"Home", "Shop", "Cart", "History", "Logout"};
           String[] iconCodes = {"\uD83C\uDFE0", "\uD83D\uDECD", "\uD83D\uDCCB", "\uD83D\uDCCA", "\uD83D\uDEAA"};
           Font iconFont = new Font("Monospaced", Font.PLAIN, 22);

           // Colors
           final Color normalColor = new Color(150, 0, 80);
           final Color hoverColor = new Color(240, 240, 209);
           final Color selectedColor = new Color(255, 105, 180);

           // Track labels for resetting
           final JLabel[] textLabels = new JLabel[menuItems.length];
           final JLabel[] iconLabels = new JLabel[menuItems.length];
         
           final int[] selectedIndex = {-1}; // -1 = none selected

           for (int i = 0; i < menuItems.length; i++) {
               final int index = i;
               final String menuItem = menuItems[i];

               JPanel item = new JPanel(new BorderLayout(5, 0));
               item.setOpaque(false);
               item.setCursor(new Cursor(Cursor.HAND_CURSOR));

               JLabel icon = new JLabel(iconCodes[i]);
               icon.setFont(iconFont);
               icon.setForeground(normalColor);

               JLabel text = new JLabel(menuItems[i]);
               text.setFont(new Font("Monospaced", Font.PLAIN, 18));
               text.setForeground(normalColor);

               textLabels[i] = text;
               iconLabels[i] = icon;

               item.add(icon, BorderLayout.WEST);
               item.add(text, BorderLayout.CENTER);
              
               //final int[] selectedIndex = {-1}; // at the top of your loop, before the listener

               item.addMouseListener(new java.awt.event.MouseAdapter() {
                   public void mouseClicked(java.awt.event.MouseEvent evt) {
                       selectedIndex[0] = index; // mark this as selected

                       // Reset all items
                       for (int j = 0; j < menuItems.length; j++) {
                           textLabels[j].setForeground(normalColor);
                           iconLabels[j].setForeground(normalColor);
                       }

                       // Highlight clicked
                       text.setForeground(selectedColor);
                       icon.setForeground(selectedColor);

                       navigate(menuItem);
                   }

                   public void mouseEntered(java.awt.event.MouseEvent evt) {
                       if (selectedIndex[0] != index) { // only hover if not selected
                           icon.setForeground(hoverColor);
                           text.setForeground(hoverColor);
                       }
                   }

                   public void mouseExited(java.awt.event.MouseEvent evt) {
                       if (selectedIndex[0] != index) { // only reset if not selected
                           icon.setForeground(normalColor);
                           text.setForeground(normalColor);
                       }
                   }
               });


               navItemsPanel.add(item);
           }

           navPanel.add(navItemsPanel, BorderLayout.EAST);
           bgPanel.add(navPanel, BorderLayout.NORTH);

           // --- Content panel ---
           contentPanel = new JPanel(new BorderLayout());
           contentPanel.setOpaque(false);
           bgPanel.add(contentPanel, BorderLayout.CENTER);

           setVisible(true);
       }

       private void navigate(String page) {
           switch (page) {
               case "Home":
                   dispose();
                   new Home();
                   break;
               case "Shop":
                   dispose();
                   new Shop();
                   break;
               case "Cart":
                   dispose();
                   new ViewCartPage(LoginForm.loggedInCustomerId);
                   break;
               case "History":
                   dispose();
                   new OrderHistoryPage(LoginForm.loggedInCustomerId);
                   break;
               case "Logout":
                   dispose();
                   new LoginForm().setVisible(true);
                   break;
           }
       }
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
         
            new Basepage("Rare Beauty");
        });
    }
}