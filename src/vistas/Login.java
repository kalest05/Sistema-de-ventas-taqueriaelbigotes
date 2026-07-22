package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;

import controladores.CLogin;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;

    private Color azulPrincipal;
    private Color rosaAcento;

    private JPanel         contentPane;
    private JTextField     txtUsuario;
    private JPasswordField txtPassword;
    private JButton        btnLogin;

    public static void main(String[] args) {
        try { FlatLightLaf.setup(); } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }

    public Login() {
        azulPrincipal = new Color(31, 42, 68);
        rosaAcento    = new Color(233, 30, 99);

        setTitle("Login - Sistema de Taquería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(58, 73, 97));
       
        contentPane.setBorder(new EmptyBorder(50, 50, 50, 50));
        setContentPane(contentPane);

        crearPanelLogin();
    }

    private void crearPanelLogin() {
        JPanel panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setBackground(Color.WHITE);
        panelLogin.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(rosaAcento);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelLogin.add(lblTitulo, gbc);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panelLogin.add(lblUsuario, gbc);

        txtUsuario = new JTextField(25);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; gbc.gridy = 1;
        panelLogin.add(txtUsuario, gbc);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 2;
        panelLogin.add(lblPassword, gbc);

        txtPassword = new JPasswordField(25);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; gbc.gridy = 2;
        panelLogin.add(txtPassword, gbc);

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLogin.setBackground(rosaAcento);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.putClientProperty("JButton.buttonType", "roundRect");
        btnLogin.addActionListener(e -> validarLogin());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelLogin.add(btnLogin, gbc);

        // Enter en cualquiera de los dos campos dispara el login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) validarLogin();
            }
        };
        txtUsuario.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);

        contentPane.add(panelLogin, BorderLayout.CENTER);
    }

    private void validarLogin() {
        String usuario  = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, ingrese usuario y contraseña.",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] resultado = CLogin.validarLogin(usuario, password);

        if (resultado == null) {
            // Credenciales incorrectas
            JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos.",
                "Error de login", JOptionPane.ERROR_MESSAGE);

        } else if ((int) resultado[0] == -1) {
            // Credenciales correctas pero empleado inactivo
            JOptionPane.showMessageDialog(this,
                "Este usuario está inactivo.\nContacta al administrador.",
                "Usuario inactivo", JOptionPane.WARNING_MESSAGE);

        } else {
            // Login exitoso
            int    idEmpleado = (int)    resultado[0];
            String rol        = (String) resultado[1];
            String nombre     = (String) resultado[2];

            JOptionPane.showMessageDialog(this,
                "Bienvenido, " + nombre + " (" + rol + ")",
                "Login exitoso", JOptionPane.INFORMATION_MESSAGE);

            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(idEmpleado, rol, nombre);
            ventanaPrincipal.setVisible(true);
            this.dispose();
        }
    }
}
