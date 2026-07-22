package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

import controladores.CInventario;

/**
 * VISTA INVENTARIO
 */
public class Inventario extends JFrame {

    private static final long serialVersionUID = 1L;

    private Color azulPrincipal = new Color(31, 42, 68);
    private Color rosaAcento = new Color(233, 30, 99);
    private Color amarilloAcento = new Color(255, 193, 7);
    private Color fondoClaro = new Color(245, 247, 250);

    private JPanel contentPane;
    private JPanel panelSidebar;
    private JPanel panelHeader;
    private JPanel panelTabla;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnAgregar;    
    private JButton btnOrden;
    private JButton btnInventario;
    private JButton btnEmpleados;
    private JButton btnReportes;
    private JButton btnVerOrdenesDia;
    private JLabel lblUsuario;
    private JLabel lblPuesto;
    private JButton btnCerrarSesion;
    private String rolSesion;
    private String nombreSesion;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            Login login = new Login(); 
            login.setVisible(true);
        });
    }
    
    public Inventario( String rol, String nombre) {
        this.rolSesion = rol;
        this.nombreSesion = nombre;

        setTitle("Gestión de Inventario - Taquería El Bigotes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(fondoClaro);
        setContentPane(contentPane);

        crearSidebar();
        crearHeader();
        crearTabla();
        cargarInventarioDesdeBD(); 
    }

    private void crearSidebar() {
        panelSidebar = new JPanel();
        panelSidebar.setPreferredSize(new Dimension(230, 0));
        panelSidebar.setBackground(azulPrincipal);
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBorder(new EmptyBorder(50, 20, 30, 20));
        
        // Logo
        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imagenes/logo.jpg"));
            Image img = icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Logo no encontrado, usando texto.");
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSidebar.add(lblLogo);
        panelSidebar.add(Box.createVerticalStrut(20));

        // Botones
        btnOrden = crearBotonMenu("Orden");
        btnInventario = crearBotonMenu("Inventario");
        btnReportes = crearBotonMenu("Reportes");
        btnVerOrdenesDia = crearBotonMenu("Ver Órdenes del Día");

        btnVerOrdenesDia.addActionListener(e -> verOrdenesDelDia());

        if ("Administrador".equals(rolSesion)) {
        	btnEmpleados = crearBotonMenu("EmpleadoS");
        	btnEmpleados.addActionListener(e -> new AgregarEmpleado().setVisible(true));
        }

        btnCerrarSesion = crearBotonMenu("Cerrar Sesión");
        btnCerrarSesion.setBackground(amarilloAcento);
        btnCerrarSesion.setForeground(Color.BLACK);
        btnCerrarSesion.setPreferredSize(new Dimension(150, 42));
        btnCerrarSesion.setMaximumSize(new Dimension(150, 42));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        panelSidebar.add(btnOrden);
        panelSidebar.add(Box.createVerticalStrut(15));
        panelSidebar.add(btnInventario);
       
        panelSidebar.add(Box.createVerticalStrut(15));
        panelSidebar.add(btnReportes);
        panelSidebar.add(Box.createVerticalStrut(15));
        panelSidebar.add(btnVerOrdenesDia);
        if ("Administrador".equals(rolSesion)) {
            panelSidebar.add(Box.createVerticalStrut(15));
            panelSidebar.add(btnEmpleados);
        }
        // Cerrar sesión al final
	    panelSidebar.add(Box.createVerticalStrut(30)); 
	    panelSidebar.add(btnCerrarSesion);
	    
	    panelSidebar.add(Box.createVerticalGlue());
	    panelSidebar.add(Box.createVerticalGlue());
        panelSidebar.add(Box.createVerticalStrut(15));
        // Panel info usuario
        JPanel panelInfoUsuario = new JPanel();
        panelInfoUsuario.setLayout(new BoxLayout(panelInfoUsuario, BoxLayout.Y_AXIS));
        panelInfoUsuario.setBackground(new Color(245, 245, 220));
        panelInfoUsuario.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelInfoUsuario.setMaximumSize(new Dimension(200, 60));
        panelInfoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblUsuario = new JLabel(nombreSesion);
        lblUsuario.setForeground(Color.BLACK);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPuesto = new JLabel(rolSesion);
        lblPuesto.setForeground(new Color(0, 51, 102));
        lblPuesto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPuesto.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInfoUsuario.add(lblUsuario);
        panelInfoUsuario.add(Box.createVerticalStrut(5));
        panelInfoUsuario.add(lblPuesto);

        panelSidebar.add(panelInfoUsuario);
        contentPane.add(panelSidebar, BorderLayout.WEST);
    }

    private void verOrdenesDelDia() {
        
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setBackground(rosaAcento);
        btn.setForeground(Color.WHITE);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setMaximumSize(new Dimension(200, 42));
        return btn;
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().setVisible(true);
        }
    }


    private void crearHeader() {
        panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel lblTitulo = new JLabel("Taquería El Bigotes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(rosaAcento);
        
        JLabel lblFecha = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy HH:mm")));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFecha.setForeground(Color.GRAY);
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(lblFecha, BorderLayout.EAST);
        
        contentPane.add(panelHeader, BorderLayout.NORTH);
    }

    private void crearTabla() {
        panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(fondoClaro);
        panelTabla.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Botón Agregar arriba
        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.setBackground(amarilloAcento);
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());
        
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(fondoClaro);
        panelTop.add(btnAgregar, BorderLayout.WEST);

        modelo = new DefaultTableModel(new Object[]{"ID", "Producto", "Cantidad", "Última Actualización", "Editar", "Eliminar"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(45);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(rosaAcento);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.removeColumn(tabla.getColumnModel().getColumn(0)); // Ocultar ID

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int columna = tabla.columnAtPoint(e.getPoint());
                if (columna == 3) editarProducto(fila); // Columna Editar
                if (columna == 4) eliminarProducto(fila); // Columna Eliminar
            }
        });

        panelTabla.add(panelTop, BorderLayout.NORTH);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);
        contentPane.add(panelTabla, BorderLayout.CENTER);
    }

    private void cargarInventarioDesdeBD() {
        modelo.setRowCount(0); // Limpiar tabla
        List<Object[]> lista = CInventario.cargarInventario();
        for (Object[] item : lista) {
            modelo.addRow(new Object[]{
                item[0], // idInventario
                item[1], // nombreInsumo
                item[3] + " " + item[2], // cantidad + unidadMedida
                item[4], // fechaActualizacion
                "✏️",
                "🗑"
            });
        }
    }
    private void mostrarFormularioAgregar() {
        JTextField txtNombre = new JTextField();
        JTextField txtUnidad = new JTextField();
        JSpinner spinnerCantidad = new JSpinner();
        spinnerCantidad.setValue(1.0);

        Object[] mensaje = {
            "Nombre del Producto:", txtNombre,
            "Unidad de Medida:", txtUnidad,
            "Cantidad:", spinnerCantidad
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Producto", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String unidad = txtUnidad.getText().trim();
            Number cantidadValue = (Number) spinnerCantidad.getValue();
            double cantidad = cantidadValue.doubleValue();
            
            if (nombre.isEmpty() || unidad.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            CInventario.agregarProductoCompleto(nombre, unidad, cantidad);
            cargarInventarioDesdeBD(); // Recargar tabla
        }
    }

    private void editarProducto(int fila) {
        int idInventario = (int) modelo.getValueAt(fila, 0);
        String cantidadStr = modelo.getValueAt(fila, 2).toString();
        double cantidadActual = Double.parseDouble(cantidadStr.split(" ")[0]); 

        JSpinner spinnerCantidad = new JSpinner();
        spinnerCantidad.setValue(cantidadActual);

        int opcion = JOptionPane.showConfirmDialog(this, new Object[]{"Nueva Cantidad:", spinnerCantidad}, "Editar Cantidad", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            Number nuevaCantidadValue = (Number) spinnerCantidad.getValue();
            double nuevaCantidad = nuevaCantidadValue.doubleValue();
            CInventario.actualizarCantidad(idInventario, nuevaCantidad);
            cargarInventarioDesdeBD(); // Recargar tabla
        }
    }

    private void eliminarProducto(int fila) {
        int idInventario = (int) modelo.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            CInventario.eliminarInventario(idInventario);
            cargarInventarioDesdeBD(); // Recargar tabla
        }
    }
}