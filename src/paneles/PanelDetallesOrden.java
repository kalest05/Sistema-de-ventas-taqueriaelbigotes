package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controladores.CVentas;
import vistas.VentanaPrincipal;

/**
 * Panel de Detalles de Orden.
 *
 * CORRECCIÓN:
 *  - Se eliminó el TableModelListener (causaba bloqueo y borrado de datos)
 *  - Ninguna celda es editable directamente en la tabla
 *  - Doble clic en una fila abre un modal con la cantidad actual,
 *    permitiendo modificarla o eliminar el producto desde ahí
 *  - El subtotal se recalcula automáticamente al confirmar
 */
public class PanelDetallesOrden extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal  = new Color(31, 42, 68);
    private final Color rosaAcento     = new Color(233, 30, 99);

    private JTable            tablaDetalles;
    private DefaultTableModel modeloDetalles;

    private int    idPedido;
    private double totalPedido;

    private final VentanaPrincipal ventanaPrincipal;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelDetallesOrden(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        crearUI();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────────────────────
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
        modeloDetalles.setRowCount(0);
        cargarDetallesPedido();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────
    private void crearUI() {
        JPanel panelDetalles = new JPanel(new BorderLayout());
        panelDetalles.setBackground(Color.WHITE);

        // ── Título ────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Detalles del Pedido");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(rosaAcento);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Instrucción para el usuario
        JLabel lblHint = new JLabel("Doble clic en un producto para editar su cantidad o eliminarlo",
            SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);

        JPanel panelNorth = new JPanel(new GridLayout(2, 1));
        panelNorth.setBackground(Color.WHITE);
        panelNorth.add(lblTitulo);
        panelNorth.add(lblHint);
        panelDetalles.add(panelNorth, BorderLayout.NORTH);

        // ── Tabla — sin celdas editables ──────────────────────────────────
        modeloDetalles = new DefaultTableModel(
            new Object[]{"ID Producto", "Producto", "Cantidad", "Subtotal"}, 0) {

			@Override
            public boolean isCellEditable(int row, int column) {
                return false; // Edición solo a través del modal
            }
        };

        tablaDetalles = new JTable(modeloDetalles);
        tablaDetalles.setRowHeight(40);
        tablaDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaDetalles.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaDetalles.getTableHeader().setBackground(rosaAcento);
        tablaDetalles.getTableHeader().setForeground(Color.WHITE);
        tablaDetalles.removeColumn(tablaDetalles.getColumnModel().getColumn(0)); // Ocultar ID

        // ── Doble clic en fila → modal de edición/eliminación ─────────────
        tablaDetalles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaDetalles.rowAtPoint(e.getPoint());
                    if (fila >= 0) {
                        abrirModalEdicion(fila);
                    }
                }
            }
        });

        panelDetalles.add(new JScrollPane(tablaDetalles), BorderLayout.CENTER);

        // ── Botones ───────────────────────────────────────────────────────
        JPanel panelBotones = new JPanel(new GridLayout(1, 3, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setBackground(rosaAcento);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnAgregar.addActionListener(e -> agregarProducto());
        panelBotones.add(btnAgregar);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(azulPrincipal);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnGuardar.addActionListener(e -> guardarCambios());
        panelBotones.add(btnGuardar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBackground(Color.GRAY);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.putClientProperty("JButton.buttonType", "roundRect");
        btnCerrar.addActionListener(e -> ventanaPrincipal.mostrarPanel(VentanaPrincipal.VISTA_ORDENES));
        panelBotones.add(btnCerrar);

        panelDetalles.add(panelBotones, BorderLayout.SOUTH);
        add(panelDetalles, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Modal de edición — se abre con doble clic en la fila
    // ─────────────────────────────────────────────────────────────────────────
    private void abrirModalEdicion(int fila) {
        String nombreProducto = (String) modeloDetalles.getValueAt(fila, 1);
        int    cantidadActual = (int)    modeloDetalles.getValueAt(fila, 2);
        double subtotalActual = (double) modeloDetalles.getValueAt(fila, 3);
        double precioUnitario = (cantidadActual > 0) ? subtotalActual / cantidadActual : 0;

        // ── Construir el panel del modal ───────────────────────────────────
        JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(cantidadActual, 1, 999, 1));
        spinnerCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Label de precio unitario para referencia
        JLabel lblPrecio = new JLabel("Precio unitario: $" + String.format("%.2f", precioUnitario));
        lblPrecio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblPrecio.setForeground(Color.GRAY);

        // Label de subtotal que se actualiza en tiempo real al mover el spinner
        JLabel lblSubtotal = new JLabel(
            "Subtotal: $" + String.format("%.2f", cantidadActual * precioUnitario));
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtotal.setForeground(azulPrincipal);

        // Actualizar subtotal en tiempo real al cambiar el spinner
        spinnerCantidad.addChangeListener(ev -> {
            int nuevaCantidad = (int) spinnerCantidad.getValue();
            lblSubtotal.setText("Subtotal: $" + String.format("%.2f", nuevaCantidad * precioUnitario));
        });

        JPanel panelModal = new JPanel();
        panelModal.setLayout(new BoxLayout(panelModal, BoxLayout.Y_AXIS));
        panelModal.setBackground(Color.WHITE);
        panelModal.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblProducto = new JLabel("Producto: " + nombreProducto);
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblProducto.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCantidadTxt = new JLabel("Cantidad:");
        lblCantidadTxt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCantidadTxt.setAlignmentX(Component.LEFT_ALIGNMENT);

        spinnerCantidad.setAlignmentX(Component.LEFT_ALIGNMENT);
        spinnerCantidad.setMaximumSize(new Dimension(120, 35));

        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelModal.add(lblProducto);
        panelModal.add(Box.createVerticalStrut(10));
        panelModal.add(lblPrecio);
        panelModal.add(Box.createVerticalStrut(8));
        panelModal.add(lblCantidadTxt);
        panelModal.add(Box.createVerticalStrut(4));
        panelModal.add(spinnerCantidad);
        panelModal.add(Box.createVerticalStrut(10));
        panelModal.add(lblSubtotal);

        // ── Opciones del diálogo: Guardar / Eliminar / Cancelar ────────────
        Object[] opciones = {"Guardar", "Eliminar Producto", "Cancelar"};

        int resultado = JOptionPane.showOptionDialog(
            this,
            panelModal,
            "Editar: " + nombreProducto,
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (resultado == 0) {
            // Guardar — actualizar cantidad y subtotal en la tabla
            int    nuevaCantidad = (int) spinnerCantidad.getValue();
            double nuevoSubtotal = nuevaCantidad * precioUnitario;
            modeloDetalles.setValueAt(nuevaCantidad, fila, 2);
            modeloDetalles.setValueAt(nuevoSubtotal,  fila, 3);
            recalcularTotal();

        } else if (resultado == 1) {
            // Eliminar producto — confirmar antes
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar \"" + nombreProducto + "\" del pedido?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                modeloDetalles.removeRow(fila);
                recalcularTotal();
            }
        }
        // resultado == 2 → Cancelar, no hace nada
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Agregar producto nuevo al pedido
    // ─────────────────────────────────────────────────────────────────────────
    private void agregarProducto() {
        List<Object[]> productos = CVentas.cargarProductosParaSeleccion();
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles.", "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> combo = new JComboBox<>();
        for (Object[] p : productos) combo.addItem(p[1] + " - $" + p[2]);

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Producto:"));  panel.add(combo);
        panel.add(new JLabel("Cantidad:")); panel.add(spinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Agregar Producto",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int index = combo.getSelectedIndex();
            if (index != -1) {
                Object[] producto = productos.get(index);
                int    idProducto = (int)    producto[0];
                String nombre     = (String) producto[1];
                double precio     = (double) producto[2];
                int    cantidad   = (int)    spinner.getValue();
                double subtotal   = cantidad * precio;

                modeloDetalles.addRow(new Object[]{idProducto, nombre, cantidad, subtotal});
                recalcularTotal();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Guardar en BD y regresar
    // ─────────────────────────────────────────────────────────────────────────
    private void guardarCambios() {
        CVentas.actualizarDetallesPedido(idPedido, modeloDetalles, totalPedido);
        JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente.", "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
        ventanaPrincipal.mostrarPanel(VentanaPrincipal.VISTA_ORDENES);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarDetallesPedido() {
        List<Object[]> detalles = CVentas.obtenerDetallesPedido(idPedido);
        for (Object[] detalle : detalles) {
            modeloDetalles.addRow(detalle);
        }
        recalcularTotal();
    }

    private void recalcularTotal() {
        totalPedido = 0;
        for (int i = 0; i < modeloDetalles.getRowCount(); i++) {
            totalPedido += (double) modeloDetalles.getValueAt(i, 3);
        }
    }
}
