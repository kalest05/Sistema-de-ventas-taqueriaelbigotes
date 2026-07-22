package vistas;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;

import paneles.PanelDetallesOrden;
import paneles.PanelEmpleados;
import paneles.PanelInventario;
import paneles.PanelOrden;
import paneles.PanelOrdenesDia;
import paneles.PanelProductos;
import paneles.PanelReportes;

/*
 * Ventana base principal.
 * Header + Sidebar + CardLayout central.
 * Botones de Empleados y Agregar Empleado solo visibles para Administrador.
 */
public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal  = new Color(31, 42, 68);
    private final Color rosaAcento     = new Color(233, 30, 99);
    private final Color amarilloAcento = new Color(255, 193, 7);
    private final Color fondoClaro     = new Color(245, 247, 250);

    private final int    idEmpleadoSesion;
    private final String rolSesion;
    private final String nombreSesion;

    private JPanel     contentPane;
    private JPanel     panelCentral;
    private CardLayout cardLayout;

    private PanelOrden           panelOrden;
    private PanelInventario      panelInventario;
    private PanelOrdenesDia      panelOrdenesDia;
    private PanelDetallesOrden   panelDetallesOrden;
    private PanelProductos       panelProductos;
    private PanelEmpleados       panelEmpleados;
    private PanelReportes panelReportes;


    public static final String VISTA_ORDEN            = "orden";
    public static final String VISTA_INVENTARIO       = "inventario";
    public static final String VISTA_ORDENES          = "verOrdenes";
    public static final String VISTA_DETALLES         = "detalles";
    public static final String VISTA_PRODUCTOS        = "productos";
    public static final String VISTA_EMPLEADOS        = "empleados";
    public static final String VISTA_REPORTES         = "reportes";

    public VentanaPrincipal(int idEmpleado, String rol, String nombre) {
        this.idEmpleadoSesion = idEmpleado;
        this.rolSesion        = rol;
        this.nombreSesion     = nombre;

        setTitle("Taquería El Bigotes - Punto de Venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(fondoClaro);
        setContentPane(contentPane);

        crearHeader();
        crearSidebar();
        crearPanelCentral();
        registrarPaneles();

        mostrarPanel(VISTA_ORDEN);
    }

    private void crearHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitulo = new JLabel("Taquería El Bigotes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(rosaAcento);

        JLabel lblFecha = new JLabel(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy HH:mm")));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFecha.setForeground(Color.GRAY);

        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(lblFecha,  BorderLayout.EAST);
        contentPane.add(panelHeader, BorderLayout.NORTH);
    }

    private void crearSidebar() {
        JPanel panelSidebar = new JPanel();
        panelSidebar.setPreferredSize(new Dimension(230, 0));
        panelSidebar.setBackground(new Color(58, 73, 97));
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBorder(new EmptyBorder(50, 20, 30, 20));

        // Logo
        JLabel lblLogo = new JLabel();

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imagenes/nuevologo.png"));
            

            lblLogo.setIcon(icon);

        } catch (Exception e) {
            System.out.println("Logo no encontrado.");
        }

    
        lblLogo.setPreferredSize(new Dimension(200, 75));
        lblLogo.setMaximumSize(new Dimension(200, 75));
        lblLogo.setMinimumSize(new Dimension(200, 75));

        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSidebar.add(lblLogo);
        panelSidebar.add(Box.createVerticalStrut(20));
     // Botones visibles para todos los roles
        agregarBoton(panelSidebar, "Orden", () -> mostrarPanel(VISTA_ORDEN), true); // seleccionado por defecto
        agregarBoton(panelSidebar, "Inventario", () -> mostrarPanel(VISTA_INVENTARIO), false);
        agregarBoton(panelSidebar, "Productos", () -> mostrarPanel(VISTA_PRODUCTOS), false);
        agregarBoton(panelSidebar, "Ver Órdenes del Día", () -> mostrarPanel(VISTA_ORDENES), false);
        agregarBoton(panelSidebar, "Reportes", () -> mostrarPanel(VISTA_REPORTES), false);

        // Botones exclusivos para Administrador
        if ("Administrador".equals(rolSesion)) {
            agregarBoton(panelSidebar, "Empleados", () -> mostrarPanel(VISTA_EMPLEADOS), false);
        }

        // Cerrar sesión
        panelSidebar.add(Box.createVerticalStrut(30));
        JButton btnCerrarSesion = crearBotonMenu("Cerrar Sesión");
        btnCerrarSesion.setBackground(amarilloAcento);
        btnCerrarSesion.setForeground(Color.BLACK);

        // Hover especial: amarillo → rosa
        btnCerrarSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCerrarSesion.setBackground(rosaAcento);
                btnCerrarSesion.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCerrarSesion.setBackground(amarilloAcento);
                btnCerrarSesion.setForeground(Color.BLACK);
            }
        });
        btnCerrarSesion.setMaximumSize(new Dimension(150, 42));
        btnCerrarSesion.setPreferredSize(new Dimension(150, 42));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSidebar.add(btnCerrarSesion);

        // Info de usuario al fondo
        panelSidebar.add(Box.createVerticalGlue());
        panelSidebar.add(Box.createVerticalStrut(15));

        JPanel panelInfoUsuario = new JPanel();
        panelInfoUsuario.setLayout(new BoxLayout(panelInfoUsuario, BoxLayout.Y_AXIS));
        panelInfoUsuario.setBackground(new Color(245, 247, 250));
        panelInfoUsuario.setBorder(BorderFactory.createLineBorder(rosaAcento, 2));
        panelInfoUsuario.setMaximumSize(new Dimension(200, 60));
        panelInfoUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsuario = new JLabel(nombreSesion);
        lblUsuario.setForeground(Color.BLACK);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPuesto = new JLabel(rolSesion);
        lblPuesto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPuesto.setForeground(new Color(233, 30, 99));
        lblPuesto.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInfoUsuario.add(lblUsuario);
        panelInfoUsuario.add(Box.createVerticalStrut(5));
        panelInfoUsuario.add(lblPuesto);
        panelSidebar.add(panelInfoUsuario);

        contentPane.add(panelSidebar, BorderLayout.WEST);
    }

    // Agrega botón al sidebar sin repetir código
    private final List<JButton> botonesSidebar = new ArrayList<>();

    private void agregarBoton(JPanel sidebar, String texto, Runnable accion, boolean seleccionadoInicial) {
        JButton btn = crearBotonMenu(texto);

        // Si es el botón que debe estar seleccionado desde el inicio
        if (seleccionadoInicial) {
            btn.setBackground(new Color(60, 130, 200)); // azul fuerte
        }

        // Acción de mostrar panel y selección
        btn.addActionListener(e -> {
            // Resetear todos los botones al color base
            for (JButton b : botonesSidebar) {
                b.setBackground(rosaAcento);
            }
            // Seleccionar este botón
            btn.setBackground(new Color(60, 130, 200)); // azul fuerte
            accion.run();
        });

        // Agregar al panel y lista
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(15));
        botonesSidebar.add(btn);
    }

    private void crearPanelCentral() {
        cardLayout   = new CardLayout();
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(fondoClaro);
        contentPane.add(panelCentral, BorderLayout.CENTER);
    }

    private void registrarPaneles() {
        panelOrden = new PanelOrden(idEmpleadoSesion, rolSesion, nombreSesion);
        registrarPanel(VISTA_ORDEN, panelOrden);

        panelInventario = new PanelInventario(rolSesion, nombreSesion);
        registrarPanel(VISTA_INVENTARIO, panelInventario);

        panelOrdenesDia = new PanelOrdenesDia(this);
        registrarPanel(VISTA_ORDENES, panelOrdenesDia);

        panelDetallesOrden = new PanelDetallesOrden(this);
        registrarPanel(VISTA_DETALLES, panelDetallesOrden);

        panelProductos = new PanelProductos();
        registrarPanel(VISTA_PRODUCTOS, panelProductos);
        
        panelReportes = new PanelReportes();
        registrarPanel(VISTA_REPORTES, panelReportes);

        // Solo se instancian y registran para administrador
        if ("Administrador".equals(rolSesion)) {
            panelEmpleados = new PanelEmpleados();
            registrarPanel(VISTA_EMPLEADOS, panelEmpleados);

       
        }

    }

    public void registrarPanel(String clave, JPanel panel) {
        panelCentral.add(panel, clave);
    }

    public void mostrarPanel(String clave) {
        if (VISTA_ORDEN.equals(clave))      panelOrden.actualizar();
        if (VISTA_INVENTARIO.equals(clave)) panelInventario.actualizar();
        if (VISTA_ORDENES.equals(clave))    panelOrdenesDia.actualizar();
        if (VISTA_PRODUCTOS.equals(clave))  panelProductos.actualizar();
        if (VISTA_EMPLEADOS.equals(clave) && panelEmpleados != null) panelEmpleados.actualizar();
        if (VISTA_REPORTES.equals(clave)) {
            panelReportes.actualizar();
        }
        cardLayout.show(panelCentral, clave);
    }

    public void mostrarDetalle(int idPedido) {
        panelDetallesOrden.setIdPedido(idPedido);
        cardLayout.show(panelCentral, VISTA_DETALLES);
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        Color colorBase = rosaAcento;                  // rosa original
        Color colorHover = new Color(180, 210, 250);   // azul clarito
        Color colorSeleccionado = new Color(60, 130, 200); // azul fuerte

        btn.setBackground(colorBase);
        btn.setForeground(Color.WHITE);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setMaximumSize(new Dimension(200, 42));

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(colorSeleccionado)) {
                    btn.setBackground(colorHover);
                    btn.setForeground(Color.BLACK);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(colorSeleccionado)) {
                    btn.setBackground(colorBase);
                    btn.setForeground(Color.WHITE);
                }
            }
        });

        return btn;
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que quieres cerrar sesión?", "Confirmar",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            this.dispose();
            new Login().setVisible(true);
        }
    }

    public static void main(String[] args) {
        try { FlatLightLaf.setup(); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
