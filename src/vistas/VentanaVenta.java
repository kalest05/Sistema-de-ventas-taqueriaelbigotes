package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

import controladores.CVentas;
import modelos.MDetallePedido;
import modelos.MProductos;

/**
 * Clase que representa la interfaz gráfica para el punto de venta.
 * <p>
 * Esta clase extiende JFrame y proporciona una ventana para la visualización,
 * selección y confirmación de productos en una orden de venta. Incluye un menú
 * lateral, una zona de productos en grid, y un resumen de la orden con tabla y
 * total.
 * </p>
 * 
 * @version 1.0
 * @since 2026
 */
public class VentanaVenta extends JFrame {

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
	/**
	 * Variable que determina el color de fondo claro
	 */
	private Color fondoClaro;
	/**
	 * Contenedores
	 */
	private JPanel contentPane;
	private JPanel panelSidebar;
	private JPanel panelHeader;
	private JPanel panelProductos;
	private JPanel panelResumen;
	private JPanel panelGridProductos;
	private JButton btnOrden;
	private JButton btnInventario;
	private JButton btnEmpleados;
	private JButton btnReportes;
	/**
	 * Botón para ver todas las órdenes del día
	 */
	private JButton btnVerOrdenesDia;
	
	private JLabel lblUsuario;
	private JLabel lblPuesto;
	/**
	 * Botón para cerrar sesión
	 */
	private JButton btnCerrarSesion;
	private JTable tablaResumen;
	private DefaultTableModel modeloResumen;
	private JLabel lblTotal;
	private JButton btnConfirmar;
	
	/**
	 * Variable de tipo double para almacenar el total de la orden
	 */
	private double total;
	/**
	 * Variable de tipo entero para almacenar el ID del empleado en sesión
	 */
	private int idEmpleadoSesion;
	/**
	 * Variable de tipo String para almacenar el rol del usuario en sesión
	 */
	private String rolSesion;
	/**
	 * Variable de tipo String para almacenar el nombre del usuario en sesión
	 */
	private String nombreSesion;

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
	        Login login = new Login(); 
	        login.setVisible(true);
	    });
	}

	/**
	 * Constructor de componentes de la interfaz visual.
	 */
	public VentanaVenta(int idEmpleado, String rol, String nombre) {
	    this.idEmpleadoSesion = idEmpleado;
	    this.rolSesion = rol;  
	    this.nombreSesion = nombre; 

	    
	    azulPrincipal = new Color(31, 42, 68);     // Azul profundo
	    rosaAcento = new Color(233, 30, 99);       // Rosa moderno
	    amarilloAcento = new Color(255, 193, 7);   // Amarillo elegante
	    fondoClaro = new Color(245, 247, 250);     // Fondo suave
	    
	    total = 0.0;
	    
	    setTitle("Taquería El Bigotes - Punt de Venta");
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setSize(1400, 800);
	    setLocationRelativeTo(null);
	    
	    contentPane = new JPanel(new BorderLayout());
	    contentPane.setBackground(fondoClaro);
	    setContentPane(contentPane);
	    
	    crearHeader();
	    crearSidebar();
	    crearZonaProductos();
	    crearResumen();
	}


	/**
 * Crea y configura el panel de encabezado.
 */
private void crearHeader() {
    panelHeader = new JPanel(new BorderLayout());
    panelHeader.setBackground(Color.WHITE);
    panelHeader.setBorder(new EmptyBorder(15, 25, 15, 25));
    
    // Título
    JLabel lblTitulo = new JLabel("Taquería El Bigotes");
    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    lblTitulo.setForeground(rosaAcento);
    
    JLabel lblFecha = new JLabel(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy HH:mm")));
    lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lblFecha.setForeground(Color.GRAY);
    
    panelHeader.add(lblTitulo, BorderLayout.WEST);
    panelHeader.add(lblFecha, BorderLayout.EAST);
    
    contentPane.add(panelHeader, BorderLayout.NORTH);
}

	
	/**
	 * Crea y configura el panel lateral (sidebar).
	 */
	private void crearSidebar() {
	    panelSidebar = new JPanel();
	    panelSidebar.setPreferredSize(new Dimension(230, 0));
	    panelSidebar.setBackground(azulPrincipal);
	    panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
	    // Aumentar separación arriba
	    panelSidebar.setBorder(new EmptyBorder(50, 20, 30, 20));
	    
	 // Agregar logo arriba de los botones
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

	    btnOrden = crearBotonMenu("Orden");
	    
	    btnOrden = crearBotonMenu("Orden");
	    btnOrden.addActionListener(e -> new VentanaVenta(idEmpleadoSesion, rolSesion, nombreSesion).setVisible(true));
	    btnInventario = crearBotonMenu("Inventario");
	    btnInventario.addActionListener(e -> new Inventario(rolSesion, nombreSesion).setVisible(true));
	    btnEmpleados = crearBotonMenu("Empleados");
	    btnReportes = crearBotonMenu("Reportes");
	    btnVerOrdenesDia = crearBotonMenu("Ver Órdenes del Día");

	    btnVerOrdenesDia.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            verOrdenesDelDia();
	        }
	    });

	 

	    // Botón cerrar sesión
	    btnCerrarSesion = crearBotonMenu("Cerrar Sesión");
	    btnCerrarSesion.setBackground(amarilloAcento);
	    btnCerrarSesion.setForeground(Color.BLACK);
	    btnCerrarSesion.setPreferredSize(new Dimension(150, 42)); 
	    btnCerrarSesion.setMaximumSize(new Dimension(150, 42));
	    btnCerrarSesion.addActionListener(e -> cerrarSesion());

	    // Agregar botones con separación
	    panelSidebar.add(btnOrden);
	    panelSidebar.add(Box.createVerticalStrut(15)); // Separación
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
	    panelSidebar.add(Box.createVerticalStrut(30)); // Más separación antes de cerrar sesión
	    panelSidebar.add(btnCerrarSesion);
	    
	    panelSidebar.add(Box.createVerticalGlue());
	    panelSidebar.add(Box.createVerticalGlue());
        panelSidebar.add(Box.createVerticalStrut(15));

	 // Panel contenedor para nombre y puesto
	    JPanel panelInfoUsuario = new JPanel();
	    panelInfoUsuario.setLayout(new BoxLayout(panelInfoUsuario, BoxLayout.Y_AXIS));
	    panelInfoUsuario.setBackground(new Color(245, 245, 220)); // Beige suave
	    panelInfoUsuario.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding interno
	    panelInfoUsuario.setMaximumSize(new Dimension(200, 60)); // Tamaño fijo para el recuadro
	    panelInfoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

	    lblUsuario = new JLabel(nombreSesion); // Nombre del empleado
	    lblUsuario.setForeground(Color.BLACK); // Negro para nombre
	    lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Fuente más grande
	    lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

	    lblPuesto = new JLabel(rolSesion); // Puesto (rol)
	    lblPuesto.setForeground(new Color(0, 51, 102)); // Azul oscuro para puesto
	    lblPuesto.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Fuente más pequeña
	    lblPuesto.setAlignmentX(Component.CENTER_ALIGNMENT);
	    

	    panelInfoUsuario.add(lblUsuario);
	    panelInfoUsuario.add(Box.createVerticalStrut(5)); // Separación entre labels
	    panelInfoUsuario.add(lblPuesto);

	    panelSidebar.add(panelInfoUsuario);
	    contentPane.add(panelSidebar, BorderLayout.WEST);
	}
	/**
	 * Muestra una ventana con todas las órdenes del día.
	 */
	private void verOrdenesDelDia() {
	    // Crear una nueva ventana para mostrar las órdenes
	    JFrame ventanaOrdenes = new JFrame("Órdenes del Día");
	    ventanaOrdenes.setSize(800, 600);
	    ventanaOrdenes.setLocationRelativeTo(this);
	    ventanaOrdenes.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    JPanel panel = new JPanel(new BorderLayout());
	    ventanaOrdenes.setContentPane(panel);
	    
	    // Crear tabla para órdenes
	    DefaultTableModel modeloOrdenes = new DefaultTableModel(
	            new Object[] { "ID Pedido", "Fecha", "Total", "Empleado" }, 0);
	    JTable tablaOrdenes = new JTable(modeloOrdenes);
	    
	    // Obtener órdenes del día desde el controlador
	    List<Object[]> ordenes = CVentas.obtenerOrdenesDelDia();
	    
	    for (Object[] orden : ordenes) {
	        modeloOrdenes.addRow(orden);
	    }
	    
	    // Agregar listener para selección de fila
	    tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
	        if (!e.getValueIsAdjusting()) {
	            int filaSeleccionada = tablaOrdenes.getSelectedRow();
	            if (filaSeleccionada != -1) {
	                int idPedido = (int) modeloOrdenes.getValueAt(filaSeleccionada, 0);
	                // Abrir ventana de detalles
	                DetallesOrden ventanaDetalles = new DetallesOrden(idPedido);
	                ventanaDetalles.setVisible(true);
	            }
	        }
	    });
	    
	    JScrollPane scroll = new JScrollPane(tablaOrdenes);
	    panel.add(scroll, BorderLayout.CENTER);
	    
	    ventanaOrdenes.setVisible(true);
	}
	/**
	 * Crea un botón para el menú lateral.
	 */
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
	
	/**
	 * Cierra la sesión y regresa al login.
	 */
	private void cerrarSesion() {
	    int opcion = javax.swing.JOptionPane.showConfirmDialog(
	            this,
	            "¿Estás seguro de que quieres cerrar sesión?",
	            "Confirmar",
	            javax.swing.JOptionPane.YES_NO_OPTION);
	    
	    if (opcion == javax.swing.JOptionPane.YES_OPTION) {
	        // Cerrar ventana actual
	        this.dispose();
	        
	        // Abrir login
	        Login login = new Login();
	        login.setVisible(true);
	    }
	}

	/**
	 * Crea y configura la zona de productos.
	 */
	private void crearZonaProductos() {
		panelProductos = new JPanel(new BorderLayout());
		panelProductos.setBackground(fondoClaro);
		panelProductos.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		JLabel lblMenu = new JLabel("Menú de Productos");
		lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblMenu.setForeground(azulPrincipal);
		
		panelGridProductos = new JPanel(new GridLayout(0, 3, 25, 25));
		panelGridProductos.setBackground(fondoClaro);
		
		JScrollPane scroll = new JScrollPane(
				panelGridProductos,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		scroll.setBorder(null);
		
		panelProductos.add(lblMenu, BorderLayout.NORTH);
		panelProductos.add(scroll, BorderLayout.CENTER);
		
		contentPane.add(panelProductos, BorderLayout.CENTER);
	}

	/**
	 * Crea una tarjeta para un producto.
	 */
	private JPanel crearTarjetaProducto(MProductos p) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBorder(new EmptyBorder(15, 15, 15, 15));
		card.setBackground(Color.WHITE);
		card.setPreferredSize(new Dimension(200, 160));
		card.putClientProperty("FlatLaf.style", "arc:20");

		JLabel lblImagen = new JLabel();
		lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
		
		try {
			String ruta = p.getUrlFoto();
			
			if (ruta != null && !ruta.isEmpty()) {
				// Quitar "src/" si viene así desde la BD
				if (ruta.startsWith("src/")) {
					ruta = ruta.substring(4);
				}
				
				java.net.URL imgURL = getClass().getClassLoader().getResource(ruta);
				
				if (imgURL != null) {
					ImageIcon icon = new ImageIcon(imgURL);
					Image imgEscalada = icon.getImage()
							.getScaledInstance(150, 130, Image.SCALE_SMOOTH);
					
					lblImagen.setIcon(new ImageIcon(imgEscalada));
				}
			}
		} catch (Exception e) {
			System.out.println("No se pudo cargar imagen: " + e.getMessage());
		}
		
		JLabel lblNombre = new JLabel(p.getNombre(), SwingConstants.CENTER);
		lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblNombre.setForeground(azulPrincipal);
		
		JLabel lblPrecio = new JLabel("$" + p.getPrecio(), SwingConstants.CENTER);
		lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblPrecio.setForeground(amarilloAcento);
		
		JPanel panelInfo = new JPanel(new GridLayout(2, 1));
		panelInfo.setBackground(Color.WHITE);
		panelInfo.add(lblNombre);
		panelInfo.add(lblPrecio);
		
		card.add(lblImagen, BorderLayout.NORTH);
		card.add(panelInfo, BorderLayout.CENTER);
		
		card.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				card.setBackground(new Color(255, 240, 245));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				card.setBackground(Color.WHITE);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				agregarProducto(p.getIdProducto(), p.getNombre(), p.getPrecio());
			}
		});
		
		return card;
	}

	/**
	 * Muestra los productos en el grid.
	 */
	public void mostrarProductos(List<MProductos> lista) {
		panelGridProductos.removeAll();
		
		for (MProductos p : lista) {
			panelGridProductos.add(crearTarjetaProducto(p));
		}
		
		panelGridProductos.revalidate();
		panelGridProductos.repaint();
	}

	/**
	 * Crea y configura el panel de resumen.
	 */
	private void crearResumen() {
		panelResumen = new JPanel(new BorderLayout());
		panelResumen.setPreferredSize(new Dimension(360, 0));
		panelResumen.setBackground(Color.WHITE);
		panelResumen.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		modeloResumen = new DefaultTableModel(
				new Object[] { "ID", "Producto", "Cant.", "Subtotal" }, 0);
		
		tablaResumen = new JTable(modeloResumen);
		tablaResumen.removeColumn(tablaResumen.getColumnModel().getColumn(0));
		tablaResumen.setEnabled(false);
		
		lblTotal = new JLabel("TOTAL: $0.00");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTotal.setForeground(rosaAcento);
		
		btnConfirmar = new JButton("Confirmar Orden");
		btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnConfirmar.setBackground(amarilloAcento);
		btnConfirmar.setForeground(Color.BLACK);
		btnConfirmar.putClientProperty("JButton.buttonType", "roundRect");
		btnConfirmar.addActionListener(e -> completarOrden());
		
		JPanel bottom = new JPanel(new GridLayout(2, 1, 15, 15));
		bottom.setBackground(Color.WHITE);
		bottom.add(lblTotal);
		bottom.add(btnConfirmar);
		
		panelResumen.add(new JScrollPane(tablaResumen), BorderLayout.CENTER);
		panelResumen.add(bottom, BorderLayout.SOUTH);
		
		contentPane.add(panelResumen, BorderLayout.EAST);
	}

	/**
	 * Completa la orden de venta.
	 */
	private void completarOrden() {
		if (modeloResumen.getRowCount() == 0) {
			javax.swing.JOptionPane.showMessageDialog(
					this,
					"No hay productos en la orden.",
					"Aviso",
					javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try {
			int idPedido = CVentas.registrarPedido(getTotal(), idEmpleadoSesion);
			
			if (idPedido == 0) {
				javax.swing.JOptionPane.showMessageDialog(
						this,
						"No se pudo generar el pedido.",
						"Error",
						javax.swing.JOptionPane.ERROR_MESSAGE);
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
			
			javax.swing.JOptionPane.showMessageDialog(
					this,
					"Orden completada correctamente.",
					"Éxito",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
			
			limpiarOrden();
			
		} catch (Exception ex) {
			javax.swing.JOptionPane.showMessageDialog(
					this,
					"Error al completar la orden: " + ex.getMessage(),
					"Error",
					javax.swing.JOptionPane.ERROR_MESSAGE);
			
			ex.printStackTrace();
		}
	}

	/**
	 * Agrega un producto a la orden.
	 */
	public void agregarProducto(int id, String nombre, double precio) {
		boolean encontrado = false;
		
		for (int i = 0; i < modeloResumen.getRowCount(); i++) {
			int idTabla = (int) modeloResumen.getValueAt(i, 0);
			
			if (idTabla == id) {
				int cantidad = (int) modeloResumen.getValueAt(i, 2);
				cantidad++;
				
				double nuevoSubtotal = cantidad * precio;
				
				modeloResumen.setValueAt(cantidad, i, 2);
				modeloResumen.setValueAt(nuevoSubtotal, i, 3);
				
				encontrado = true;
				break;
			}
		}
		
		if (!encontrado) {
			modeloResumen.addRow(new Object[] { id, nombre, 1, precio });
		}
		
		recalcularTotal();
	}

	/**
	 * Recalcula el total de la orden.
	 */
	private void recalcularTotal() {
		total = 0;
		
		for (int i = 0; i < modeloResumen.getRowCount(); i++) {
			total += (double) modeloResumen.getValueAt(i, 3);
		}
		
		lblTotal.setText("TOTAL: $" + String.format("%.2f", total));
	}

	/**
	 * Obtiene el total de la orden.
	 */
	public double getTotal() {
		return total;
	}

	/**
	 * Obtiene la lista de detalles del pedido.
	 */
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

	/**
	 * Limpia la orden actual.
	 */
	public void limpiarOrden() {
		modeloResumen.setRowCount(0);
		total = 0;
		lblTotal.setText("TOTAL: $0.00");
	}
}
