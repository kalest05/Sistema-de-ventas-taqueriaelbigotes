package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import controladores.CInventario;

/**
 * Panel de Inventario — versión JPanel para VentanaPrincipal.
 * Eliminados: header, sidebar, setTitle, setSize, setContentPane.
 * Agregado: método actualizar() para refrescar datos al navegar aquí.
 */
public class PanelInventario extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal  = new Color(31, 42, 68);
    private final Color rosaAcento     = new Color(233, 30, 99);
    private final Color amarilloAcento = new Color(255, 193, 7);
    private final Color fondoClaro     = new Color(245, 247, 250);

    private final String rolSesion;
    private final String nombreSesion;

    private JTable            tabla;
    private DefaultTableModel modelo;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelInventario(String rol, String nombre) {
        this.rolSesion    = rol;
        this.nombreSesion = nombre;

        setLayout(new BorderLayout());
        setBackground(fondoClaro);

        crearTabla();
        cargarInventarioDesdeBD();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Llamado por VentanaPrincipal.mostrarPanel() cada vez que se navega aquí
    // ─────────────────────────────────────────────────────────────────────────
    public void actualizar() {
        cargarInventarioDesdeBD();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────
    private void crearTabla() {
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(fondoClaro);
        panelTabla.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ── Título + botón agregar ─────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Gestión de Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(azulPrincipal);

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAgregar.setBackground(amarilloAcento);
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregar.addActionListener(e -> mostrarFormularioAgregar());

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(fondoClaro);
        panelTop.add(lblTitulo,  BorderLayout.WEST);
        panelTop.add(btnAgregar, BorderLayout.EAST);

        // ── Tabla ─────────────────────────────────────────────────────────
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Cantidad", "Fecha", "Editar", "Eliminar"}, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {

                if (columnIndex == 4 || columnIndex == 5) {
                    return Icon.class; // Estas columnas son iconos
                }

                return Object.class;
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
                int fila    = tabla.rowAtPoint(e.getPoint());
                int columna = tabla.columnAtPoint(e.getPoint());
                if (fila < 0) return;
                if (columna == 3) editarProducto(fila);
                if (columna == 4) eliminarProducto(fila);
            }
        });

        panelTabla.add(panelTop,              BorderLayout.NORTH);
        panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CRUD
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarInventarioDesdeBD() {
        modelo.setRowCount(0);
        List<Object[]> lista = CInventario.cargarInventario();
        for (Object[] item : lista) {
        	
        	  Icon iconEditar = new FlatSVGIcon(
                      getClass().getResource("/icons/edit.svg"));

              Icon iconEliminar = new FlatSVGIcon(
                      getClass().getResource("/icons/delete.svg"));
        	
            modelo.addRow(new Object[]{
                item[0],                 // idInventario (oculto)
                item[1],                 // nombreInsumo
                item[3] + " " + item[2], // cantidad + unidadMedida
                item[4],                 // fechaActualizacion
                iconEditar,
                iconEliminar
                
            });
        }
    }
    

    private void mostrarFormularioAgregar() {
        JTextField txtNombre   = new JTextField();
        JTextField txtUnidad   = new JTextField();
        JSpinner   spinnerCant = new JSpinner();
        spinnerCant.setValue(1.0);

        int opcion = JOptionPane.showConfirmDialog(this,
            new Object[]{"Nombre del Producto:", txtNombre,
                         "Unidad de Medida:",    txtUnidad,
                         "Cantidad:",            spinnerCant},
            "Agregar Producto", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String unidad = txtUnidad.getText().trim();
            if (nombre.isEmpty() || unidad.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            double cantidad = ((Number) spinnerCant.getValue()).doubleValue();
            CInventario.agregarProductoCompleto(nombre, unidad, cantidad);
            cargarInventarioDesdeBD();
        }
    }

    private void editarProducto(int fila) {
        int    idInventario   = (int) modelo.getValueAt(fila, 0);
        double cantidadActual = Double.parseDouble(
            modelo.getValueAt(fila, 2).toString().split(" ")[0]);

        JSpinner spinnerCant = new JSpinner();
        spinnerCant.setValue(cantidadActual);

        int opcion = JOptionPane.showConfirmDialog(this,
            new Object[]{"Nueva Cantidad:", spinnerCant},
            "Editar Cantidad", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            double nuevaCantidad = ((Number) spinnerCant.getValue()).doubleValue();
            CInventario.actualizarCantidad(idInventario, nuevaCantidad);
            cargarInventarioDesdeBD();
        }
    }

    private void eliminarProducto(int fila) {
        int idInventario = (int) modelo.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            CInventario.eliminarInventario(idInventario);
            cargarInventarioDesdeBD();
        }
    }
}
