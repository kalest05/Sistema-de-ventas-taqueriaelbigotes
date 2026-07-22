package conexion;
/**
 * Descripción: Paquete para gestionar la conexión a base de datos SQL Server.
 * <p>
 * Esta clase permite establecer una conexión con un servidor SQL Server
 * utilizando parámetros externos definidos en un archivo de propiedades ubicado
 * en {@code src/properties/configSqlServer.properties}.
 * </p>
 * 
 * <p>
 * Archivo de configuración esperado con claves:
 * <ul>
 *   <li>{@code servidor.usuario}</li>
 *   <li>{@code servidor.password}</li>
 *   <li>{@code bd.name}</li>
 *   <li>{@code servidor.control}</li>
 *   <li>{@code ip}</li>
 *   <li>{@code servidor.port}</li>
 * </ul>
 * </p>
 * 
 * @author Luz Maria Garcia Encines - 230130052
 * @author Aracely Berenice Sanchez Montoya - 230130064
 * @version 2.0
 * @grupo ISC 6-1
 */


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que gestiona la conexión a una base de datos SQL Server utilizando
 * parámetros de configuración externos.
 */
public class ConexionBDSQLServer {

    /** Objeto para almacenar las propiedades de configuración de la conexión */
	private static Properties Propiedades; // Variable de tipo properties

    /** Lector para acceder al archivo de propiedades desde disco */
	private static FileReader RutaFisica; // Variable de Tipo FileReader Lee el archivo
	
    /** Conexión activa a la base de datos */
	public static Connection Conexion = null;
	
    /** Usuario de la base de datos */
	private static String usuario;
	
    /** Contraseña de la base de datos */
	private static String pwd;
	
    /** Nombre de la base de datos */
	private static String db;
	
    /** Dirección IP o nombre del host */
	private static String ip;
	
    /** Controlador JDBC */
	private static String jdbc;
	
    /** Puerto del servidor */
	private static String port;

	   /**
     * Carga los parámetros de conexión desde el archivo de configuración.
     * El archivo debe contener las siguientes claves:
     * <ul>
     *   <li>servidor.usuario</li>
     *   <li>servidor.password</li>
     *   <li>bd.name</li>
     *   <li>servidor.control</li>
     *   <li>ip</li>
     *   <li>servidor.port</li>
     * </ul>
     */
	public static void GetParametros() {
		// Se inicializa la variable Properties
		Propiedades = new Properties();

		try {// Se inicializa la FileReader pasndo como parametro
			
			RutaFisica = new FileReader("src\\properties\\configSqlServer.properties");				

		} catch (FileNotFoundException eRuta) {
			// TODO Auto-generated catch block
			eRuta.printStackTrace();
		}

		try {
			Propiedades.load(RutaFisica);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		usuario = Propiedades.getProperty("servidor.usuario");
		pwd = Propiedades.getProperty("servidor.password");
		db = Propiedades.getProperty("bd.name");
		jdbc = Propiedades.getProperty("servidor.control");
		ip = Propiedades.getProperty("ip");
		port = Propiedades.getProperty("servidor.port");
	}

	  /**
     * Establece y devuelve una conexión activa con la base de datos SQL Server.
     * <p>
     * Los parámetros necesarios son cargados mediante {@link #GetParametros()}.
     * </p>
     *
     * @return Objeto {@link Connection} si la conexión fue exitosa; de lo contrario, {@code null}.
     */
	public static Connection GetConexion() {
		// Se ejecuta el metodo que optiene los parametros de el archivo propertie
		GetParametros();  // Cargar parámetros desde el archivo .properties
		Conexion = null;
		
		try {
			
            // Cargar el driver JDBC de SQL Server
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// esta cadena permite determinar configuraciones basicas.
			// String DbUrl = jdbc + "://" + ip + "\\" + NombreServer + ";databaseName=" +
			// db + ";";
			
            // Construir la URL de conexión
			String DbUrl = jdbc + "://" + ip + ":" + port + ";databaseName=" + db + ";";
			///System.out.println(DbUrl);
		
            // Establecer la conexión
			Conexion = DriverManager.getConnection(DbUrl, usuario, pwd);
			
		} catch (SQLException ex) {
			System.err.println("Error." + ex.getMessage());
			Conexion = null;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Conexion = null;
		}
		
		return Conexion;
	}
}