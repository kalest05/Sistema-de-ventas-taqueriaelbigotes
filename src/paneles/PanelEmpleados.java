package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controladores.CEmpleados;

/*
 * Panel de Empleados — solo accesible para Administrador.
 * Lista todos los empleados: activos primero, inactivos al final en gris.
 * Doble clic abre modal de edición con opción de inactivar o reactivar.
 */
public class PanelEmpleados extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal  = new Color(31, 42, 68);
    private final Color rosaAcento     = new Color(233, 30, 99);
    private final Color fondoClaro     = new Color(245, 247, 250);
    private final Color grisInactivo   = new Color(180, 180, 180);
    private final Color fondoInactivo  = new Color(240, 240, 240);

    private JTable            tabla;
    private DefaultTableModel modelo;

    public PanelEmpleados() {
        setLayout(new BorderLayout());
        setBackground(fondoClaro);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        crearUI();
    }

    public void actualizar() {
        cargarEmpleados();
    }

    private void crearUI() {
        JLabel lblTitulo = new JLabel("Empleados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(azulPrincipal);

        JLabel lblHint = new JLabel("Doble clic en un empleado para editar");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(fondoClaro);
        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(lblHint,   BorderLayout.EAST);

        // Columnas: ID(oculto), Nombre, Puesto, Usuario, Rol, Estado, activo(oculto para lógica)
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Puesto", "Usuario", "Rol", "Estado", "activo"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(rosaAcento);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.removeColumn(tabla.getColumnModel().getColumn(6)); // ocultar col activo
        tabla.removeColumn(tabla.getColumnModel().getColumn(0)); // ocultar ID

        // Renderer para pintar filas inactivas en gris
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                int filaModelo = t.convertRowIndexToModel(row);
                boolean activo = (boolean) modelo.getValueAt(filaModelo, 6);
                if (!activo) {
                    c.setForeground(grisInactivo);
                    c.setBackground(selected ? new Color(220, 220, 220) : fondoInactivo);
                } else {
                    c.setForeground(Color.BLACK);
                    c.setBackground(selected ? tabla.getSelectionBackground() : Color.WHITE);
                }
                return c;
            }
        };
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int filaVista = tabla.rowAtPoint(e.getPoint());
                    if (filaVista >= 0)
                        abrirModalEdicion(tabla.convertRowIndexToModel(filaVista));
                }
            }
        });

        JButton btnAgregar = new JButton("Agregar Empleado");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(rosaAcento);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregar.addActionListener(e -> abrirModalAgregar());

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSur.setBackground(fondoClaro);
        panelSur.add(btnAgregar);

        add(panelTop,               BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelSur,               BorderLayout.SOUTH);

        cargarEmpleados();
    }

    private void cargarEmpleados() {
        modelo.setRowCount(0);
        // Retorna: [idEmpleado, nombre, puesto, usuario, rol, activo(boolean)]
        List<Object[]> lista = CEmpleados.obtenerTodosEmpleados();
        for (Object[] e : lista) {
            boolean activo = (boolean) e[5];
            modelo.addRow(new Object[]{
                e[0],                              // ID (oculto)
                e[1],                              // Nombre
                e[2],                              // Puesto
                e[3],                              // Usuario
                e[4],                              // Rol
                activo ? "Activo" : "Inactivo",    // Estado visible
                activo                             // activo (oculto, para renderer y lógica)
            });
        }
    }

    private void abrirModalEdicion(int filaModelo) {
        int     idEmpleado = (int)     modelo.getValueAt(filaModelo, 0);
        String  nombre     = (String)  modelo.getValueAt(filaModelo, 1);
        String  puesto     = (String)  modelo.getValueAt(filaModelo, 2);
        String  usuario    = (String)  modelo.getValueAt(filaModelo, 3);
        String  rol        = (String)  modelo.getValueAt(filaModelo, 4);
        boolean activo     = (boolean) modelo.getValueAt(filaModelo, 6);

        JTextField    txtNombre   = new JTextField(nombre, 20);
        JTextField    txtPuesto   = new JTextField(puesto, 20);
        JTextField    txtUsuario  = new JTextField(usuario, 20);
        JPasswordField txtPass    = new JPasswordField(20);
        txtPass.setToolTipText("Dejar vacío para no cambiar");
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"Empleado", "Administrador"});
        comboRol.setSelectedItem(rol);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        String[]     labels = {"Nombre:", "Puesto:", "Usuario:", "Nueva contraseña:", "Rol:"};
        JComponent[] campos = {txtNombre, txtPuesto, txtUsuario, txtPass, comboRol};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; panel.add(new JLabel(labels[i]), g);
            g.gridx = 1;               panel.add(campos[i], g);
        }

        JLabel lblNota = new JLabel("Contraseña: dejar vacío para no cambiar");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(Color.GRAY);
        g.gridx = 0; g.gridy = labels.length; g.gridwidth = 2;
        panel.add(lblNota, g);

        // Botón de estado cambia según si está activo o no
        String textoEstado = activo ? "Inactivar Empleado" : "Reactivar Empleado";
        Object[] opciones  = {"Guardar", textoEstado, "Cancelar"};

        int resultado = JOptionPane.showOptionDialog(
            this, panel, "Editar Empleado: " + nombre,
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, opciones, opciones[0]);

        if (resultado == 0) {
            String nuevoNombre  = txtNombre.getText().trim();
            String nuevoPuesto  = txtPuesto.getText().trim();
            String nuevoUsuario = txtUsuario.getText().trim();
            String nuevaPass    = new String(txtPass.getPassword()).trim();
            String nuevoRol     = (String) comboRol.getSelectedItem();

            if (nuevoNombre.isEmpty() || nuevoPuesto.isEmpty() || nuevoUsuario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre, puesto y usuario son obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
         // Validar solo si el usuario escribió una nueva contraseña
            if (!nuevaPass.isEmpty()) {
                if (nuevaPass.length() < 8 || !nuevaPass.matches(".*[A-Z].*")) {
                    JOptionPane.showMessageDialog(this,
                        "La contraseña debe tener mínimo 8 caracteres y al menos una letra mayúscula.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            CEmpleados.actualizarEmpleado(idEmpleado, nuevoNombre, nuevoPuesto,
                nuevoUsuario, nuevaPass.isEmpty() ? null : nuevaPass, nuevoRol);
            JOptionPane.showMessageDialog(this, "Empleado actualizado.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            cargarEmpleados();

        } else if (resultado == 1) {
            if (activo) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Inactivar a \"" + nombre + "\"?\nNo podrá iniciar sesión.",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    CEmpleados.inactivarEmpleado(idEmpleado);
                    JOptionPane.showMessageDialog(this, "Empleado inactivado.", "Listo",
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarEmpleados();
                }
            } else {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Reactivar a \"" + nombre + "\"?\nPodrá volver a iniciar sesión.",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    CEmpleados.activarEmpleado(idEmpleado);
                    JOptionPane.showMessageDialog(this, "Empleado reactivado.", "Listo",
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarEmpleados();
                }
            }
        }
    }

    private void abrirModalAgregar() {
        JTextField    txtNombre  = new JTextField(20);
        JTextField    txtPuesto  = new JTextField(20);
        JTextField    txtUsuario = new JTextField(20);
        JPasswordField txtPass   = new JPasswordField(20);
        JComboBox<String> combo  = new JComboBox<>(new String[]{"Empleado", "Administrador"});

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        String[]     labels = {"Nombre:", "Puesto:", "Usuario:", "Contraseña:", "Rol:"};
        JComponent[] campos = {txtNombre, txtPuesto, txtUsuario, txtPass, combo};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; panel.add(new JLabel(labels[i]), g);
            g.gridx = 1;               panel.add(campos[i], g);
        }

        int resultado = JOptionPane.showConfirmDialog(this, panel, "Agregar Empleado",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String nombre   = txtNombre.getText().trim();
            String puesto   = txtPuesto.getText().trim();
            String usuario  = txtUsuario.getText().trim();
            String password = new String(txtPass.getPassword()).trim();
            String rol      = (String) combo.getSelectedItem();

            if (nombre.isEmpty() || puesto.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
         // Validación de contraseña
            if (password.length() < 8 || !password.matches(".*[A-Z].*")) {
                JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener mínimo 8 caracteres y al menos una letra mayúscula.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            CEmpleados.agregarEmpleado(nombre, puesto, usuario, password, rol);
            JOptionPane.showMessageDialog(this, "Empleado agregado.", "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            cargarEmpleados();
        }
    }
}
