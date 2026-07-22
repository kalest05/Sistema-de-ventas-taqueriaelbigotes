package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import conexion.ConexionBDSQLServer;
import controladores.CVentas;
import modelos.MDetallePedido;
import modelos.MProductos;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import util.ImagenUtil;

public class PanelOrden extends JPanel {

    private static final long serialVersionUID = 1L;

    private String filtroActual = "Todos";

    private final Color azulPrincipal = new Color(31, 42, 68);
    private final Color rosaAcento = new Color(233, 30, 99);
    private final Color amarilloAcento = new Color(255, 193, 7);
    private final Color fondoClaro = new Color(245, 247, 250);

    private final int idEmpleadoSesion;
    private final String rolSesion;
    private final String nombreSesion;

    private JPanel panelGridProductos;
    private JTable tablaResumen;
    private DefaultTableModel modeloResumen;
    private JLabel lblTotal;
    private double total = 0.0;

    private final List<JButton> botonesFiltro = new ArrayList<>();

    // Etiquetas informativas
    private JLabel lblMensajeEliminar;
    private JLabel lblMensajeEditar;

    public PanelOrden(int idEmpleado, String rol, String nombre) {
        this.idEmpleadoSesion = idEmpleado;
        this.rolSesion = rol;
        this.nombreSesion = nombre;

        setLayout(new BorderLayout());
        setBackground(fondoClaro);

        crearZonaProductos();
        crearResumen();

        cargarProductos();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void crearZonaProductos() {
        JPanel panelProductos = new JPanel(new BorderLayout());
        panelProductos.setBackground(fondoClaro);
        panelProductos.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblMenu = new JLabel("Menú de Productos");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblMenu.setForeground(azulPrincipal);

        JPanel panelFiltros = new JPanel();
        panelFiltros.setBackground(fondoClaro);
        JButton btnTodos = new JButton("Todos");
        JButton btnComida = new JButton("Comida");
        JButton btnBebidas = new JButton("Bebidas");

        estilizarBotonFiltro(btnTodos, true);
        estilizarBotonFiltro(btnComida, false);
        estilizarBotonFiltro(btnBebidas, false);

        btnTodos.addActionListener(e -> {
            filtroActual = "Todos";
            cargarProductos();
            seleccionarFiltro(btnTodos, btnTodos, btnComida, btnBebidas);
        });

        btnComida.addActionListener(e -> {
            filtroActual = "Comida";
            cargarProductos();
            seleccionarFiltro(btnComida, btnTodos, btnComida, btnBebidas);
        });

        btnBebidas.addActionListener(e -> {
        	filtroActual = "Bebida";   
        	cargarProductos();
            seleccionarFiltro(btnBebidas, btnTodos, btnComida, btnBebidas);
        });

        panelFiltros.add(btnTodos);
        panelFiltros.add(btnComida);
        panelFiltros.add(btnBebidas);

        panelGridProductos = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        panelGridProductos.setBackground(fondoClaro);

        JScrollPane scroll = new JScrollPane(
                panelGridProductos,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(fondoClaro);
        top.add(lblMenu, BorderLayout.NORTH);
        top.add(panelFiltros, BorderLayout.SOUTH);

        panelProductos.add(top, BorderLayout.NORTH);
        panelProductos.add(scroll, BorderLayout.CENTER);

        add(panelProductos, BorderLayout.CENTER);
    }

    private void estilizarBotonFiltro(JButton btn, boolean seleccionadoInicial) {
        Color colorBase = Color.WHITE;
        Color colorHover = new Color(255, 230, 240);
        Color colorSeleccionado = rosaAcento;

        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(seleccionadoInicial ? colorSeleccionado : colorBase);
        btn.setForeground(seleccionadoInicial ? Color.WHITE : azulPrincipal);
        btn.setBorder(BorderFactory.createLineBorder(rosaAcento, 2));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (!btn.getBackground().equals(colorSeleccionado)) btn.setBackground(colorHover); }
            @Override public void mouseExited(MouseEvent e) { if (!btn.getBackground().equals(colorSeleccionado)) btn.setBackground(colorBase); }
        });

        btn.addActionListener(e -> {
            for (JButton b : botonesFiltro) {
                b.setBackground(colorBase);
                b.setForeground(azulPrincipal);
            }
            btn.setBackground(colorSeleccionado);
            btn.setForeground(Color.WHITE);
        });

        botonesFiltro.add(btn);
    }

    private void seleccionarFiltro(JButton seleccionado, JButton... botones) {
        for (JButton b : botones) {
            b.setBackground(Color.WHITE);
            b.setForeground(azulPrincipal);
        }
        seleccionado.setBackground(rosaAcento);
        seleccionado.setForeground(Color.WHITE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void crearResumen() {
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setPreferredSize(new Dimension(360, 0));
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ── Modelo con columna oculta PrecioUnitario ──────────────────────────
        modeloResumen = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Cant.", "Subtotal", "PrecioUnitario"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // solo editable columna Cantidad
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class;
                if (columnIndex == 3 || columnIndex == 4) return Double.class;
                return String.class;
            }
        };

        tablaResumen = new JTable(modeloResumen);
        tablaResumen.removeColumn(tablaResumen.getColumnModel().getColumn(0)); // ID
        tablaResumen.removeColumn(tablaResumen.getColumnModel().getColumn(3)); // Subtotal visible
        tablaResumen.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // ── Listener para recalcular subtotal al cambiar cantidad ────────────
        modeloResumen.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 2) { // columna Cantidad
                    int cantidad = (int) modeloResumen.getValueAt(row, 2);
                    double precioUnitario = (double) modeloResumen.getValueAt(row, 4);
                    double subtotalNuevo = cantidad * precioUnitario;
                    modeloResumen.setValueAt(subtotalNuevo, row, 3);
                    recalcularTotal();
                }
            }
        });

        // ── Etiquetas informativas ────────────────────────────────────────────
        lblMensajeEliminar = new JLabel("Seleccione un producto para eliminar.");
        lblMensajeEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensajeEliminar.setForeground(Color.RED);

        lblMensajeEditar = new JLabel("Para editar la cantidad, seleccione la celda correspondiente.");
        lblMensajeEditar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensajeEditar.setForeground(Color.BLUE);

        JPanel panelMensajes = new JPanel(new GridLayout(2, 1, 5, 5));
        panelMensajes.setBackground(Color.WHITE);
        panelMensajes.add(lblMensajeEliminar);
        panelMensajes.add(lblMensajeEditar);

        // ── Botones y total ─────────────────────────────────────────────────
        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(rosaAcento);

        JButton btnConfirmar = new JButton("Confirmar Orden");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirmar.setBackground(amarilloAcento);
        btnConfirmar.setForeground(Color.BLACK);
        btnConfirmar.putClientProperty("JButton.buttonType", "roundRect");
        btnConfirmar.addActionListener(e -> completarOrden());

        JButton btnEliminar = new JButton("Eliminar Producto");
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.setBackground(Color.RED);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.addActionListener(e -> {
            int fila = tablaResumen.getSelectedRow();
            if (fila >= 0) {
                modeloResumen.removeRow(fila);
                recalcularTotal();
            } else {
                lblMensajeEliminar.setText("❌ Selecciona un producto para eliminar.");
            }
        });

        // ── Reset mensaje al seleccionar fila ───────────────────────────────
        tablaResumen.getSelectionModel().addListSelectionListener(e -> {
            lblMensajeEliminar.setText("Seleccione un producto para eliminar.");
        });

        // ── Panel bottom ────────────────────────────────────────────────────
        JPanel bottom = new JPanel(new GridLayout(4, 1, 10, 10));
        bottom.setBackground(Color.WHITE);
        bottom.add(panelMensajes);
        bottom.add(lblTotal);
        bottom.add(btnConfirmar);
        bottom.add(btnEliminar);

        panelResumen.add(new JScrollPane(tablaResumen), BorderLayout.CENTER);
        panelResumen.add(bottom, BorderLayout.SOUTH);

        add(panelResumen, BorderLayout.EAST);
    }

    // ─────────────────────────────────────────────────────────────────────────
    public void actualizar() { cargarProductos(); }

    public void cargarProductos() {
        List<MProductos> lista;
        if (filtroActual.equals("Todos")) lista = CVentas.cargarProductos();
        else lista = CVentas.cargarProductosPorCategoria(filtroActual);
        mostrarProductos(lista);
    }

    private void mostrarProductos(List<MProductos> lista) {
        panelGridProductos.removeAll();

        DayOfWeek hoy = LocalDate.now().getDayOfWeek();
        boolean esFinDeSemana = (hoy == DayOfWeek.SATURDAY || hoy == DayOfWeek.SUNDAY);

        int contador = 0;

        for (MProductos p : lista) {
            if (!p.isActivo()) continue;
            if (p.getNombre().equalsIgnoreCase("Menudo") && !esFinDeSemana) continue;
            if (!filtroActual.equals("Todos") && !p.getCategoria().equalsIgnoreCase(filtroActual)) continue;

            panelGridProductos.add(crearTarjetaProducto(p));
            contador++;
        }

        int columnas = 3;
        int filas = (int) Math.ceil(contador / (double) columnas);
        int altura = filas * 280;

        panelGridProductos.setPreferredSize(new Dimension(700, altura));

        panelGridProductos.revalidate();
        panelGridProductos.repaint();
    }

    private JPanel crearTarjetaProducto(MProductos p) {
        if (!p.isActivo()) return null;

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(180, 250));

        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        Image img = ImagenUtil.cargar(p.getUrlFoto(), 150, 110);
        if (img != null) lblImagen.setIcon(new ImageIcon(img));
        else {
            lblImagen.setText("📷");
            lblImagen.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        }

        JLabel lblNombre = new JLabel(p.getNombre(), SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(azulPrincipal);

        JLabel lblDescripcion = new JLabel("<html><center>" + p.getDescripcion() + "</center></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescripcion.setForeground(Color.GRAY);
        lblDescripcion.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblPrecio = new JLabel("$" + p.getPrecio(), SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPrecio.setForeground(amarilloAcento);

        JPanel panelInfo = new JPanel(new GridLayout(3, 1, 2, 2));
        panelInfo.setBackground(Color.WHITE);
        panelInfo.add(lblNombre);
        panelInfo.add(lblDescripcion);
        panelInfo.add(lblPrecio);

        card.add(lblImagen, BorderLayout.NORTH);
        card.add(panelInfo, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(255, 240, 245)); }
            @Override public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); }
            @Override public void mouseClicked(MouseEvent e) { agregarProducto(p.getIdProducto(), p.getNombre(), p.getPrecio()); }
        });

        return card;
    }

    public void agregarProducto(int id, String nombre, double precio) {
        for (int i = 0; i < modeloResumen.getRowCount(); i++) {
            if ((int) modeloResumen.getValueAt(i, 0) == id) {
                int cantidad = (int) modeloResumen.getValueAt(i, 2) + 1;
                modeloResumen.setValueAt(cantidad, i, 2);
                double precioUnitario = (double) modeloResumen.getValueAt(i, 4);
                modeloResumen.setValueAt(cantidad * precioUnitario, i, 3);
                recalcularTotal();
                return;
            }
        }
        modeloResumen.addRow(new Object[]{id, nombre, 1, precio, precio});
        recalcularTotal();
    }

    private void recalcularTotal() {
        total = 0;
        for (int i = 0; i < modeloResumen.getRowCount(); i++) {
            total += (double) modeloResumen.getValueAt(i, 3);
        }
        lblTotal.setText("TOTAL: $" + String.format("%.2f", total));
    }

    private void completarOrden() {
        if (modeloResumen.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la orden.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idPedido = CVentas.registrarPedido(total, idEmpleadoSesion);
            if (idPedido == 0) {
                JOptionPane.showMessageDialog(this, "No se pudo generar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < modeloResumen.getRowCount(); i++) {
                MDetallePedido detalle = new MDetallePedido();
                detalle.setIdPedido(idPedido);
                detalle.setIdProducto((int) modeloResumen.getValueAt(i, 0));
                detalle.setCantidad((int) modeloResumen.getValueAt(i, 2));
                detalle.setSubtotal((double) modeloResumen.getValueAt(i, 3));
                CVentas.registrarDetalle(detalle);
            }
            JOptionPane.showMessageDialog(this, "Orden completada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            imprimirTicket(idPedido);
            limpiarOrden();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void imprimirTicket(int idPedido) {

    
    		try {
    		    Connection Conexion = ConexionBDSQLServer.GetConexion();

    		    String reportPath = "src/Taqueria/Ticket.jrxml";

    		    JasperReport report = JasperCompileManager.compileReport(reportPath);

    		    Map<String, Object> parametros = new HashMap<>();
    		    parametros.put("idPedido", idPedido);

    		    JasperPrint print = JasperFillManager.fillReport(report, parametros, Conexion);

    		    if (print.getPages() == null || print.getPages().isEmpty()) {
    		        JOptionPane.showMessageDialog(null, "No hay datos para imprimir",
    		                "Aviso", JOptionPane.INFORMATION_MESSAGE);
    		        return;
    		    }

    		    JasperViewer.viewReport(print, false);

    		    // 🖨️ Imprimir ticket
    		   // JasperPrintManager.printReport(print, false);

    		} catch (JRException e) {
    		    e.printStackTrace();
    		}
    	}		
	

	public void limpiarOrden() {
        modeloResumen.setRowCount(0);
        total = 0;
        lblTotal.setText("TOTAL: $0.00");
    }

    public double getTotal() { return total; }

    public List<MDetallePedido> getDetallePedido(int idPedido) {
        List<MDetallePedido> lista = new ArrayList<>();
        for (int i = 0; i < modeloResumen.getRowCount(); i++) {
            MDetallePedido d = new MDetallePedido();
            d.setIdPedido(idPedido);
            d.setIdProducto((int) modeloResumen.getValueAt(i, 0));
            d.setCantidad((int) modeloResumen.getValueAt(i, 2));
            d.setSubtotal((double) modeloResumen.getValueAt(i, 3));
            lista.add(d);
        }
        return lista;
    }
    
    
}