package auth;

import dao.UserDAO;
import model.User;
import utils.PasswordUtil;

import javax.swing.*;

import admin.AdminDashboard;

import java.awt.*;
import java.awt.event.ActionEvent;

public class SignInFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passField;

    public SignInFrame() {
        setTitle("SwingBeats - Sign In");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(20, 20, 30));

        JLabel title = new JLabel("🎵 Welcome to SwingBeats");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(500, 50, 500, 40);
        add(title);

        JLabel emailLabel = createLabel("Email:", 450, 150);
        emailField = createTextField(550, 150);
        JLabel passLabel = createLabel("Password:", 450, 220);
        passField = new JPasswordField();
        passField.setBounds(550, 220, 300, 30);
        add(passField);

        JButton loginBtn = new JButton("Sign In");
        loginBtn.setBounds(550, 280, 120, 35);
        loginBtn.setBackground(new Color(0, 150, 200));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(this::handleLogin);
        add(loginBtn);

        JButton toSignup = new JButton("New? Create an account");
        toSignup.setBounds(550, 330, 200, 30);
        toSignup.setBackground(new Color(45, 45, 60));
        toSignup.setForeground(Color.LIGHT_GRAY);
        toSignup.setBorder(null);
        toSignup.addActionListener(e -> {
            dispose();
            new SignUpFrame();
        });
        add(toSignup);

        setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        l.setBounds(x, y, 100, 30);
        add(l);
        return l;
    }

    private JTextField createTextField(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 300, 30);
        add(tf);
        return tf;
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText().trim();
        String pass = String.valueOf(passField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hashed = PasswordUtil.hashPassword(pass);
        User user = UserDAO.authenticate(email, hashed);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome " + user.getName() + "!");
            dispose();
            if (user.getRole().equals("admin")) {
            	new AdminDashboard(user);
            } else {
            	new dashboard.UserDashboard(user);

            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
