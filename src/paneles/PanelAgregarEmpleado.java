package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import controladores.CLogin;

/**
 * Panel de Agregar Empleado.
 *
 * NOTA: Este panel tiene un caso especial: originalmente era pantalla completa
 * (MAXIMIZED_BOTH). Ahora vive dentro de VentanaPrincipal, así que el formulario
 * se centra dentro del espacio disponible usando GridBagLayout igual que antes.
 *
 * Solo visible para Administrador. VentanaPrincipal ya lo controla con:
 *   if ("Administrador".equals(rolSesion)) { registrar y mostrar botón }
 */
public class PanelAgregarEmpleado extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal = new Color(31, 42, 68);
    private final Color rosaAcento    = new Color(233, 30, 99);

    private JTextField     txtNombre;
    private JTextField     txtPuesto;
    private JTextField     txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelAgregarEmpleado() {
        // Fondo azul igual que el original (que tenía contentPane azul)
        setLayout(new BorderLayout());
        setBackground(azulPrincipal);
        setBorder(new EmptyBorder(50, 50, 50, 50));

        crearFormulario();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────
    private void crearFormulario() {
        JPanel panelAgregar = new JPanel(new GridBagLayout());
        panelAgregar.setBackground(Color.WHITE);
        panelAgregar.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // ── Título ────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Agregar Nuevo Empleado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(rosaAcento);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelAgregar.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        // ── Campos ───────────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 1;
        panelAgregar.add(crearLabel("Nombre:"), gbc);
        txtNombre = crearTextField();
        gbc.gridx = 1;
        panelAgregar.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelAgregar.add(crearLabel("Puesto:"), gbc);
        txtPuesto = crearTextField();
        gbc.gridx = 1;
        panelAgregar.add(txtPuesto, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelAgregar.add(crearLabel("Usuario:"), gbc);
        txtUsuario = crearTextField();
        gbc.gridx = 1;
        panelAgregar.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelAgregar.add(crearLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField(25);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        panelAgregar.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panelAgregar.add(crearLabel("Rol:"), gbc);
        comboRol = new JComboBox<>(new String[]{"Empleado", "Administrador"});
        comboRol.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1;
        panelAgregar.add(comboRol, gbc);

        // ── Botones ───────────────────────────────────────────────────────
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnGuardar.setBackground(rosaAcento);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(e -> guardarEmpleado());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLimpiar.setBackground(Color.GRAY);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.putClientProperty("JButton.buttonType", "roundRect");
        btnLimpiar.addActionListener(e -> limpiarCampos());

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelAgregar.add(panelBotones, gbc);

        add(panelAgregar, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica
    // ─────────────────────────────────────────────────────────────────────────
    private void guardarEmpleado() {
        String nombre   = txtNombre.getText().trim();
        String puesto   = txtPuesto.getText().trim();
        String usuario  = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        String rol      = (String) comboRol.getSelectedItem();

        if (nombre.isEmpty() || puesto.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmp = CLogin.insertarEmpleado(nombre, puesto);
        if (idEmp > 0) {
            boolean ok = CLogin.insertarUsuario(usuario, password, rol, idEmp);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Empleado agregado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear usuario.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar empleado.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpia todos los campos del formulario.
     * En el original había un botón "Cancelar" que cerraba la ventana (dispose).
     * Ahora que es un panel, cancelar simplemente limpia el formulario.
     * Si quieres volver a otra vista al cancelar, inyecta una referencia a
     * VentanaPrincipal y llama ventanaPrincipal.mostrarPanel(VISTA_ORDEN).
     */
    public void limpiarCampos() {
        txtNombre.setText("");
        txtPuesto.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────────────────────────────────
    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return lbl;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField(25);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        return txt;
    }
}
