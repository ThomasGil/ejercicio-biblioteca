package dominio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String EL_LIBRO_SOLO_PUEDE_USARSE_EN_LA_BIBLIOTECA = "Los libros palindromos solo se pueden usar en la biblioteca";

	private static final int MAXIMO_DIAS_PLAZO_DE_ENTREGA = 14;
	
	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public void prestar(String isbn, String nombreUsuario) {
		
		if (esPrestado(isbn)){
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}
		if (libroEsPalindromo(isbn)){
			throw new PrestamoException(EL_LIBRO_SOLO_PUEDE_USARSE_EN_LA_BIBLIOTECA);
		}
		
		Date fechaPrestamo = new Date();
		Date fechaEntrega = calcularFechaDeEntrega(isbn, fechaPrestamo);
		Libro libroAPrestar = repositorioLibro.obtenerPorIsbn(isbn);
		Prestamo prestamo = new Prestamo(fechaPrestamo, libroAPrestar, fechaEntrega, nombreUsuario);
		repositorioPrestamo.agregar(prestamo);
	}

	public boolean esPrestado(String isbn) {
		return repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn) != null;
	}
	
	public boolean libroEsPalindromo(String isbn){
		String isbnAlContrario = "";
		for(int i = isbn.length() - 1; i >= 0; i--){
			isbnAlContrario += isbn.charAt(i);
		}
		return isbn.compareTo(isbnAlContrario) == 0;
	}

	public Date calcularFechaDeEntrega(String isbn, Date fechaPrestamo) {
		if (sumaDeCaracteresNumericosEsMayorATreinta(isbn)){
			
			Calendar calendario = Calendar.getInstance();
			calendario.setTime(fechaPrestamo);
			int diasHabilesPrestamo = MAXIMO_DIAS_PLAZO_DE_ENTREGA;
			
			if(esDiaConDosDomingosEnElPlazoMaximo(calendario.get(Calendar.DAY_OF_WEEK))){
				diasHabilesPrestamo += 2;
			} else {
				diasHabilesPrestamo += 3;
			}
			
			calendario.add(Calendar.DAY_OF_MONTH, diasHabilesPrestamo);
			
			if(calendario.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				calendario.add(Calendar.DAY_OF_MONTH, 1);
			}
			return calendario.getTime();
		}
		return null;
	}
	
	private boolean esDiaConDosDomingosEnElPlazoMaximo(int dia){
		return dia == Calendar.MONDAY || dia == Calendar.TUESDAY || dia == Calendar.WEDNESDAY || dia == Calendar.THURSDAY ;
	}
	
	private boolean sumaDeCaracteresNumericosEsMayorATreinta(String isbn){
		int sumaDeCaracteresNumericos = 0;
		for(int i = 0; i < isbn.length(); i++){
			String caracter = "" + isbn.charAt(i);
			try {
				sumaDeCaracteresNumericos += Integer.parseInt(caracter);
			} catch (Exception e){
				sumaDeCaracteresNumericos += 0;
			}
		}
		return sumaDeCaracteresNumericos > 30;
	}
}
