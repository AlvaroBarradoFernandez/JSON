package accesoDatos;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import auxiliares.ApiRequests;
import auxiliares.LeeProperties;
import logicaRefrescos.Deposito;
import logicaRefrescos.Dispensador;

public class AccesoJDBC implements I_Acceso_Datos {

	private String driver, urlbd, user, password; // Datos de la conexion
	private Connection conn1;

	ApiRequests encargadoPeticiones;
	private String SERVER_PATH, GET_Dispensadores, SET_Deposito, GET_Depositos; // Datos de la conexion

	public AccesoJDBC() {
		System.out.println("ACCESO A DATOS - Acceso JDBC");

		encargadoPeticiones = new ApiRequests();

		SERVER_PATH = "http://localhost/xampp/MaquinasPHPserver/";
		GET_Depositos = "leeDepositos.php";
		GET_Dispensadores = "leeDispensadores.php";
		SET_Deposito = "escribirJugador.php";

	}

	public HashMap<Integer, Deposito> lee() {

		HashMap<Integer, Deposito> auxhm = new HashMap<Integer, Deposito>();

		try {
			HashMap<String, String> datosConexion;

			LeeProperties properties = new LeeProperties("Ficheros/config/accesoJDBC.properties");
			datosConexion = properties.getHash();

			driver = datosConexion.get("driver");
			urlbd = datosConexion.get("urlbd");
			user = datosConexion.get("user");
			password = datosConexion.get("password");
			conn1 = null;

			Class.forName(driver);
			conn1 = DriverManager.getConnection(urlbd, user, password);
			if (conn1 != null) {
				System.out.println("Conectado a la base de datos");
			}

		} catch (ClassNotFoundException e1) {
			System.out.println("ERROR: No Conectado a la base de datos. No se ha encontrado el driver de conexion");
			// e1.printStackTrace();
			System.out.println("No se ha podido inicializar la maquina\n Finaliza la ejecucion");
			System.exit(1);
		} catch (SQLException e) {
			System.out.println("ERROR: No se ha podido conectar con la base de datos");
			System.out.println(e.getMessage());
			// e.printStackTrace();
			System.out.println("No se ha podido inicializar la maquina\n Finaliza la ejecucion");
			System.exit(1);
		}
		return auxhm;
	}

	public int cerrarConexion() {
		try {
			conn1.close();
			System.out.println("Cerrada conexion");
			return 0;
		} catch (Exception e) {
			System.out.println("ERROR: No se ha cerrado corretamente");
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public HashMap<Integer, Deposito> obtenerDepositos() {
		// leer
		PreparedStatement pstm;
		HashMap<Integer, Deposito> depositos = new HashMap<>();
		try {
			System.out.println("---------- Leemos datos de JSON --------------------");
			System.out.println("Lanzamos peticion JSON para deposito");
			String url = SERVER_PATH + GET_Depositos; // Sacadas de configuracion
			System.out.println("La url a la que lanzamos la petici�n es " + url); // Traza para pruebas
			String response = encargadoPeticiones.getRequest(url);
			System.out.println(response); // Traza para pruebas
			// Parseamos la respuesta y la convertimos en un JSONObject
			JSONObject respuesta = (JSONObject) JSONValue.parse(response.toString());
			if (respuesta == null) { // Si hay alg�n error de parseo (json
										// incorrecto porque hay alg�n caracter
										// raro, etc.) la respuesta ser� null
				System.out.println("El json recibido no es correcto. Finaliza la ejecuci�n");
				System.exit(-1);
			} else { // El JSON recibido es correcto
				// Sera "ok" si todo ha ido bien o "error" si hay alg�n problema
				String estado = (String) respuesta.get("estado");
				// Si ok, obtenemos array de jugadores para recorrer y generar hashmap
				if (estado.equals("ok")) {
					JSONArray array = (JSONArray) respuesta.get("depositos");

					if (array.size() > 0) {

						// Declaramos variables
						Deposito nuevoDeposito;
						int id;
						String nombre;
						int valor;
						int cantidad;

						for (int i = 0; i < array.size(); i++) {
							JSONObject row = (JSONObject) array.get(i);
							id = Integer.parseInt(row.get("id").toString());
							nombre = row.get("nombre").toString();
							valor = Integer.parseInt(row.get("valor").toString());
							cantidad = Integer.parseInt(row.get("cantidad").toString());
							nuevoDeposito = new Deposito(nombre, valor, cantidad);
							depositos.put(valor, nuevoDeposito);
						}

						System.out.println("Acceso JSON Remoto - Leidos datos correctamente y generado hashmap");
						System.out.println();

					} else { // El array de jugadores est� vac�o
						System.out.println("Acceso JSON Remoto - No hay datos que tratar");
						System.out.println();
					}

				} else { // Hemos recibido el json pero en el estado se nos
							// indica que ha habido alg�n error

					System.out.println("Ha ocurrido un error en la busqueda de datos");
					System.out.println("Error: " + (String) respuesta.get("error"));
					System.out.println("Consulta: " + (String) respuesta.get("query"));
					System.exit(-1);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return depositos;
	}

	@Override
	public HashMap<String, Dispensador> obtenerDispensadores() {

		HashMap<String, Dispensador> dispensadores = new HashMap<>();
		try {
			System.out.println("---------- Leemos datos de JSON --------------------");
			System.out.println("Lanzamos peticion JSON para dispensadores");
			String url = SERVER_PATH + GET_Dispensadores; // Sacadas de configuracion
			System.out.println("La url a la que lanzamos la petici�n es " + url); // Traza para pruebas
			String response = encargadoPeticiones.getRequest(url);
			System.out.println(response); // Traza para pruebas
			// Parseamos la respuesta y la convertimos en un JSONObject
			JSONObject respuesta = (JSONObject) JSONValue.parse(response.toString());
			if (respuesta == null) { // Si hay alg�n error de parseo (json
										// incorrecto porque hay alg�n caracter
										// raro, etc.) la respuesta ser� null
				System.out.println("El json recibido no es correcto. Finaliza la ejecuci�n");
				System.exit(-1);
			} else { // El JSON recibido es correcto
				// Sera "ok" si todo ha ido bien o "error" si hay alg�n problema
				String estado = (String) respuesta.get("estado");
				// Si ok, obtenemos array de jugadores para recorrer y generar hashmap
				if (estado.equals("ok")) {
					JSONArray array = (JSONArray) respuesta.get("dispensadores");
					if (array.size() > 0) {
						// Declaramos variables
						Dispensador nuevoDispensador;
						int id;
						String clave;
						String nombre;
						int precio;
						int cantidad;

						for (int i = 0; i < array.size(); i++) {
							JSONObject row = (JSONObject) array.get(i);

							id = Integer.parseInt(row.get("id").toString());
							clave = row.get("clave").toString();
							nombre = row.get("nombre").toString();
							precio = Integer.parseInt(row.get("precio").toString());
							cantidad = Integer.parseInt(row.get("cantidad").toString());
							nuevoDispensador = new Dispensador(clave, nombre, precio, cantidad);

							dispensadores.put(clave, nuevoDispensador);
						}

						System.out.println("Acceso JSON Remoto - Leidos datos correctamente y generado hashmap");
						System.out.println();

					} else { // El array de jugadores est� vac�o
						System.out.println("Acceso JSON Remoto - No hay datos que tratar");
						System.out.println();
					}

				} else { // Hemos recibido el json pero en el estado se nos
							// indica que ha habido alg�n error

					System.out.println("Ha ocurrido un error en la busqueda de datos");
					System.out.println("Error: " + (String) respuesta.get("error"));
					System.out.println("Consulta: " + (String) respuesta.get("query"));
					System.exit(-1);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return dispensadores;
	}

	@Override
	public boolean guardarDepositos(HashMap<Integer, Deposito> depositos) {
		PreparedStatement pstm;
		try {

			for (Deposito unDeposito : depositos.values()) {

				String cargar = "UPDATE depositos SET cantidad = " + unDeposito.getCantidad() + " WHERE valor = "
						+ unDeposito.getValor();
				System.out.println(cargar);
				pstm = conn1.prepareStatement(cargar);
				int rset = pstm.executeUpdate();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean todoOK = true;
		return todoOK;
	}

	@Override
	public boolean guardarDispensadores(HashMap<String, Dispensador> dispensadores) {

		PreparedStatement pstm;
		try {

			for (Dispensador unDispensador : dispensadores.values()) {

				String cargar = "UPDATE dispensadores SET cantidad = " + unDispensador.getCantidad()
						+ " WHERE clave = '" + unDispensador.getClave() + "' ";
				System.out.println(cargar);
				pstm = conn1.prepareStatement(cargar);

				int rset = pstm.executeUpdate();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean todoOK = true;

		return todoOK;
	}

} // Fin de la clase