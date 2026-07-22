package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

/**
 * Clase que representa la interfaz gráfica para agregar un nuevo empleado y su usuario.
 * <p>
 * Esta clase extiende JFrame y proporciona una ventana a pantalla completa para ingresar datos del empleado,
 * crear su usuario y contraseña para el login.
 * </p>
 * 
 * @version 1.0
 * @since 2026
 */
public class AgregarEmpleado extends JFrame {

    private static final long serialVersionUID = 1L;
    private Color azulPrincipal;
    private Color rosaAcento;
    private JPanel contentPane;
    private JTextField txtNombre;
    private JTextField txtPuesto;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private JButton btnGuardar;
    private JButton btnCancelar;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            AgregarEmpleado ventana = new AgregarEmpleado();
            ventana.setVisible(true);
        });
    }

    /**
     * Constructor de componentes de la interfaz visual.
     */
    public AgregarEmpleado() {
        
        azulPrincipal = new Color(31, 42, 68);     // Azul profundo
        rosaAcento = new Color(233, 30, 99);       // Rosa moderno
        
        setTitle("Agregar Empleado");
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(azulPrincipal); // Fondo azul
        contentPane.setBorder(new EmptyBorder(50, 50, 50, 50)); 
        setContentPane(contentPane);
        
        crearPanelAgregar();
    }

    /**
     * Crea y configura el panel para agregar empleado.
     */
    private void crearPanelAgregar() {
        JPanel panelAgregar = new JPanel(new GridBagLayout());
        panelAgregar.setBackground(Color.WHITE);
        panelAgregar.setBorder(new EmptyBorder(40, 40, 40, 40)); // Padding interno
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel lblTitulo = new JLabel("Agregar Nuevo Empleado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Fuente más grande para pantalla completa
        lblTitulo.setForeground(rosaAcento);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelAgregar.add(lblTitulo, gbc);
        
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Fuente más grande
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelAgregar.add(lblNombre, gbc);
        
        txtNombre = new JTextField(25); 
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelAgregar.add(txtNombre, gbc);
        
        JLabel lblPuesto = new JLabel("Puesto:");
        lblPuesto.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelAgregar.add(lblPuesto, gbc);
        
        txtPuesto = new JTextField(25); 
        txtPuesto.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panelAgregar.add(txtPuesto, gbc);
        
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelAgregar.add(lblUsuario, gbc);
        
        txtUsuario = new JTextField(25); 
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 3;
        panelAgregar.add(txtUsuario, gbc);
        
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelAgregar.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(25); 
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 4;
        panelAgregar.add(txtPassword, gbc);
        
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 5;
        panelAgregar.add(lblRol, gbc);
        
        comboRol = new JComboBox<>(new String[]{"Empleado", "Administrador"});
        comboRol.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 5;
        panelAgregar.add(comboRol, gbc);
        
        JPanel panelBotones = new JPanel();
        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        btnGuardar.setBackground(rosaAcento);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarEmpleado();
            }
        });
        panelBotones.add(btnGuardar);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        btnCancelar.setBackground(Color.GRAY);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelAgregar.add(panelBotones, gbc);
        
        contentPane.add(panelAgregar, BorderLayout.CENTER);
    }

    /**
     * Guarda el empleado y su usuario.
     */
    private void guardarEmpleado() {
        String nombre = txtNombre.getText().trim();
        String puesto = txtPuesto.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        String rol = (String) comboRol.getSelectedItem();
        
        if (nombre.isEmpty() || puesto.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idEmp = CLogin.insertarEmpleado(nombre, puesto);
        if (idEmp > 0) {
            boolean ok = CLogin.insertarUsuario(usuario, password, rol, idEmp);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Empleado agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar empleado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
