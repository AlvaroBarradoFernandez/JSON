package accesoDatos;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import logicaRefrescos.Deposito;
import logicaRefrescos.Dispensador;

/*
 * Todas los accesos a datos implementan la interfaz de Datos
 */

public class FicherosTexto implements I_Acceso_Datos {

	File fDis; // FicheroDispensadores
	File fDep; // FicheroDepositos

	public FicherosTexto() {
		System.out.println("ACCESO A DATOS - FICHEROS DE TEXTO");
	}

	@Override
	public HashMap<Integer, Deposito> obtenerDepositos() {
		HashMap<Integer, Deposito> depositosCreados = new HashMap();
		fDep = new File("Ficheros\\datos\\depositos.txt");
		Deposito deposito;
		try {
			String cadena;
			FileReader f = new FileReader(fDep);
			BufferedReader b = new BufferedReader(f);
			while ((cadena = b.readLine()) != null) {
				deposito = new Deposito();
				String[] dataFromline = cadena.split(";");
				deposito.setNombreMoneda(dataFromline[0]);
				deposito.setValor(Integer.parseInt(dataFromline[1]));
				deposito.setCantidad(Integer.parseInt(dataFromline[2]));
				depositosCreados.put(deposito.getValor(), deposito);
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return depositosCreados;
	}

	@Override
	public HashMap<String, Dispensador> obtenerDispensadores() {
		HashMap<String, Dispensador> dispensadoresCreados = new HashMap();
		fDep = new File("Ficheros\\datos\\dispensadores.txt");
		Dispensador dispensador;
		try {
			String cadena;
			FileReader f = new FileReader(fDep);
			BufferedReader b = new BufferedReader(f);
			while ((cadena = b.readLine()) != null) {
				dispensador = new Dispensador();
				String[] dataFromline = cadena.split(";");
				dispensador.setClave(dataFromline[0]);
				dispensador.setNombreProducto(dataFromline[1]);
				dispensador.setPrecio(Integer.parseInt(dataFromline[2]));
				dispensador.setCantidad(Integer.parseInt(dataFromline[3]));
				dispensadoresCreados.put(dispensador.getClave(), dispensador);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dispensadoresCreados;

	}

	@Override
	public boolean guardarDepositos(HashMap<Integer, Deposito> depositos) {
		FileWriter fichero;
		try {
			fichero = new FileWriter("Ficheros/datos/depositos.txt");
			BufferedWriter b = new BufferedWriter(fichero);
			for (Entry<Integer, Deposito> entry : depositos.entrySet()) {
				b.write(entry.getValue().getNombreMoneda() + ";");
				b.write(entry.getValue().getValor() + ";");
				b.write(entry.getValue().getCantidad() + ";");
				b.newLine();
				b.flush();
			}
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		boolean todoOK = true;

		return todoOK;

	}

	@Override
	public boolean guardarDispensadores(HashMap<String, Dispensador> dispensadores) {
		FileWriter fichero;
		try {
			fichero = new FileWriter("Ficheros/datos/dispensadores.txt");
			BufferedWriter b = new BufferedWriter(fichero);
			for (Entry<String, Dispensador> entry : dispensadores.entrySet()) {
				b.write(entry.getValue().getClave() + ";");
				b.write(entry.getValue().getNombreProducto() + ";");
				b.write(entry.getValue().getPrecio() + ";");
				b.write(entry.getValue().getCantidad() + ";");
				b.newLine();
				b.flush();
			}
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		boolean todoOK = true;

		return todoOK;
	}

} // Fin de la clase