package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import controladores.CVentas;

/**
 * Clase que representa la interfaz gráfica para ver y editar detalles de un pedido.
 * <p>
 * Esta clase extiende JFrame y permite visualizar productos, cantidades y subtotales de un pedido,
 * además de agregar, eliminar o modificar cantidades.
 * </p>
 * 
 * @author [Tu Nombre o Equipo]
 * @version 1.0
 * @since [Fecha]
 */
public class DetallesOrden extends JFrame {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable que determina el color principal azul
     */
    private Color azulPrincipal;
    /**
     * Variable que determina el color de acento rosa
     */
    private Color rosaAcento;
    /**
     * Variable que determina el color de acento amarillo
     */
    private Color amarilloAcento;
    
    /* ================= COMPONENTES ================= */
    private JPanel contentPane;
    private JTable tablaDetalles;
    private DefaultTableModel modeloDetalles;
    private JButton btnAgregarProducto;
    private JButton btnEliminarProducto;
    private JButton btnGuardarCambios;
    private JButton btnCerrar;
    private int idPedido;
    private double totalPedido;

    /**
     * Constructor de componentes de la interfaz visual.
     */
    public DetallesOrden(int idPedido) {
        this.idPedido = idPedido;
        
        /* ================= PALETA MODERNA ================= */
        azulPrincipal = new Color(31, 42, 68);     // Azul profundo
        rosaAcento = new Color(233, 30, 99);       // Rosa moderno
        amarilloAcento = new Color(255, 193, 7);   // Amarillo elegante
        
        setTitle("Detalles del Pedido - ID: " + idPedido);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        
        crearPanelDetalles();
        cargarDetallesPedido();
    }

    /**
     * Crea y configura el panel de detalles.
     */
    private void crearPanelDetalles() {
        JPanel panelDetalles = new JPanel(new BorderLayout());
        panelDetalles.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Detalles del Pedido");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(rosaAcento);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelDetalles.add(lblTitulo, BorderLayout.NORTH);
        
        // Tabla de detalles (hacer editable la columna de cantidad)
        modeloDetalles = new DefaultTableModel(
                new Object[] { "ID Producto", "Producto", "Cantidad", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo cantidad editable
            }
        };
        tablaDetalles = new JTable(modeloDetalles);
        tablaDetalles.removeColumn(tablaDetalles.getColumnModel().getColumn(0)); // Ocultar ID Producto
        
        // Listener para recalcular subtotal al editar cantidad
        modeloDetalles.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) { // Columna cantidad
                    int row = e.getFirstRow();
                    int cantidad = (int) modeloDetalles.getValueAt(row, 2);
                    double precioUnitario = obtenerPrecioProducto((int) modeloDetalles.getValueAt(row, 0));
                    double subtotal = cantidad * precioUnitario;
                    modeloDetalles.setValueAt(subtotal, row, 3);
                    recalcularTotal();
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaDetalles);
        panelDetalles.add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        
        btnAgregarProducto = new JButton("Agregar Producto");
        btnAgregarProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregarProducto.setBackground(rosaAcento);
        btnAgregarProducto.setForeground(Color.WHITE);
        btnAgregarProducto.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregarProducto.addActionListener(e -> agregarProducto());
        panelBotones.add(btnAgregarProducto);
        
        btnEliminarProducto = new JButton("Eliminar Producto");
        btnEliminarProducto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminarProducto.setBackground(amarilloAcento);
        btnEliminarProducto.setForeground(Color.BLACK);
        btnEliminarProducto.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminarProducto.addActionListener(e -> eliminarProducto());
        panelBotones.add(btnEliminarProducto);
        
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardarCambios.setBackground(azulPrincipal);
        btnGuardarCambios.setForeground(Color.WHITE);
        btnGuardarCambios.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardarCambios.addActionListener(e -> guardarCambios());
        panelBotones.add(btnGuardarCambios);
        
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(Color.GRAY);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.putClientProperty("JButton.buttonType", "roundRect");
        btnCerrar.addActionListener(e -> dispose());
        panelBotones.add(btnCerrar);
        
        panelDetalles.add(panelBotones, BorderLayout.SOUTH);
        
        contentPane.add(panelDetalles, BorderLayout.CENTER);
    }

    /**
     * Carga los detalles del pedido desde la BD.
     */
    private void cargarDetallesPedido() {
        List<Object[]> detalles = CVentas.obtenerDetallesPedido(idPedido);
        
        for (Object[] detalle : detalles) {
            modeloDetalles.addRow(detalle);
        }
        
        recalcularTotal();
    }

    /**
     * Agrega un producto al pedido con cantidad seleccionada en el mismo diálogo.
     */
    private void agregarProducto() {
        List<Object[]> productos = CVentas.cargarProductosParaSeleccion();
        
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear panel personalizado para selección
        JPanel panelSeleccion = new JPanel(new GridLayout(2, 2, 10, 10));
        panelSeleccion.setBackground(Color.WHITE);
        
        JComboBox<String> comboProductos = new JComboBox<>();
        for (Object[] p : productos) {
            comboProductos.addItem(p[1] + " - $" + p[2]);
        }
        
        JSpinner spinnerCantidad = new JSpinner();
        spinnerCantidad.setValue(1);
        
        panelSeleccion.add(new JLabel("Producto:"));
        panelSeleccion.add(comboProductos);
        panelSeleccion.add(new JLabel("Cantidad:"));
        panelSeleccion.add(spinnerCantidad);
        
        int result = JOptionPane.showConfirmDialog(this, panelSeleccion, "Agregar Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int index = comboProductos.getSelectedIndex();
            if (index != -1) {
                Object[] producto = productos.get(index);
                int idProducto = (int) producto[0];
                String nombre = (String) producto[1];
                double precio = (double) producto[2];
                int cantidad = (int) spinnerCantidad.getValue();
                
                if (cantidad > 0) {
                    double subtotal = cantidad * precio;
                    modeloDetalles.addRow(new Object[] { idProducto, nombre, cantidad, subtotal });
                    recalcularTotal();
                } else {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Elimina el producto seleccionado de la tabla.
     */
    private void eliminarProducto() {
        int filaSeleccionada = tablaDetalles.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        modeloDetalles.removeRow(filaSeleccionada);
        recalcularTotal();
    }

    /**
     * Guarda los cambios en la BD.
     */
    private void guardarCambios() {
        CVentas.actualizarDetallesPedido(idPedido, modeloDetalles, totalPedido);
        JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    /**
     * Recalcula el total del pedido basado en la tabla.
     */
    private void recalcularTotal() {
        totalPedido = 0;
        for (int i = 0; i < modeloDetalles.getRowCount(); i++) {
            totalPedido += (double) modeloDetalles.getValueAt(i, 3);
        }
        CVentas.actualizarTotalPedido(idPedido, totalPedido);
    }

    /**
     * Obtiene el precio unitario de un producto por ID.
     */
    private double obtenerPrecioProducto(int idProducto) {
        List<Object[]> productos = CVentas.cargarProductosParaSeleccion();
        for (Object[] p : productos) {
            if ((int) p[0] == idProducto) {
                return (double) p[2];
            }
        }
        return 0.0;
    }
}
