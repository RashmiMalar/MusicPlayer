package auth;

import dao.UserDAO;
import model.User;
import utils.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignUpFrame extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField;

    public SignUpFrame() {
        setTitle("SwingBeats - Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(25, 25, 35));

        JLabel title = new JLabel("🎵 SwingBeats - Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBounds(500, 50, 500, 40);
        add(title);

        JLabel nameLabel = createLabel("Name:", 450, 150);
        nameField = createTextField(550, 150);
        JLabel emailLabel = createLabel("Email:", 450, 220);
        emailField = createTextField(550, 220);
        JLabel passLabel = createLabel("Password:", 450, 290);
        passField = new JPasswordField();
        passField.setBounds(550, 290, 300, 30);
        add(passField);

        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.setBounds(550, 350, 120, 35);
        signUpBtn.setBackground(new Color(0, 150, 200));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFocusPainted(false);
        signUpBtn.addActionListener(this::handleSignUp);
        add(signUpBtn);

        JButton toLogin = new JButton("Already have an account?");
        toLogin.setBounds(550, 400, 200, 30);
        toLogin.setBackground(new Color(45, 45, 60));
        toLogin.setForeground(Color.LIGHT_GRAY);
        toLogin.setBorder(null);
        toLogin.addActionListener(e -> {
            dispose();
            new SignInFrame();
        });
        add(toLogin);

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

    private void handleSignUp(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = String.valueOf(passField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (UserDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already registered!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashed = PasswordUtil.hashPassword(pass);
        User user = new User(name, email, hashed);

        if (UserDAO.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "Account created! Please login.");
            dispose();
            new SignInFrame();
        } else {
            JOptionPane.showMessageDialog(this, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
