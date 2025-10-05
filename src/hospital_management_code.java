import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mysql.cj.protocol.Resultset;
import com.google.zxing.common.HybridBinarizer;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import java.awt.image.BufferedImage;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.*;
import java.nio.file.Files;
import java.awt.event.*;
import java.net.*;

import com.itextpdf.text.pdf.*;
import java.io.*;
import java.util.Map;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import java.sql.*;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.BaseColor;
import java.util.Base64;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import java.awt.Dimension; // Ensure this is the only Dimension import
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.ResultSet;
import com.google.zxing.Result;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.sql.*;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class hospital_management_code {
  private static final String DB_URL = "jdbc:sqlite:hospital.db";
  private static Connection conn;
  private static final Color PRIMARY_COLOR = new Color(176, 34, 22);
  private static final Color SECONDARY_COLOR = new Color(242, 222, 222);
  private static final Color TERTIARY_COLOR = new Color(255, 60, 40);
  private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 24);
  private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
  private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
  private static final Color HOVER_COLOR = new Color(255, 70, 50);
  private static JFrame mainFrame;

  public static void main(String[] args) {
    try {
      // Load the SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    initializeDatabase();
    showLoginPage();
  }

  private static void initializeDatabase() {
    try {

      conn = DriverManager.getConnection(DB_URL);
      System.out.println("Database connection established successfully!");

      Statement stmt = conn.createStatement();

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS users (" +
              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "username TEXT UNIQUE NOT NULL, " +
              "password TEXT NOT NULL, " +
              "email TEXT NOT NULL)");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS patients (" +
              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "name TEXT NOT NULL, " +
              "age INTEGER, " +
              "gender TEXT, " +
              "address TEXT, " +
              "phone_number TEXT UNIQUE, " +
              "email TEXT UNIQUE)");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS doctors (" +
              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "name TEXT NOT NULL, " +
              "specialty TEXT)");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS billing (" +
              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "patient_id INTEGER, " +
              "amount REAL, " +
              "date TEXT, " +
              "FOREIGN KEY(patient_id) REFERENCES patients(id))");

      stmt.execute(
          "CREATE TABLE IF NOT EXISTS appointments (" +
              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "patient_id INTEGER, " +
              "doctor_id INTEGER, " +
              "date DATE, " +
              "time TIME, " +
              "FOREIGN KEY(patient_id) REFERENCES patients(id), " +
              "FOREIGN KEY(doctor_id) REFERENCES doctors(id))");

      stmt.close();
      System.out.println("Database tables created successfully!");

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  private static JTextField createStyledTextField() {
    JTextField field = new JTextField();
    field.setPreferredSize(new Dimension(200, 30));
    field.setMaximumSize(new Dimension(200, 30)); // Added maximum size to prevent stretching
    field.setFont(LABEL_FONT);
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR),
        BorderFactory.createEmptyBorder(2, 8, 2, 8))); // Reduced padding
    return field;
  }

  private static JPasswordField createStyledPasswordField() {
    JPasswordField field = new JPasswordField();
    field.setPreferredSize(new Dimension(200, 30));
    field.setMaximumSize(new Dimension(200, 30));
    field.setFont(LABEL_FONT);
    field.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR),
        BorderFactory.createEmptyBorder(2, 8, 2, 8)));
    return field;
  }

  private static JButton createStyledButton(String text, Color bgColor, Color fgColor) {
    JButton button = new JButton(text);
    button.setFont(BUTTON_FONT);
    button.setForeground(fgColor);
    button.setBackground(bgColor);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(120, 12, 8), 2),
        BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    button.setOpaque(true);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(180, 35));
    button.setBorder(BorderFactory.createRaisedBevelBorder());

    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setBackground(bgColor.brighter());
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setBackground(bgColor);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        button.setBorder(BorderFactory.createLoweredBevelBorder());
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        button.setBorder(BorderFactory.createRaisedBevelBorder());
      }
    });

    button.setMaximumSize(new Dimension(180, 35));
    return button;
  }

  private static JPanel createCenteredPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.setOpaque(false);
    panel.add(Box.createHorizontalGlue());
    return panel;
  }

  private static void showLoginPage() {
    JFrame loginFrame = new JFrame("Hospital Management System");
    loginFrame.setSize(1000, 700);
    loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    loginFrame.setLocationRelativeTo(null);

    // Main panel with gradient background
    JPanel mainPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(170, 35, 25), w, h, new Color(210, 60, 50));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
      }
    };
    mainPanel.setLayout(new BorderLayout(20, 20));

    // Header Panel
    JPanel headerPanel = new JPanel();
    headerPanel.setOpaque(false);
    JLabel titleLabel = new JLabel("Hospital Management System");
    titleLabel.setFont(HEADER_FONT);
    titleLabel.setForeground(Color.WHITE);
    headerPanel.add(titleLabel);

    // Create a new panel for content with left (login) and right (image) sides
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new GridLayout(1, 2)); // 1 row, 2 columns
    contentPanel.setOpaque(false);

    // Login Panel (Left Side)
    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
    loginPanel.setOpaque(false);
    loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    // Create styled components
    JTextField usernameField = createStyledTextField();
    JPasswordField passwordField = createStyledPasswordField();
    JButton loginButton = createStyledButton("Login", TERTIARY_COLOR, SECONDARY_COLOR);
    JButton signupButton = createStyledButton("Sign Up", SECONDARY_COLOR, PRIMARY_COLOR);
    JButton resetButton = createStyledButton("Forgot Password", SECONDARY_COLOR, PRIMARY_COLOR);

    // Create labels with new style
    JLabel usernameLabel = new JLabel("Username");
    JLabel passwordLabel = new JLabel("Password");
    usernameLabel.setFont(LABEL_FONT);
    passwordLabel.setFont(LABEL_FONT);
    usernameLabel.setForeground(Color.WHITE);
    passwordLabel.setForeground(Color.WHITE);

    // Center align the labels
    usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Center align all components
    usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
    passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
    loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Add components to login panel
    loginPanel.add(Box.createVerticalGlue());
    loginPanel.add(usernameLabel);
    loginPanel.add(Box.createVerticalStrut(5));
    loginPanel.add(usernameField);
    loginPanel.add(Box.createVerticalStrut(15));
    loginPanel.add(passwordLabel);
    loginPanel.add(Box.createVerticalStrut(5));
    loginPanel.add(passwordField);
    loginPanel.add(Box.createVerticalStrut(25));
    loginPanel.add(loginButton);
    loginPanel.add(Box.createVerticalStrut(15));

    // Button panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setOpaque(false);
    buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    buttonPanel.add(signupButton);
    buttonPanel.add(Box.createHorizontalStrut(20));
    buttonPanel.add(resetButton);
    loginPanel.add(buttonPanel);
    loginPanel.add(Box.createVerticalGlue());

    JPanel imagePanel = new JPanel();
    imagePanel.setOpaque(false);
    imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
    imagePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 50));

    JLabel imageLabel = new JLabel();
    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    boolean imageLoaded = false;
    String imagePath = "D:\\College JAVA\\swing\\src\\yrc logo.gif";

    try {
      File imageFile = new File(imagePath);
      System.out.println("Checking file at: " + imagePath);
      System.out.println("File exists: " + imageFile.exists());
      System.out.println("File size: " + (imageFile.exists() ? imageFile.length() + " bytes" : "N/A"));

      if (imageFile.exists()) {

        ImageIcon originalIcon = new ImageIcon(imagePath);
        System.out.println("Icon width: " + originalIcon.getIconWidth());
        System.out.println("Icon height: " + originalIcon.getIconHeight());

        if (originalIcon.getIconWidth() > 0) {

          imageLabel.setIcon(originalIcon);
          imageLoaded = true;
          System.out.println("GIF loaded successfully - displaying without scaling");
        }
      } else {

        String[] alternativePaths = {
            "./src/yrc logo.gif",
            "src/yrc logo.gif",
            "yrc logo.gif",
            "D:/College JAVA/swing/src/yrc logo.gif"
        };

        for (String altPath : alternativePaths) {
          System.out.println("Trying alternative path: " + altPath);
          File altFile = new File(altPath);
          if (altFile.exists()) {
            ImageIcon altIcon = new ImageIcon(altPath);
            if (altIcon.getIconWidth() > 0) {
              imageLabel.setIcon(altIcon);
              imageLoaded = true;
              System.out.println("GIF loaded successfully from alternative path: " + altPath);
              break;
            }
          }
        }
      }

      // Only add the image if it loaded successfully
      if (imageLoaded) {
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(Box.createVerticalGlue());
        imagePanel.add(imageLabel);
        imagePanel.add(Box.createVerticalGlue());
      }
    } catch (Exception ex) {
      System.err.println("Error loading GIF: " + ex.getMessage());
      ex.printStackTrace();
      imageLoaded = false;
    }

    if (!imageLoaded) {
      JPanel placeholderPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          Graphics2D g2d = (Graphics2D) g;
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

          g2d.setColor(Color.RED);
          g2d.fillRect(getWidth() / 2 - 40, getHeight() / 2 - 10, 80, 20);
          g2d.fillRect(getWidth() / 2 - 10, getHeight() / 2 - 40, 20, 80);
        }
      };
      placeholderPanel.setPreferredSize(new Dimension(300, 300));
      placeholderPanel.setOpaque(false);

      JLabel placeholderLabel = new JLabel("Hospital Logo");
      placeholderLabel.setForeground(Color.WHITE);
      placeholderLabel.setFont(new Font("Arial", Font.BOLD, 18));
      placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      imagePanel.add(Box.createVerticalGlue());
      imagePanel.add(placeholderPanel);
      imagePanel.add(Box.createVerticalStrut(10));
      imagePanel.add(placeholderLabel);
      imagePanel.add(Box.createVerticalGlue());

      System.out.println("Using placeholder instead of GIF");
    }

    contentPanel.add(loginPanel);
    contentPanel.add(imagePanel);

    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    loginFrame.add(mainPanel);

    loginButton.addActionListener(e -> {
      String username = usernameField.getText().trim();
      String password = new String(passwordField.getPassword());

      if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(loginFrame, "Please enter username and password.", "Input Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
          JOptionPane.showMessageDialog(loginFrame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
          loginFrame.dispose();
          showMainPage();
        } else {
          JOptionPane.showMessageDialog(loginFrame, "Invalid username or password!", "Login Error",
              JOptionPane.ERROR_MESSAGE);
        }
        rs.close();
        stmt.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(loginFrame, "Database Error: " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    signupButton.addActionListener(e -> {
      loginFrame.dispose();
      showSignupPage();
    });

    resetButton.addActionListener(e -> {
      loginFrame.dispose();
      showResetPasswordPage();
    });

    loginFrame.setVisible(true);
  }

  private static void showSignupPage() {
    JFrame signupFrame = new JFrame("Hospital Management System - Sign Up");
    signupFrame.setSize(800, 600);
    signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    signupFrame.setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(170, 35, 25), w, h, new Color(210, 60, 50));

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
      }
    };
    mainPanel.setLayout(new BorderLayout(20, 20));

    JPanel headerPanel = new JPanel();
    headerPanel.setOpaque(false);
    JLabel titleLabel = new JLabel("Sign Up");
    titleLabel.setFont(HEADER_FONT);
    titleLabel.setForeground(Color.WHITE);
    headerPanel.add(titleLabel);

    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setOpaque(false);
    formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    JTextField usernameField = createStyledTextField();
    JPasswordField passwordField = createStyledPasswordField();
    JTextField emailField = createStyledTextField();
    JButton signupButton = createStyledButton("Sign Up", TERTIARY_COLOR, SECONDARY_COLOR);
    JButton backButton = createStyledButton("Back to Login", SECONDARY_COLOR, PRIMARY_COLOR);

    JLabel usernameLabel = new JLabel("Username");
    JLabel passwordLabel = new JLabel("Password");
    JLabel emailLabel = new JLabel("Email");

    usernameLabel.setFont(LABEL_FONT);
    passwordLabel.setFont(LABEL_FONT);
    emailLabel.setFont(LABEL_FONT);
    usernameLabel.setForeground(Color.WHITE);
    passwordLabel.setForeground(Color.WHITE);
    emailLabel.setForeground(Color.WHITE);

    usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
    passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
    emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
    signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    formPanel.add(Box.createVerticalGlue());
    formPanel.add(usernameLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(usernameField);
    formPanel.add(Box.createVerticalStrut(15));
    formPanel.add(passwordLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(passwordField);
    formPanel.add(Box.createVerticalStrut(15));
    formPanel.add(emailLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(emailField);
    formPanel.add(Box.createVerticalStrut(25));
    formPanel.add(signupButton);
    formPanel.add(Box.createVerticalStrut(10));
    formPanel.add(backButton);
    formPanel.add(Box.createVerticalGlue());

    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(formPanel, BorderLayout.CENTER);

    signupFrame.add(mainPanel);

    signupButton.addActionListener(e -> {
      String username = usernameField.getText().trim();
      String password = new String(passwordField.getPassword());
      String email = emailField.getText().trim();

      if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
        JOptionPane.showMessageDialog(signupFrame, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        PreparedStatement checkStmt = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
        checkStmt.setString(1, username);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
          JOptionPane.showMessageDialog(signupFrame, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        rs.close();
        checkStmt.close();

        PreparedStatement insertStmt = conn
            .prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
        insertStmt.setString(1, username);
        insertStmt.setString(2, password);
        insertStmt.setString(3, email);

        insertStmt.executeUpdate();
        insertStmt.close();

        JOptionPane.showMessageDialog(signupFrame, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        signupFrame.dispose();
        showLoginPage();

      } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(signupFrame, "Database Error: " + ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    backButton.addActionListener(e -> {
      signupFrame.dispose();
      showLoginPage();
    });

    signupFrame.setVisible(true);
  }

  private static void showResetPasswordPage() {
    JFrame resetFrame = new JFrame("Hospital Management System - Reset Password");
    resetFrame.setSize(800, 600);
    resetFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    resetFrame.setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, new Color(170, 35, 25), w, h, new Color(210, 60, 50));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
      }
    };
    mainPanel.setLayout(new BorderLayout(20, 20));

    JPanel headerPanel = new JPanel();
    headerPanel.setOpaque(false);
    JLabel titleLabel = new JLabel("Reset Password");
    titleLabel.setFont(HEADER_FONT);
    titleLabel.setForeground(Color.WHITE);
    headerPanel.add(titleLabel);

    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setOpaque(false);
    formPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

    JTextField usernameField = createStyledTextField();
    JTextField emailField = createStyledTextField();
    JPasswordField newPasswordField = createStyledPasswordField();
    JButton resetButton = createStyledButton("Reset Password", TERTIARY_COLOR, SECONDARY_COLOR);
    JButton backButton = createStyledButton("Back to Login", SECONDARY_COLOR, PRIMARY_COLOR);

    JLabel usernameLabel = new JLabel("Username");
    JLabel emailLabel = new JLabel("Email");
    JLabel newPasswordLabel = new JLabel("New Password");

    usernameLabel.setFont(LABEL_FONT);
    emailLabel.setFont(LABEL_FONT);
    newPasswordLabel.setFont(LABEL_FONT);
    usernameLabel.setForeground(Color.WHITE);
    emailLabel.setForeground(Color.WHITE);
    newPasswordLabel.setForeground(Color.WHITE);

    usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    newPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
    emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
    newPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
    resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    formPanel.add(Box.createVerticalGlue());
    formPanel.add(usernameLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(usernameField);
    formPanel.add(Box.createVerticalStrut(15));
    formPanel.add(emailLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(emailField);
    formPanel.add(Box.createVerticalStrut(15));
    formPanel.add(newPasswordLabel);
    formPanel.add(Box.createVerticalStrut(5));
    formPanel.add(newPasswordField);
    formPanel.add(Box.createVerticalStrut(25));
    formPanel.add(resetButton);
    formPanel.add(Box.createVerticalStrut(10));
    formPanel.add(backButton);
    formPanel.add(Box.createVerticalGlue());

    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(formPanel, BorderLayout.CENTER);

    resetFrame.add(mainPanel);

    resetButton.addActionListener(e -> {
      String username = usernameField.getText();
      String email = emailField.getText();
      String newPassword = new String(newPasswordField.getPassword());

      if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
        JOptionPane.showMessageDialog(resetFrame, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      try {
        PreparedStatement stmt = conn
            .prepareStatement("UPDATE users SET password = ? WHERE username = ? AND email = ?");
        stmt.setString(1, newPassword);
        stmt.setString(2, username);
        stmt.setString(3, email);

        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated > 0) {
          JOptionPane.showMessageDialog(resetFrame, "Password reset successful!", "Success",
              JOptionPane.INFORMATION_MESSAGE);
          resetFrame.dispose();
          showLoginPage();
        } else {
          JOptionPane.showMessageDialog(resetFrame, "Invalid username or email.", "Reset Error",
              JOptionPane.ERROR_MESSAGE);
        }
        stmt.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(resetFrame, "Error: " + ex.getMessage(), "Database Error",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    backButton.addActionListener(e -> {
      resetFrame.dispose();
      showLoginPage();
    });

    resetFrame.setVisible(true);
  }

  private static final Color ACCENT_COLOR = new Color(255, 60, 40);
  private static final Color TEXT_COLOR = new Color(44, 62, 80);
  private static final Color BG_COLOR = new Color(245, 247, 250);

  private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 32);
  private static final Font TAB_FONT = new Font("Arial", Font.BOLD, 14);
  private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);

  private static void showMainPage() {
    mainFrame = new JFrame("Hospital Management System");
    mainFrame.setSize(1200, 800);
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setLocationRelativeTo(null);
    mainFrame.setBackground(BG_COLOR);

    JTabbedPane tabbedPane = new JTabbedPane() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
      }
    };

    tabbedPane.setFont(TAB_FONT);
    tabbedPane.setForeground(TEXT_COLOR);
    tabbedPane.setBackground(TERTIARY_COLOR);
    tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    tabbedPane.addTab("Dashboard", createStyledPanel(createDashboardTab()));
    tabbedPane.addTab("Patients", createStyledPanel(createPatientManagementPanel()));
    tabbedPane.addTab("Doctors", createStyledPanel(createDoctorManagementPanel()));
    tabbedPane.addTab("Billing", createStyledPanel(createBillingManagementPanel()));
    tabbedPane.addTab("Appointments", createStyledPanel(createAppointmentManagementPanel()));
    tabbedPane.addTab("Appointment Letter Generation", createStyledPanel(createAppointmentLetterPanel()));

    tabbedPane.setIconAt(0, createTabIcon("\u2302")); // Home icon
    tabbedPane.setIconAt(1, createTabIcon("\u2764")); // Heart icon
    tabbedPane.setIconAt(2, createTabIcon("\u2695")); // Medical icon
    tabbedPane.setIconAt(3, createTabIcon("\u0024")); // Dollar icon
    tabbedPane.setIconAt(4, createTabIcon("\u23F2")); // Clock icon
    tabbedPane.setIconAt(5, createTabIcon("\u2B1C")); // QR icon

    JButton logoutButton = createStyledButton("Logout", TERTIARY_COLOR, SECONDARY_COLOR);

    logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        logoutButton.setBackground(TERTIARY_COLOR.brighter()); // Brighter effect on hover
      }

      @Override
      public void mouseExited(java.awt.event.MouseEvent evt) {
        logoutButton.setBackground(TERTIARY_COLOR);
      }
    });

    logoutButton.addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(mainFrame,
          "Are you sure you want to log out?", "Confirm Logout",
          JOptionPane.YES_NO_OPTION);

      if (confirm == JOptionPane.YES_OPTION) {
        mainFrame.dispose();
        showLoginPage();
      }
    });

    JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    logoutPanel.setOpaque(false);
    logoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    logoutPanel.add(logoutButton);

    mainFrame.setLayout(new BorderLayout());
    mainFrame.add(tabbedPane, BorderLayout.CENTER);
    mainFrame.add(logoutPanel, BorderLayout.SOUTH); // Bottom-right position

    mainFrame.setVisible(true);
  }

  private static Icon createTabIcon(String unicode) {
    return new Icon() {
      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        g2.setColor(PRIMARY_COLOR);
        g2.drawString(unicode, x, y + 16);
        g2.dispose();
      }

      @Override
      public int getIconWidth() {
        return 20;
      }

      @Override
      public int getIconHeight() {
        return 20;
      }
    };
  }

  private static JPanel createStyledPanel(JPanel contentPanel) {
    contentPanel.setBackground(BG_COLOR);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    return contentPanel;
  }

  private static JLabel totalPatientsLabel = new JLabel();
  private static JLabel totalDoctorsLabel = new JLabel();
  private static JLabel appointmentsTodayLabel = new JLabel();
  private static JLabel revenueLabel = new JLabel();
  private static JLabel imageLabel = new JLabel();

  private static JPanel createDashboardTab() {
    JPanel dashboardPanel = new JPanel(new BorderLayout(20, 20));
    dashboardPanel.setBackground(BG_COLOR);

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);

    JLabel welcomeLabel = new JLabel("Welcome to Hospital Management System");
    welcomeLabel.setFont(TITLE_FONT);
    welcomeLabel.setForeground(PRIMARY_COLOR);
    welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    headerPanel.add(welcomeLabel, BorderLayout.CENTER);

    JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
    statsPanel.setOpaque(false);

    statsPanel.add(createStatCard("Total Patients", totalPatientsLabel, "\u2764"));
    statsPanel.add(createStatCard("Doctors", totalDoctorsLabel, "\u2695"));
    statsPanel.add(createStatCard("Appointments Today", appointmentsTodayLabel, "\u23F2"));
    statsPanel.add(createStatCard("Revenue", revenueLabel, null));

    headerPanel.add(statsPanel, BorderLayout.SOUTH);

    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setOpaque(false);

    try {
      String imagePath = "D:\\College JAVA\\swing\\src\\hospital_red.png";
      File imageFile = new File(imagePath);

      if (imageFile.exists()) {
        BufferedImage originalImage = ImageIO.read(imageFile);
        java.awt.Image scaledImage = originalImage.getScaledInstance(800, -1, java.awt.Image.SCALE_SMOOTH);

        ImageIcon imageIcon = new ImageIcon(scaledImage);

        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 2));

        contentPanel.add(imageLabel, BorderLayout.CENTER);
      }
    } catch (IOException e) {
      e.printStackTrace();
      JLabel errorLabel = new JLabel("Error loading image", JLabel.CENTER);
      errorLabel.setForeground(Color.RED);
      contentPanel.add(errorLabel, BorderLayout.CENTER);
    }

    Timer timer = new Timer(5000, e -> updateDashboardStats());
    timer.start();
    updateDashboardStats();

    dashboardPanel.add(headerPanel, BorderLayout.NORTH);
    dashboardPanel.add(contentPanel, BorderLayout.CENTER);

    return dashboardPanel;
  }

  private static JPanel createStatCard(String title, JLabel valueLabel, String icon) {
    JPanel card = new JPanel(new BorderLayout(10, 5));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(SECONDARY_COLOR, 1, true),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));

    JLabel iconLabel = new JLabel(icon);
    iconLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
    iconLabel.setForeground(PRIMARY_COLOR);

    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(NORMAL_FONT);
    titleLabel.setForeground(TEXT_COLOR);

    valueLabel.setFont(HEADER_FONT);
    valueLabel.setForeground(ACCENT_COLOR);

    JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    textPanel.setOpaque(false);
    textPanel.add(titleLabel);
    textPanel.add(valueLabel);

    card.add(iconLabel, BorderLayout.WEST);
    card.add(textPanel, BorderLayout.CENTER);
    return card;
  }

  private static void updateDashboardStats() {
    totalPatientsLabel.setText(String.valueOf(fetchTotalPatients()));
    totalDoctorsLabel.setText(String.valueOf(fetchTotalDoctors()));
    appointmentsTodayLabel.setText(String.valueOf(fetchAppointmentsToday()));
    revenueLabel.setText("₹" + fetchTotalRevenue());
  }

  private static int fetchTotalPatients() {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM patients")) {
      return rs.next() ? rs.getInt(1) : 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  private static int fetchTotalDoctors() {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM doctors")) {
      return rs.next() ? rs.getInt(1) : 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  private static int fetchAppointmentsToday() {
    try (PreparedStatement stmt = conn.prepareStatement(
        "SELECT COUNT(*) FROM appointments WHERE date = DATE('now')")) {
      ResultSet rs = stmt.executeQuery();
      return rs.next() ? rs.getInt(1) : 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  private static double fetchTotalRevenue() {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT SUM(amount) FROM billing")) {
      return rs.next() ? rs.getDouble(1) : 0.0;
    } catch (SQLException e) {
      e.printStackTrace();
      return 0.0;
    }
  }

  public static JPanel createPatientManagementPanel() {

    connectToDatabase();

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);

    DefaultTableModel model = new DefaultTableModel(
        new String[] { "ID", "Name", "Age", "Gender", "Address", "Phone Number", "Email" }, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 15));
    table.setRowHeight(25);
    table.setGridColor(new Color(200, 200, 200));
    table.setShowGrid(true);

    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 16));
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(ACCENT_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
      }
    });

    JScrollPane scrollPane = new JScrollPane(table);

    JButton addButton = new JButton("➕ Add Patient");
    JButton updateButton = new JButton("✏ Update Patient");
    JButton deleteButton = new JButton("🗑 Delete Patient");

    Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
    addButton.setFont(emojiFont);
    updateButton.setFont(emojiFont);
    deleteButton.setFont(emojiFont);

    styleButton(addButton);
    styleButton(updateButton);
    styleButton(deleteButton);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(addButton);
    buttonPanel.add(updateButton);
    buttonPanel.add(deleteButton);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    loadPatientData(model);

    addButton.addActionListener(e -> addPatient(model));

    updateButton.addActionListener(e -> updatePatient(table, model));

    deleteButton.addActionListener(e -> deletePatient(table, model));

    return panel;
  }

  private static void styleButton(JButton button) {
    button.setFont(new Font("Arial", Font.BOLD, 14));
    button.setBackground(ACCENT_COLOR);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setOpaque(true);
    button.setBorderPainted(false);
    button.setContentAreaFilled(true);
    button.setRolloverEnabled(true);
    button.setRolloverIcon(null);
    button.setPressedIcon(null);
    button.setSelectedIcon(null);
    button.setFocusable(false);
    button.setMargin(new Insets(10, 15, 10, 15));
  }

  private static void addPatient(DefaultTableModel model) {
    JTextField nameField = new JTextField();
    JTextField ageField = new JTextField();
    JComboBox<String> genderField = new JComboBox<>(new String[] { "Male", "Female" });
    JTextField addressField = new JTextField();
    JTextField phoneNumberField = new JTextField();
    JTextField emailField = new JTextField();

    Object[] message = {
        "Name:", nameField,
        "Age:", ageField,
        "Gender:", genderField,
        "Address:", addressField,
        "Phone Number:", phoneNumberField,
        "Email:", emailField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Add Patient", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      try {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String gender = (String) genderField.getSelectedItem();
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();

        if (!phoneNumber.matches("\\d{10}")) {
          JOptionPane.showMessageDialog(null, "Phone number must be exactly 10 digits.", "Validation Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (!email.contains("@")) {
          JOptionPane.showMessageDialog(null, "Email must contain the @ symbol.", "Validation Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        PreparedStatement checkPhoneStmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM patients WHERE phone_number = ?");
        checkPhoneStmt.setString(1, phoneNumber);
        ResultSet phoneRs = checkPhoneStmt.executeQuery();
        if (phoneRs.next() && phoneRs.getInt(1) > 0) {
          JOptionPane.showMessageDialog(null, "A patient with this phone number already exists.", "Duplicate Error",
              JOptionPane.ERROR_MESSAGE);
          checkPhoneStmt.close();
          return;
        }
        checkPhoneStmt.close();

        PreparedStatement checkEmailStmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM patients WHERE email = ?");
        checkEmailStmt.setString(1, email);
        ResultSet emailRs = checkEmailStmt.executeQuery();
        if (emailRs.next() && emailRs.getInt(1) > 0) {
          JOptionPane.showMessageDialog(null, "A patient with this email already exists.", "Duplicate Error",
              JOptionPane.ERROR_MESSAGE);
          checkEmailStmt.close();
          return;
        }
        checkEmailStmt.close();

        PreparedStatement stmt = conn
            .prepareStatement(
                "INSERT INTO patients (name, age, gender, address, phone_number, email) VALUES (?, ?, ?, ?, ?, ?)");
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.setString(3, gender);
        stmt.setString(4, address);
        stmt.setString(5, phoneNumber);
        stmt.setString(6, email);
        stmt.executeUpdate();
        stmt.close();

        loadPatientData(model);
      } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static void updatePatient(JTable table, DefaultTableModel model) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(null, "Please select a patient to update.", "Selection Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    int id = (int) model.getValueAt(selectedRow, 0);
    JTextField nameField = new JTextField((String) model.getValueAt(selectedRow, 1));
    JTextField ageField = new JTextField(String.valueOf(model.getValueAt(selectedRow, 2)));
    JComboBox<String> genderField = new JComboBox<>(new String[] { "Male", "Female" });
    genderField.setSelectedItem((String) model.getValueAt(selectedRow, 3));
    JTextField addressField = new JTextField((String) model.getValueAt(selectedRow, 4));
    JTextField phoneNumberField = new JTextField((String) model.getValueAt(selectedRow, 5));
    JTextField emailField = new JTextField((String) model.getValueAt(selectedRow, 6));

    Object[] message = {
        "Name:", nameField,
        "Age:", ageField,
        "Gender:", genderField,
        "Address:", addressField,
        "Phone Number:", phoneNumberField,
        "Email:", emailField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Update Patient", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      try {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String gender = (String) genderField.getSelectedItem();
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();

        if (!phoneNumber.matches("\\d{10}")) {
          JOptionPane.showMessageDialog(null, "Phone number must be exactly 10 digits.", "Validation Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (!email.contains("@")) {
          JOptionPane.showMessageDialog(null, "Email must contain the @ symbol.", "Validation Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }

        PreparedStatement checkPhoneStmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM patients WHERE phone_number = ? AND id != ?");
        checkPhoneStmt.setString(1, phoneNumber);
        checkPhoneStmt.setInt(2, id);
        ResultSet phoneRs = checkPhoneStmt.executeQuery();
        if (phoneRs.next() && phoneRs.getInt(1) > 0) {
          JOptionPane.showMessageDialog(null, "A patient with this phone number already exists.", "Duplicate Error",
              JOptionPane.ERROR_MESSAGE);
          checkPhoneStmt.close();
          return;
        }
        checkPhoneStmt.close();

        PreparedStatement checkEmailStmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM patients WHERE email = ? AND id != ?");
        checkEmailStmt.setString(1, email);
        checkEmailStmt.setInt(2, id);
        ResultSet emailRs = checkEmailStmt.executeQuery();
        if (emailRs.next() && emailRs.getInt(1) > 0) {
          JOptionPane.showMessageDialog(null, "A patient with this email already exists.", "Duplicate Error",
              JOptionPane.ERROR_MESSAGE);
          checkEmailStmt.close();
          return;
        }
        checkEmailStmt.close();

        PreparedStatement stmt = conn
            .prepareStatement(
                "UPDATE patients SET name = ?, age = ?, gender = ?, address = ?, phone_number = ?, email = ? WHERE id = ?");
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.setString(3, gender);
        stmt.setString(4, address);
        stmt.setString(5, phoneNumber);
        stmt.setString(6, email);
        stmt.setInt(7, id);
        stmt.executeUpdate();
        stmt.close();

        loadPatientData(model);
      } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static void deletePatient(JTable table, DefaultTableModel model) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
      JOptionPane.showMessageDialog(null, "Please select a patient to delete.", "Selection Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    int id = (int) model.getValueAt(selectedRow, 0);
    int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this patient?",
        "Delete Confirmation", JOptionPane.YES_NO_OPTION);
    if (option == JOptionPane.YES_OPTION) {
      try {
        PreparedStatement deleteAppointmentsStmt = conn.prepareStatement(
            "DELETE FROM appointments WHERE patient_id = ?");
        deleteAppointmentsStmt.setInt(1, id);
        deleteAppointmentsStmt.executeUpdate();
        deleteAppointmentsStmt.close();

        PreparedStatement deleteBillingStmt = conn.prepareStatement(
            "DELETE FROM billing WHERE patient_id = ?");
        deleteBillingStmt.setInt(1, id);
        deleteBillingStmt.executeUpdate();
        deleteBillingStmt.close();

        PreparedStatement deleteStmt = conn.prepareStatement(
            "DELETE FROM patients WHERE id = ?");
        deleteStmt.setInt(1, id);
        deleteStmt.executeUpdate();
        deleteStmt.close();

        reassignPatientIds(id);

        loadPatientData(model);
      } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static void reassignPatientIds(int deletedId) {
    try {
      PreparedStatement updateStmt = conn.prepareStatement(
          "UPDATE patients SET id = id - 1 WHERE id > ?");
      updateStmt.setInt(1, deletedId);
      updateStmt.executeUpdate();
      updateStmt.close();

      PreparedStatement updateAppointmentsStmt = conn.prepareStatement(
          "UPDATE appointments SET patient_id = patient_id - 1 WHERE patient_id > ?");
      updateAppointmentsStmt.setInt(1, deletedId);
      updateAppointmentsStmt.executeUpdate();
      updateAppointmentsStmt.close();

      PreparedStatement updateBillingStmt = conn.prepareStatement(
          "UPDATE billing SET patient_id = patient_id - 1 WHERE patient_id > ?");
      updateBillingStmt.setInt(1, deletedId);
      updateBillingStmt.executeUpdate();
      updateBillingStmt.close();

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM patients");
      int maxId = 0;
      if (rs.next()) {
        maxId = rs.getInt(1);
      }
      rs.close();

      stmt.executeUpdate("UPDATE sqlite_sequence SET seq = " + maxId + " WHERE name = 'patients'");
      stmt.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null, "Error reassigning IDs: " + ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private static void loadPatientData(DefaultTableModel model) {
    model.setRowCount(0);
    try {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM patients");

      while (rs.next()) {
        model.addRow(new Object[] {
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("address"),
            rs.getString("phone_number"),
            rs.getString("email")
        });
      }
      stmt.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private static void connectToDatabase() {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:hospital.db");
      Statement stmt = conn.createStatement();
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS patients (" +
              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "name TEXT, " +
              "age INTEGER, " +
              "gender TEXT, " +
              "address TEXT, " +
              "phone_number TEXT UNIQUE, " +
              "email TEXT UNIQUE)");
      stmt.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public static JPanel createDoctorManagementPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);

    DefaultTableModel model = new DefaultTableModel(new String[] { "ID", "Name", "Specialty" }, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 15));
    table.setRowHeight(25);
    table.setGridColor(new Color(200, 200, 200));
    table.setShowGrid(true);

    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 16));

    JScrollPane scrollPane = new JScrollPane(table);

    JButton addButton = new JButton("➕ Add Doctor");
    JButton deleteButton = new JButton("🗑 Delete Doctor");

    styleButton(addButton);
    styleButton(deleteButton);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    loadDoctorData(model);

    JTableHeader header2 = table.getTableHeader();
    header2.setFont(new Font("Arial", Font.BOLD, 16));
    header2.setDefaultRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(ACCENT_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
      }
    });

    addButton.addActionListener(e -> {
      JTextField nameField = new JTextField();
      JTextField specialtyField = new JTextField();

      Object[] message = {
          "Name:", nameField,
          "Specialty:", specialtyField
      };

      int option = JOptionPane.showConfirmDialog(null, message, "Add Doctor", JOptionPane.OK_CANCEL_OPTION);
      if (option == JOptionPane.OK_OPTION) {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO doctors (name, specialty) VALUES (?, ?)")) {
          stmt.setString(1, nameField.getText());
          stmt.setString(2, specialtyField.getText());
          stmt.executeUpdate();
          loadDoctorData(model);
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    deleteButton.addActionListener(e -> {
      int selectedRow = table.getSelectedRow();
      if (selectedRow == -1) {
        JOptionPane.showMessageDialog(panel, "Please select a doctor to delete.", "Selection Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      int id = (int) model.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this doctor?",
          "Delete Confirmation", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        try {
          PreparedStatement stmt = conn.prepareStatement("DELETE FROM doctors WHERE id = ?");
          stmt.setInt(1, id);
          stmt.executeUpdate();
          stmt.close();

          resetDoctorIDs();

          loadDoctorData(model);
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    return panel;
  }

  private static void loadDoctorData(DefaultTableModel model) {
    model.setRowCount(0);
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM doctors")) {
      while (rs.next()) {
        model.addRow(new Object[] { rs.getInt("id"), rs.getString("name"), rs.getString("specialty") });
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private static void resetDoctorIDs() {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("CREATE TABLE temp_doctors AS SELECT * FROM doctors ORDER BY id ASC;");
      stmt.execute("DROP TABLE doctors;");
      stmt.execute("CREATE TABLE doctors (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, specialty TEXT);");
      stmt.execute("INSERT INTO doctors (name, specialty) SELECT name, specialty FROM temp_doctors;");
      stmt.execute("DROP TABLE temp_doctors;");
      stmt.execute("DELETE FROM sqlite_sequence WHERE name='doctors';");
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private static JPanel createBillingManagementPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel(new String[] { "ID", "Patient ID", "Amount", "Date" }, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 15));
    JScrollPane scrollPane = new JScrollPane(table);

    JButton addButton = new JButton("Add Billing");
    JButton deleteButton = new JButton("Delete Billing");
    styleButton(addButton);
    styleButton(deleteButton);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 16));
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(ACCENT_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
      }
    });

    loadBillingData(model);

    addButton.addActionListener(e -> {
      JTextField patientIdField = new JTextField();
      JTextField amountField = new JTextField();
      JDateChooser dateChooser = new JDateChooser();
      dateChooser.setDateFormatString("yyyy-MM-dd");

      Object[] message = {
          "Patient ID:", patientIdField,
          "Amount:", amountField,
          "Date:", dateChooser
      };

      int option = JOptionPane.showConfirmDialog(null, message, "Add Billing", JOptionPane.OK_CANCEL_OPTION);
      if (option == JOptionPane.OK_OPTION) {
        try {
          int patientId = Integer.parseInt(patientIdField.getText().trim());
          double amount = Double.parseDouble(amountField.getText().trim());
          Date selectedDate = dateChooser.getDate();

          if (selectedDate == null) {
            JOptionPane.showMessageDialog(panel, "Please select a valid date!", "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          Date today = new Date();
          if (selectedDate.after(today)) {
            JOptionPane.showMessageDialog(panel, "Billing date cannot be in the future!", "Input Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          String date = sdf.format(selectedDate);

          if (!doesPatientExist(patientId)) {
            JOptionPane.showMessageDialog(panel, "No patient found with ID: " + patientId, "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (doesBillingExist(patientId)) {
            JOptionPane.showMessageDialog(panel, "Billing already exists for Patient ID: " + patientId, "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
          int newId = getNextBillingId();

          PreparedStatement stmt = conn
              .prepareStatement("INSERT INTO billing (id, patient_id, amount, date) VALUES (?, ?, ?, ?)");
          stmt.setInt(1, newId);
          stmt.setInt(2, patientId);
          stmt.setDouble(3, amount);
          stmt.setString(4, date);
          stmt.executeUpdate();
          stmt.close();

          loadBillingData(model);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    deleteButton.addActionListener(e -> {
      int selectedRow = table.getSelectedRow();
      if (selectedRow == -1) {
        JOptionPane.showMessageDialog(panel, "Please select a billing record to delete.", "Selection Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      int id = (int) model.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this billing record?",
          "Delete Confirmation", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM billing WHERE id = ?")) {
          stmt.setInt(1, id);
          stmt.executeUpdate();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        resetBillingIds();
        loadBillingData(model);
      }
    });

    return panel;
  }

  private static void resetBillingIds() {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id FROM billing ORDER BY id ASC")) {
      int newId = 1;
      while (rs.next()) {
        int currentId = rs.getInt("id");
        if (currentId != newId) {
          try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE billing SET id = ? WHERE id = ?")) {
            updateStmt.setInt(1, newId);
            updateStmt.setInt(2, currentId);
            updateStmt.executeUpdate();
          }
        }
        newId++;
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private static boolean doesPatientExist(int patientId) {
    try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM patients WHERE id = ?")) {
      stmt.setInt(1, patientId);
      ResultSet rs = stmt.executeQuery();
      return rs.next();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private static boolean doesBillingExist(int patientId) {
    try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM billing WHERE patient_id = ?")) {
      stmt.setInt(1, patientId);
      ResultSet rs = stmt.executeQuery();
      return rs.next();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private static int getNextBillingId() {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id FROM billing ORDER BY id ASC")) {
      int expectedId = 1;
      while (rs.next()) {
        int currentId = rs.getInt("id");
        if (currentId != expectedId) {
          return expectedId;
        }
        expectedId++;
      }
      return expectedId;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return -1;
    }
  }

  private static void loadBillingData(DefaultTableModel model) {
    model.setRowCount(0);
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM billing ORDER BY id ASC")) {
      while (rs.next()) {
        model.addRow(new Object[] {
            rs.getInt("id"),
            rs.getInt("patient_id"),
            rs.getDouble("amount"),
            rs.getString("date")
        });
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private static JPanel createAppointmentManagementPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    DefaultTableModel model = new DefaultTableModel(new String[] { "ID", "Patient ID", "Doctor ID", "Date", "Time" },
        0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 15));
    JScrollPane scrollPane = new JScrollPane(table);

    JButton addButton = new JButton("Add Appointment");
    JButton deleteButton = new JButton("Delete Appointment");
    styleButton(addButton);
    styleButton(deleteButton);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);

    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 16));
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(ACCENT_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
      }
    });

    loadAppointmentData(model);

    addButton.addActionListener(e -> {
      JTextField patientIdField = new JTextField();
      JTextField doctorIdField = new JTextField();

      JComboBox<String> dateDropdown = new JComboBox<>(getNextSevenDays());

      JComboBox<String> timeDropdown = new JComboBox<>(new String[] {
          "09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00"
      });

      Object[] message = {
          "Patient ID:", patientIdField,
          "Doctor ID:", doctorIdField,
          "Date:", dateDropdown,
          "Time:", timeDropdown
      };

      int option = JOptionPane.showConfirmDialog(null, message, "Add Appointment", JOptionPane.OK_CANCEL_OPTION);
      if (option == JOptionPane.OK_OPTION) {
        try {
          int patientId = Integer.parseInt(patientIdField.getText());
          int doctorId = Integer.parseInt(doctorIdField.getText());

          String date = (String) dateDropdown.getSelectedItem();
          String time = (String) timeDropdown.getSelectedItem();

          if (!isPatientInDatabase(patientId)) {
            JOptionPane.showMessageDialog(panel, "No patient found with ID: " + patientId, "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }
          if (!isDoctorInDatabase(doctorId)) {
            JOptionPane.showMessageDialog(panel, "No doctor found with ID: " + doctorId, "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          if (isDuplicateAppointment(patientId, doctorId, date, time)) {
            JOptionPane.showMessageDialog(panel,
                "Appointment already exists for this patient and doctor at the same time.", "Duplicate Entry",
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          try (PreparedStatement stmt = conn
              .prepareStatement("INSERT INTO appointments (patient_id, doctor_id, date, time) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.executeUpdate();
            loadAppointmentData(model);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    deleteButton.addActionListener(e -> {
      int selectedRow = table.getSelectedRow();
      if (selectedRow == -1) {
        JOptionPane.showMessageDialog(panel, "Please select an appointment to delete.", "Selection Error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }

      int id = (int) model.getValueAt(selectedRow, 0);
      int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?",
          "Delete Confirmation", JOptionPane.YES_NO_OPTION);
      if (option == JOptionPane.YES_OPTION) {
        try {
          PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointments WHERE id = ?");
          stmt.setInt(1, id);
          stmt.executeUpdate();
          stmt.close();
          Statement resetStmt = conn.createStatement();

          resetStmt.execute("DROP TABLE IF EXISTS temp_appointments;");

          resetStmt.execute("CREATE TABLE temp_appointments AS SELECT * FROM appointments ORDER BY id ASC;");
          resetStmt.execute("DROP TABLE appointments;");
          resetStmt.execute(
              "CREATE TABLE appointments (id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER, doctor_id INTEGER, date TEXT, time TEXT);");
          resetStmt.execute(
              "INSERT INTO appointments (patient_id, doctor_id, date, time) SELECT patient_id, doctor_id, date, time FROM temp_appointments;");
          resetStmt.execute("DROP TABLE temp_appointments;");
          resetStmt.close();

          Statement resetAutoInc = conn.createStatement();
          resetAutoInc.execute("DELETE FROM sqlite_sequence WHERE name='appointments';");
          resetAutoInc.close();

          loadAppointmentData(model);
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    return panel;
  }

  private static void loadAppointmentData(DefaultTableModel model) {
    model.setRowCount(0);
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM appointments")) {
      while (rs.next()) {
        model.addRow(new Object[] { rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
            rs.getString("date"), rs.getString("time") });
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  private static String[] getNextSevenDays() {
    Vector<String> dates = new Vector<>();
    for (int i = 0; i < 7; i++) {
      dates.add(java.time.LocalDate.now().plusDays(i).toString());
    }
    return dates.toArray(new String[0]);
  }

  private static boolean isPatientInDatabase(int patientId) {
    try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM patients WHERE id = ?")) {
      stmt.setInt(1, patientId);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private static boolean isDoctorInDatabase(int doctorId) {
    try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM doctors WHERE id = ?")) {
      stmt.setInt(1, doctorId);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private static boolean isDuplicateAppointment(int patientId, int doctorId, String date, String time) {
    try (PreparedStatement stmt = conn.prepareStatement(
        "SELECT id FROM appointments WHERE patient_id = ? AND doctor_id = ? AND date = ? AND time = ?")) {
      stmt.setInt(1, patientId);
      stmt.setInt(2, doctorId);
      stmt.setString(3, date);
      stmt.setString(4, time);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private static JPanel createAppointmentLetterPanel() {
    JPanel panel = new JPanel(new BorderLayout());

    DefaultTableModel model = new DefaultTableModel(
        new String[] { "Appointment ID", "Patient", "Doctor", "Date", "Time" }, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", Font.PLAIN, 15));
    JScrollPane scrollPane = new JScrollPane(table);

    loadAppointmentsToTable(model);

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JButton generatePDFButton = new JButton("Generate PDF");
    JButton shareButton = new JButton("Share PDF");
    styleButton(generatePDFButton);
    styleButton(shareButton);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(generatePDFButton);
    buttonPanel.add(shareButton);

    JPanel mainContent = new JPanel(new BorderLayout());
    mainContent.add(scrollPane, BorderLayout.CENTER);

    panel.add(mainContent, BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    JTableHeader header = table.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 16));
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
          int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBackground(ACCENT_COLOR);
        setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return this;
      }
    });

    generatePDFButton.addActionListener(e -> {
      int selectedRow = table.getSelectedRow();
      if (selectedRow == -1) {
        JOptionPane.showMessageDialog(panel, "Please select an appointment.",
            "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
      }

      try {
        int appointmentId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

        File pdfFile = generateAppointmentPDF(appointmentId);

        if (pdfFile != null && pdfFile.exists()) {
          Desktop.getDesktop().open(pdfFile);
        }

        JOptionPane.showMessageDialog(panel, "PDF generated for Appointment ID: " + appointmentId +
            "\nSaved at: " + pdfFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(panel, "Error generating or opening PDF: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    shareButton.addActionListener(e -> {
      int selectedRow = table.getSelectedRow();
      if (selectedRow == -1) {
        JOptionPane.showMessageDialog(panel, "Please select an appointment.",
            "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
      }

      try {
        int appointmentId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

        File pdfFile = new File("D:\\College JAVA\\swing\\src\\appointment_" + appointmentId + ".pdf");
        if (!pdfFile.exists()) {
          pdfFile = generateAppointmentPDF(appointmentId);
          if (pdfFile == null || !pdfFile.exists()) {
            JOptionPane.showMessageDialog(panel, "Failed to generate PDF for Appointment ID: " + appointmentId,
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
        }

        try {
          String subject = URLEncoder.encode("Appointment Letter - ID " + appointmentId, "UTF-8").replace("+", "%20");
          String body = URLEncoder
              .encode("Please find attached the appointment letter for Appointment ID: " + appointmentId, "UTF-8")
              .replace("+", "%20");
          String gmailComposeUrl = "https://mail.google.com/mail/?view=cm&fs=1&su=" + subject + "&body=" + body;
          URI gmailUri = new URI(gmailComposeUrl);
          Desktop.getDesktop().browse(gmailUri);

          Desktop.getDesktop().open(pdfFile);

          JOptionPane.showMessageDialog(panel, "Gmail compose page opened in your browser.\n" +
              "The PDF has been opened for you. Please attach the PDF to the email and choose the recipient.",
              "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {

          Desktop.getDesktop().open(pdfFile);
          JOptionPane.showMessageDialog(panel, "Could not open browser. PDF opened instead.\n" +
              "Please share the file manually using your preferred email client or app.",
              "Info", JOptionPane.INFORMATION_MESSAGE);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(panel, "Error sharing PDF: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    Timer timer = new Timer(5000, e -> loadAppointmentsToTable(model));
    timer.start();

    return panel;
  }

  private static void loadAppointmentsToTable(DefaultTableModel model) {

    model.setRowCount(0);

    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT a.id, a.patient_id, a.doctor_id, a.date, a.time, " +
                "p.name as patient_name, d.name as doctor_name " +
                "FROM appointments a " +
                "LEFT JOIN patients p ON a.patient_id = p.id " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.id ASC")) {

      while (rs.next()) {
        int id = rs.getInt("id");
        String patientName = rs.getString("patient_name");
        String doctorName = rs.getString("doctor_name");
        String date = rs.getString("date");
        String time = rs.getString("time");

        if (patientName == null)
          patientName = "Patient ID: " + rs.getInt("patient_id");
        if (doctorName == null)
          doctorName = "Doctor ID: " + rs.getInt("doctor_id");

        model.addRow(new Object[] { id, patientName, doctorName, date, time });
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      model.addRow(new Object[] { "", "Database connection error", "", "", "" });
    }
  }

  private static File generateAppointmentPDF(int appointmentId) {
    try {
      String query = "SELECT a.id, a.patient_id, a.doctor_id, a.date, a.time, p.name as patient_name, d.name as doctor_name "
          + "FROM appointments a "
          + "LEFT JOIN patients p ON a.patient_id = p.id "
          + "LEFT JOIN doctors d ON a.doctor_id = d.id "
          + "WHERE a.id = ?";
      PreparedStatement stmt = conn.prepareStatement(query);
      stmt.setInt(1, appointmentId);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        int id = rs.getInt("id");
        String patientName = rs.getString("patient_name");
        String doctorName = rs.getString("doctor_name");
        String date = rs.getString("date");
        String time = rs.getString("time");

        Document document = new Document();
        File pdfFile = new File("D:\\College JAVA\\swing\\src\\appointment_" + appointmentId + ".pdf");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 30,
            com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.RED);
        Paragraph title = new Paragraph("Appointment Letter", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(80);
        detailsTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailsTable.setSpacingBefore(20);

        addDetailRow(detailsTable, "Appointment ID", String.valueOf(id));
        addDetailRow(detailsTable, "Patient Name", patientName);
        addDetailRow(detailsTable, "Doctor Name", doctorName);
        addDetailRow(detailsTable, "Date", date);
        addDetailRow(detailsTable, "Time", time);

        document.add(detailsTable);

        com.itextpdf.text.Image background = com.itextpdf.text.Image
            .getInstance("D:\\College JAVA\\swing\\src\\hospital_red.png");
        background.scaleToFit(document.getPageSize().getWidth() - 100, document.getPageSize().getHeight() - 100);
        background.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
        background.setSpacingBefore(20);
        document.add(background);

        com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 28,
            com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.RED);
        Paragraph footer = new Paragraph("Thank you for choosing our hospital, " + patientName + "!", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(40);
        document.add(footer);

        document.close();

        return pdfFile;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  private static void addDetailRow(PdfPTable table, String label, String value) {
    com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14,
        com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.BLACK);
    com.itextpdf.text.Font valueFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14,
        com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.BLACK);

    PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
    labelCell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
    labelCell.setPadding(10);
    labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(labelCell);

    PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
    valueCell.setPadding(10);
    valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(valueCell);
  }
}