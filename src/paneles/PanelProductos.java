package paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controladores.CProductos;
import util.ImagenUtil;

/*
 * Panel de Productos.
 * Lista todos los productos: activos primero, inactivos al final en gris.
 * Doble clic para editar. No muestra columna urlFoto en la tabla.
 */
public class PanelProductos extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color azulPrincipal = new Color(31, 42, 68);
    private final Color rosaAcento    = new Color(233, 30, 99);
    private final Color fondoClaro    = new Color(245, 247, 250);
    private final Color grisInactivo  = new Color(180, 180, 180);
    private final Color fondoInactivo = new Color(240, 240, 240);

    private JTable            tabla;
    private DefaultTableModel modelo;

    public PanelProductos() {
        setLayout(new BorderLayout());
        setBackground(fondoClaro);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        crearUI();
    }

    public void actualizar() {
        cargarProductos();
    }

    private void crearUI() {
        JLabel lblTitulo = new JLabel("Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(azulPrincipal);

        JLabel lblHint = new JLabel("Doble clic en un producto para editar");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(Color.GRAY);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(fondoClaro);
        panelTop.add(lblTitulo, BorderLayout.WEST);
        panelTop.add(lblHint,   BorderLayout.EAST);

        // Columnas modelo: ID(oculto), Nombre, Precio, Descripción, Estado, activo(oculto)
        // urlFoto NO aparece en la tabla — se carga solo en el modal de edición
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Precio", "Descripción", "Categoría", "Estado", "activo"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(rosaAcento);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.removeColumn(tabla.getColumnModel().getColumn(6)); // ocultar activo
        tabla.removeColumn(tabla.getColumnModel().getColumn(0)); // ocultar ID

        // Renderer para filas inactivas en gris
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

        JButton btnAgregar = new JButton("Agregar Producto");
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

        cargarProductos();
    }

    private void cargarProductos() {
        modelo.setRowCount(0);
        // Retorna: [idProducto, nombre, precio, descripcion, activo(boolean)]
        List<Object[]> lista = CProductos.obtenerTodosProductos();
        for (Object[] p : lista) {
        	boolean activo = (boolean) p[5];

        	modelo.addRow(new Object[]{
        	    p[0],                         // ID
        	    p[1],                         // Nombre
        	    p[2],                         // Precio
        	    p[3],                         // Descripción
        	    p[4],                         // Categoría
        	    activo ? "Activo" : "Inactivo",
        	    activo
                                    
            });
        }
    }

    private void abrirModalEdicion(int filaModelo) {

        int     id          = (int)     modelo.getValueAt(filaModelo, 0);
        String  nombre      = (String)  modelo.getValueAt(filaModelo, 1);
        double  precio      = (double)  modelo.getValueAt(filaModelo, 2);
        String  descripcion = (String)  modelo.getValueAt(filaModelo, 3);
        String  categoriaActual = (String) modelo.getValueAt(filaModelo, 4);
        boolean activo      = (boolean) modelo.getValueAt(filaModelo, 6);

        // Cargar urlFoto desde BD solo para el modal
        String urlFotoActual = CProductos.obtenerUrlFoto(id);

        JTextField txtNombre = new JTextField(nombre, 22);
        JTextField txtPrecio = new JTextField(String.valueOf(precio), 22);

        JTextArea txtDesc = new JTextArea(descripcion, 3, 22);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);

        // 🔥 ComboBox Categoría
        JComboBox<String> cbCategoria =
                new JComboBox<>(new String[]{"Comida", "Bebida"});
        cbCategoria.setSelectedItem(categoriaActual);

        // Imagen preview
        JLabel lblPreview = crearPreviewImagen(urlFotoActual);
        final String[] rutaSeleccionada = {urlFotoActual};

        JButton btnImagen = new JButton("Cambiar imagen...");
        btnImagen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnImagen.addActionListener(ev -> {
            String nombreActual = txtNombre.getText().trim().isEmpty()
                    ? nombre
                    : txtNombre.getText().trim();

            String nueva = seleccionarImagen(nombreActual);
            if (nueva != null) {
                rutaSeleccionada[0] = nueva;
                actualizarPreview(lblPreview, nueva);
            }
        });

        JPanel panelImagen = new JPanel(new BorderLayout(5, 5));
        panelImagen.setBackground(Color.WHITE);
        panelImagen.add(lblPreview, BorderLayout.CENTER);
        panelImagen.add(btnImagen, BorderLayout.SOUTH);

        // 🔥 IMPORTANTE: usar la versión nueva con categoría
        JPanel panel = construirFormulario(
                txtNombre, txtPrecio, txtDesc, cbCategoria, panelImagen);

        String textoEstado = activo ? "Inactivar Producto" : "Reactivar Producto";
        Object[] opciones = {"Guardar", textoEstado, "Cancelar"};

        int resultado = JOptionPane.showOptionDialog(
                this,
                panel,
                "Editar: " + nombre,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (resultado == 0) { // GUARDAR

            try {
                String nuevoNombre = txtNombre.getText().trim();
                double nuevoPrecio = Double.parseDouble(txtPrecio.getText().trim());
                String nuevaDesc   = txtDesc.getText().trim();
                String nuevaCategoria = cbCategoria.getSelectedItem().toString();

                if (nuevoNombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "El nombre es obligatorio.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 🔥 AHORA GUARDA TAMBIÉN CATEGORÍA
                CProductos.actualizarProducto(
                        id,
                        nuevoNombre,
                        nuevoPrecio,
                        nuevaDesc,
                        rutaSeleccionada[0],
                        nuevaCategoria
                );

                JOptionPane.showMessageDialog(this,
                        "Producto actualizado.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                cargarProductos();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "El precio debe ser un número válido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else if (resultado == 1) { // ACTIVAR / INACTIVAR

            if (activo) {

                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Inactivar \"" + nombre +
                        "\"? Ya no aparecerá en el menú de órdenes.",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    CProductos.inactivarProducto(id);
                    JOptionPane.showMessageDialog(this,
                            "Producto inactivado.",
                            "Listo",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarProductos();
                }

            } else {

                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Reactivar \"" + nombre +
                        "\"? Volverá a aparecer en el menú de órdenes.",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    CProductos.activarProducto(id);
                    JOptionPane.showMessageDialog(this,
                            "Producto reactivado.",
                            "Listo",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarProductos();
                }
            }
        }
    }

    private void abrirModalAgregar() {
        JTextField txtNombre = new JTextField(22);
        JTextField txtPrecio = new JTextField(22);
        JTextArea  txtDesc   = new JTextArea(3, 22);
        JComboBox<String> cbCategoria = new JComboBox<>(new String[]{"Comida", "Bebida"});
        cbCategoria.setSelectedItem("Comida"); // default
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);

        JLabel lblPreview = crearPreviewImagen(null);
        final String[] rutaSeleccionada = {null};

        JButton btnImagen = new JButton("Seleccionar imagen...");
        btnImagen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnImagen.addActionListener(ev -> {
            String nombreActual = txtNombre.getText().trim();
            if (nombreActual.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Escribe el nombre del producto antes de seleccionar la imagen.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nueva = seleccionarImagen(nombreActual);
            if (nueva != null) {
                rutaSeleccionada[0] = nueva;
                actualizarPreview(lblPreview, nueva);
            }
        });

        JPanel panelImagen = new JPanel(new BorderLayout(5, 5));
        panelImagen.setBackground(Color.WHITE);
        panelImagen.add(lblPreview, BorderLayout.CENTER);
        panelImagen.add(btnImagen,  BorderLayout.SOUTH);

        JPanel panel = construirFormulario(txtNombre, txtPrecio, txtDesc, cbCategoria, panelImagen);
        
        
        int resultado = JOptionPane.showConfirmDialog(this, panel, "Agregar Producto",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                String desc   = txtDesc.getText().trim();
                String foto   = rutaSeleccionada[0] != null ? rutaSeleccionada[0] : "";
                String categoria = cbCategoria.getSelectedItem().toString();

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre es obligatorio.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CProductos.agregarProducto(nombre, precio, desc, foto, categoria);
                JOptionPane.showMessageDialog(this, "Producto agregado.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel construirFormulario(JTextField txtNombre, JTextField txtPrecio,
            JTextArea txtDesc, JComboBox<String> cbCategoria,
            JPanel panelImagen) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Nombre:"),      g);
        g.gridx = 1;               panel.add(txtNombre, g);
        g.gridx = 0; g.gridy = 1; panel.add(new JLabel("Precio:"),      g);
        g.gridx = 1;               panel.add(txtPrecio, g);
        g.gridx = 0; g.gridy = 2; panel.add(new JLabel("Descripción:"), g);
        g.gridx = 1;               panel.add(new JScrollPane(txtDesc),   g);
        g.gridx = 0; g.gridy = 3; panel.add(new JLabel("Categoría:"), g);
        g.gridx = 1;               panel.add(cbCategoria, g);
        g.gridx = 0; g.gridy = 4; panel.add(new JLabel("Imagen:"), g);
        g.gridx = 1;               panel.add(panelImagen, g);

        return panel;
    }

    private String seleccionarImagen(String nombreProducto) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen del producto");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Imágenes (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return null;

        File archivo = chooser.getSelectedFile();

        // 🔥 Obtener extensión original
        String nombreOriginal = archivo.getName();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));

        // 🔥 Generar nombre final con extensión correcta
        String nombreArchivo = ImagenUtil.generarNombreArchivo(nombreProducto) + extension;

        File destDir = new File("imagenes"); // usar carpeta fuera de src
        if (!destDir.exists()) destDir.mkdirs();

        File destino = new File(destDir, nombreArchivo);

        try {
            Files.copy(archivo.toPath(), destino.toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo copiar la imagen: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // 🔥 DEVOLVER RUTA REAL RELATIVA
        return "imagenes/" + nombreArchivo;
    }

    private JLabel crearPreviewImagen(String urlFoto) {
        JLabel lbl = new JLabel();
        lbl.setPreferredSize(new Dimension(120, 100));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        actualizarPreview(lbl, urlFoto);
        return lbl;
    }

    private void actualizarPreview(JLabel lbl, String urlFoto) {
        Image img = ImagenUtil.cargar(urlFoto, 120, 100);
        if (img != null) {
            lbl.setIcon(new ImageIcon(img));
            lbl.setText(null);
        } else {
            lbl.setIcon(null);
            lbl.setText("Sin imagen");
        }
    }
}
