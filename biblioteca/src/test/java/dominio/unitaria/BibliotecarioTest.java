package dominio.unitaria;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {
	
	private static final String ISBN_PALINDROMO_ALFANUMERICO = "a1b22b1a";
	private static final String ISBN_NO_PALINDROMO_ALFANUMERICO = "a1b2b21a";
	private static final String ISBN_CARACTERES_NUMERICOS_SUMAN_MAS_DE_30 = "A987B654";
	private static final String ISBN_CARACTERES_NUMERICOS_SUMAN_MENOS_DE_30 = "A123B456";
	private static final String FECHA_PRESTAMO = "2019-07-01";
	private static final String FECHA_ENTREGA_15_DIAS_HABILES = "2019-07-17";
	private static final String FECHA_PRESTAMO_TERMINA_DOMINGO = "2017-05-26";
	private static final String FECHA_ENTREGA_15_DIAS_HABILES_TERMINA_DOMINGO = "2017-06-12";

	@Test
	public void esPrestadoTest() {
		
		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();
		
		Libro libro = libroTestDataBuilder.build(); 
		
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(libro);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act 
		boolean esPrestado =  bibliotecario.esPrestado(libro.getIsbn());
		
		//assert
		assertTrue(esPrestado);
	}
	
	@Test
	public void libroNoPrestadoTest() {
		
		// arrange
		LibroTestDataBuilder libroTestDataBuilder = new LibroTestDataBuilder();
		
		Libro libro = libroTestDataBuilder.build(); 
		
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		when(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn())).thenReturn(null);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		// act 
		boolean esPrestado =  bibliotecario.esPrestado(libro.getIsbn());
		
		//assert
		assertFalse(esPrestado);
	}
	
	@Test
	public void isbnEsPalindromo(){
		
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		//act
		boolean esPalindromo = bibliotecario.libroEsPalindromo(ISBN_PALINDROMO_ALFANUMERICO);
		
		//assert
		assertTrue(esPalindromo);
	}
	
	@Test
	public void isbnNoEsPalindromo(){
		
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		
		//act
		boolean esPalindromo = bibliotecario.libroEsPalindromo(ISBN_NO_PALINDROMO_ALFANUMERICO);
		
		//assert
		assertFalse(esPalindromo);
	}
	
	@Test
	public void calcularFechaDeEntregaMaximo15Dias() throws ParseException{
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaPrestamo = formatoFecha.parse(FECHA_PRESTAMO);
		
		// act
		Date fechaEntrega = bibliotecario.calcularFechaDeEntrega(ISBN_CARACTERES_NUMERICOS_SUMAN_MAS_DE_30, fechaPrestamo);
		
		//assert
		
		assertEquals(FECHA_ENTREGA_15_DIAS_HABILES, formatoFecha.format(fechaEntrega));
	}
	
	@Test
	public void calcularFechaDeEntregaMaximo15DiasEnDomingo() throws ParseException{
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaPrestamo = formatoFecha.parse(FECHA_PRESTAMO_TERMINA_DOMINGO);
		
		// act
		Date fechaEntrega = bibliotecario.calcularFechaDeEntrega(ISBN_CARACTERES_NUMERICOS_SUMAN_MAS_DE_30, fechaPrestamo);
		
		//assert
		
		assertEquals(FECHA_ENTREGA_15_DIAS_HABILES_TERMINA_DOMINGO, formatoFecha.format(fechaEntrega));
	}
	
	@Test
	public void calcularFechaDeEntregaNoCumpleConRequisitos() throws ParseException{
		// arrange
		RepositorioPrestamo repositorioPrestamo = mock(RepositorioPrestamo.class);
		RepositorioLibro repositorioLibro = mock(RepositorioLibro.class);
		
		Bibliotecario bibliotecario = new Bibliotecario(repositorioLibro, repositorioPrestamo);
		DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaPrestamo = formatoFecha.parse(FECHA_PRESTAMO);
		
		// act
		Date fechaEntrega = bibliotecario.calcularFechaDeEntrega(ISBN_CARACTERES_NUMERICOS_SUMAN_MENOS_DE_30, fechaPrestamo);
		
		//assert
		assertNull(fechaEntrega);
	}
}
