package accesoDatos;

import java.util.HashMap;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import auxiliares.ApiRequests;
import logicaRefrescos.Deposito;

public class AccesoJSONRemoto {

	ApiRequests encargadoPeticiones;
	private String SERVER_PATH, GET_PLAYER, SET_PLAYER; // Datos de la conexion

	public AccesoJSONRemoto() {

		encargadoPeticiones = new ApiRequests();

		SERVER_PATH = "http://localhost/Jaime/ServidoresJSON/pruebasJSON/";
		GET_PLAYER = "leeJugadores.php";
		SET_PLAYER = "escribirJugador.php";

	}

	public HashMap<Integer, Deposito> lee() {

		HashMap<Integer, Deposito> auxhm = new HashMap<Integer, Deposito>();

		try {

			System.out.println("---------- Leemos datos de JSON --------------------");

			System.out.println("Lanzamos peticion JSON para jugadores");

			String url = SERVER_PATH + GET_PLAYER; // Sacadas de configuracion

			// System.out.println("La url a la que lanzamos la petici�n es " +
			// url); // Traza para pruebas

			String response = encargadoPeticiones.getRequest(url);

			// System.out.println(response); // Traza para pruebas

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
							valor = Integer.parseInt(row.get("numero").toString());
							cantidad = Integer.parseInt(row.get("equipo").toString());
							nuevoDeposito = new Deposito(nombre, valor, cantidad);

							auxhm.put(valor, nuevoDeposito);
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
			System.out.println("Ha ocurrido un error en la busqueda de datos");

			e.printStackTrace();

			System.exit(-1);
		}

		return auxhm;
	}

	public void anadirJugadorJSON(Deposito auxJugador) {

		try {
			JSONObject objDeposito = new JSONObject();
			JSONObject objPeticion = new JSONObject();

			objDeposito.put("nombre", auxJugador.getNombreMoneda());
			objDeposito.put("valor", auxJugador.getValor());
			objDeposito.put("cantidad", auxJugador.getCantidad());

			// Tenemos el jugador como objeto JSON. Lo a�adimos a una peticion
			// Lo transformamos a string y llamamos al
			// encargado de peticiones para que lo envie al PHP

			objPeticion.put("peticion", "add");
			objPeticion.put("jugadorAnnadir", objDeposito);
			
			String json = objPeticion.toJSONString();

			System.out.println("Lanzamos peticion JSON para almacenar un jugador");

			String url = SERVER_PATH + SET_PLAYER;

			System.out.println("La url a la que lanzamos la petici�n es " + url);
			System.out.println("El json que enviamos es: ");
			System.out.println(json);
			//System.exit(-1);

			String response = encargadoPeticiones.postRequest(url, json);
			
			System.out.println("El json que recibimos es: ");
			
			System.out.println(response); // Traza para pruebas
			System.exit(-1);
			
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
				if (estado.equals("ok")) {

					System.out.println("Almacenado jugador enviado por JSON Remoto");

				} else { // Hemos recibido el json pero en el estado se nos
							// indica que ha habido alg�n error

					System.out.println("Acceso JSON REMOTO - Error al almacenar los datos");
					System.out.println("Error: " + (String) respuesta.get("error"));
					System.out.println("Consulta: " + (String) respuesta.get("query"));

					System.exit(-1);

				}
			}
		} catch (Exception e) {
			System.out.println(
					"Excepcion desconocida. Traza de error comentada en el m�todo 'annadirJugador' de la clase JSON REMOTO");
			// e.printStackTrace();
			System.out.println("Fin ejecuci�n");
			System.exit(-1);
		}

	}

}
