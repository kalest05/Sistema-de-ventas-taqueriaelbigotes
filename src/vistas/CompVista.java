package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;

public class CompVista extends JFrame {

    private static final long serialVersionUID = 1L;

    
    protected Color azulPrincipal = new Color(31, 42, 68);
    protected Color rosaAcento = new Color(233, 30, 99);
    protected Color amarilloAcento = new Color(255, 193, 7);
    protected Color fondoClaro = new Color(245, 247, 250);
    protected JPanel contentPane;
    protected JPanel panelHeader;
    protected JPanel panelSidebar;
    protected JPanel panelContenido;
    protected JButton btnOrden;
    protected JButton btnInventario;
    protected JButton btnEmpleados;
    protected JButton btnReportes;

    protected JLabel lblUsuario;
    protected JLabel lblPuesto;
    public static void main(String[] args) {

        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            CompVista vista = new CompVista("Sistema", "Vista Genérica");
            vista.setVisible(true);
        });
    }
    public CompVista(String tituloVentana, String tituloHeader) {

        setTitle(tituloVentana);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, 800);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(fondoClaro);
        setContentPane(contentPane);

        crearHeader(tituloHeader);
        crearSidebar();
        crearPanelContenido();
    }

   
    private void crearHeader(String titulo) {

        panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(rosaAcento);

        JLabel lblFecha = new JLabel(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy HH:mm")));
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFecha.setForeground(Color.GRAY);

        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(lblFecha, BorderLayout.EAST);

        contentPane.add(panelHeader, BorderLayout.NORTH);
    }

    
    private void crearSidebar() {

        panelSidebar = new JPanel();
        panelSidebar.setPreferredSize(new Dimension(230, 0));
        panelSidebar.setBackground(azulPrincipal);
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        btnOrden = crearBotonMenu("Orden");
        btnInventario = crearBotonMenu("Inventario");
        btnEmpleados = crearBotonMenu("Empleados");
        btnReportes = crearBotonMenu("Reportes");

        panelSidebar.add(btnOrden);
        panelSidebar.add(Box.createVerticalStrut(20));
        panelSidebar.add(btnInventario);
        panelSidebar.add(Box.createVerticalStrut(20));
        panelSidebar.add(btnEmpleados);
        panelSidebar.add(Box.createVerticalStrut(20));
        panelSidebar.add(btnReportes);
        panelSidebar.add(Box.createVerticalGlue());

        lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(Color.WHITE);

        lblPuesto = new JLabel("Rol");
        lblPuesto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPuesto.setForeground(Color.LIGHT_GRAY);

        panelSidebar.add(lblUsuario);
        panelSidebar.add(lblPuesto);

        contentPane.add(panelSidebar, BorderLayout.WEST);
    }

    
    protected JButton crearBotonMenu(String texto) {

        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 15, 10, 15));
        btn.setBackground(rosaAcento);
        btn.setForeground(Color.WHITE);
        btn.putClientProperty("JButton.buttonType", "roundRect");

        return btn;
    }

    
    private void crearPanelContenido() {

        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(fondoClaro);
        panelContenido.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPane.add(panelContenido, BorderLayout.CENTER);
    }

    public JPanel getPanelContenido() {
        return panelContenido;
    }

  

    public void setUsuario(String nombre, String rol) {
        lblUsuario.setText(nombre);
        lblPuesto.setText(rol);
    }

    public JButton getBtnOrden() {
        return btnOrden;
    }

    public JButton getBtnInventario() {
        return btnInventario;
    }

    public JButton getBtnEmpleados() {
        return btnEmpleados;
    }

    public JButton getBtnReportes() {
        return btnReportes;
    }
}
