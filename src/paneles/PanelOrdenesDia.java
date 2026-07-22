package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controladores.CVentas;
import vistas.VentanaPrincipal;

/**
 * Panel de Órdenes del Día.
 *
 * Basado en el método verOrdenesDelDia() que existía en VentanaVenta.
 * Al hacer clic en una fila navega directamente a PanelDetallesOrden
 * usando ventanaPrincipal.mostrarDetalle(idPedido), reemplazando el
 * antiguo: new DetallesOrden(idPedido).setVisible(true)
 */
public class PanelOrdenesDia extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal  = new Color(31, 42, 68);
    private final Color rosaAcento     = new Color(233, 30, 99);
    private final Color fondoClaro     = new Color(245, 247, 250);

    private JTable            tablaOrdenes;
    private DefaultTableModel modeloOrdenes;

    private final VentanaPrincipal ventanaPrincipal;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelOrdenesDia(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;

        setLayout(new BorderLayout());
        setBackground(fondoClaro);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        crearUI();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Llamado por VentanaPrincipal.mostrarPanel() al navegar aquí
    // ─────────────────────────────────────────────────────────────────────────
    public void actualizar() {
        cargarOrdenes();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────
    private void crearUI() {
        // ── Título ────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Órdenes del Día");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(azulPrincipal);
        add(lblTitulo, BorderLayout.NORTH);

        // ── Tabla ─────────────────────────────────────────────────────────
        modeloOrdenes = new DefaultTableModel(
            new Object[]{"ID Pedido", "Fecha", "Total", "Empleado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaOrdenes = new JTable(modeloOrdenes);
        tablaOrdenes.setRowHeight(40);
        tablaOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaOrdenes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaOrdenes.getTableHeader().setBackground(rosaAcento);
        tablaOrdenes.getTableHeader().setForeground(Color.WHITE);

        // Al seleccionar una fila navega a los detalles del pedido
        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaOrdenes.getSelectedRow();
                if (fila != -1) {
                    int idPedido = (int) modeloOrdenes.getValueAt(fila, 0);
                    ventanaPrincipal.mostrarDetalle(idPedido);
                }
            }
        });

        add(new JScrollPane(tablaOrdenes), BorderLayout.CENTER);

        // ── Botón refrescar ───────────────────────────────────────────────
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefrescar.setBackground(rosaAcento);
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.putClientProperty("JButton.buttonType", "roundRect");
        btnRefrescar.addActionListener(e -> cargarOrdenes());

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSur.setBackground(fondoClaro);
        panelSur.add(btnRefrescar);
        add(panelSur, BorderLayout.SOUTH);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Datos
    // ─────────────────────────────────────────────────────────────────────────
    private void cargarOrdenes() {
        modeloOrdenes.setRowCount(0);
        List<Object[]> ordenes = CVentas.obtenerOrdenesDelDia();
        for (Object[] orden : ordenes) {
            modeloOrdenes.addRow(orden);
        }
    }
}
