package conexion;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que realiza prueba de la conexión a la base de datos SQL Server.
 * <p>
 * Esta clase que permite conectarse a una base de datos SQL Server utilizando
 * parámetros almacenados en un archivo de configuración.
 * </p>
 * 
 * @author Milagros Guadalupe Camacho Camacho
 * @author Pedro Antonio De los Santos Aguilar
 * 
 * @version 1.0
 * 
 * @since 09-02-2026
 * 
 * @see ConexionBDSQLServer
 * 
 */
public class TestSQLServer {

	/**
	 * Variable Properties para almacenar los parámetros leídos desde el archivo de
	 * configuración
	 */
	private static Properties Propiedades;
	/**
	 * Variable FileReader para leer el archivo físico de Properties y pasa su ruta
	 * a la variable proepiedades
	 */
	private static FileReader RutaFisica;
	/**
	 * Variable que permite la conexión a la base de datos
	 */
	public static Connection Conexion = null;
	// Variables globales que se obtendran valores desde archivo propeties
	private static String usuario;
	private static String pwd;
	private static String db;
	private static String ip;
	private static String jdbc;
	private static String port;
	private static String NombreServer;

	/**
	 * Busca los parametros de conexión.
	 * <p>
	 * Este metodo carga los parámetros de configuración necesarios para conectarse
	 * a una base de datos SQL Server desde un archivo de propiedades ubicado en en
	 * archivo de properties.
	 * </p>
	 * 
	 * <ul>
	 * <b>Parametros: </b>
	 * <li>{@code usuario} - Usuario de la base de datos</li>
	 * <li>{@code pwd} - Contraseña del usuario</li>
	 * <li>{@code db} - Nombre de la base de datos</li>
	 * <li>{@code NombreServer} - Nombre del servidor SQL Server</li>
	 * <li>{@code jdbc} - Protocolo JDBC utilizado {@code jdbc:sqlserver}</li>
	 * <li>{@code port} - Puerto de conexión</li>
	 * <li>{@code ip} - Dirección IP del servidor</li>
	 * </ul>
	 * 
	 * @throws IOException           si ocurre un error al leer el archivo de
	 *                               configuración
	 * 
	 * @throws FileNotFoundException si el archivo no se encuentra en la ruta
	 *                               especificada
	 */
	public static void GetParametros() {
		// Se inicializa la variable Properties
		Propiedades = new Properties();

		try {
			// Se inicializa la FileReader pasando como parametro
			// la ruta fisica del archivo propiertie
			RutaFisica = new FileReader("src\\properties\\configSqlServer.properties");
		} catch (FileNotFoundException ExcRuta) {
			// TODO Auto-generated catch block
			ExcRuta.printStackTrace();
		}

		try {
			Propiedades.load(RutaFisica);
		} catch (FileNotFoundException ExcArchivos) {
			// TODO Auto-generated catch block
			ExcArchivos.printStackTrace();
		} catch (IOException ExcIO) {
			// TODO Auto-generated catch block
			ExcIO.printStackTrace();
		}

		usuario = Propiedades.getProperty("servidor.usuario");
		pwd = Propiedades.getProperty("servidor.password");
		db = Propiedades.getProperty("bd.name");
		NombreServer = Propiedades.getProperty("servidor.nombre");
		jdbc = Propiedades.getProperty("servidor.control");
		port = Propiedades.getProperty("servidor.port");
		ip = Propiedades.getProperty("ip");

	}

	/**
	 * Establece una conexión con una base de datos SQL Server utilizando los
	 * parámetros obtenidos desde un archivo de propiedades.
	 * <p>
	 * Este método realiza las siguientes acciones:
	 * </p>
	 * <ul>
	 * <li>Obtiene los parámetros de conexión mediante el método {@code GetParametros()}.</li>
	 * <li>Carga el driver JDBC para SQL Server.</li>
	 * <li>Construye la URL de conexión con los parámetros obtenidos.</li>
	 * <li>Intenta establecer una conexión con la base de datos.</li>
	 * <li>Si la conexión se establece correctamente, imprime información del driver
	 * y del producto de base de datos.</li>
	 * <li>Cierra la conexión al final del proceso.</li>
	 * </ul>
	 * <p>
	 * <b>En caso de errores de carga del driver o de conexión, se imprimen mensajes de error en consola.</b>
	 * </p>
	 * 
	 * @throws ClassNotFoundException Si el driver JDBC no carga.
	 * @throws SQLException si ocurre un error al intentar conectarse a la bd.
	 */
	public static void GetConexion() {
		// Se ejecuta el metodo que optiene los parametros de el archivo propertie
		GetParametros();
		try {

			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// esta cadena permite determinar configuraciones basi
			String DbUrl = jdbc + "://" + ip + "\\" + NombreServer + ";databaseName=" + db + ";";
			System.out.println(DbUrl);
			DbUrl = jdbc + "://" + ip + ":" + port + ";" + "databaseName=" + db + ";";
			System.out.println(DbUrl);
			Conexion = DriverManager.getConnection(DbUrl, usuario, pwd);

			if (Conexion != null) {

				DatabaseMetaData dm = Conexion.getMetaData();

				System.out.println("Driver name: " + dm.getDriverName());

				System.out.println("Driver version: " + dm.getDriverVersion());

				System.out.println("Product name: " + dm.getDatabaseProductName());

				System.out.println("Product version: " + dm.getDatabaseProductVersion());

			}
		} catch (SQLException ex) {
			System.out.println("Error." + ex.getMessage());
		}

		try {
			Conexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		GetConexion();

	}

}