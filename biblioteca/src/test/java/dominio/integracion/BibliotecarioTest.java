package dominio.integracion;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Bibliotecario;
import dominio.Libro;
import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.LibroTestDataBuilder;

public class BibliotecarioTest {

	private static final String CRONICA_DE_UNA_MUERTA_ANUNCIADA = "Cronica de una muerta anunciada";
	private static final String ISBN_PRESTAMO_MAXIMO_15_DIAS = "A987B765";
	private static final String NOMBRE_USUARIO = "Thomas Gil";
	private static final String ISBN_PALINDROME = "a1b22b1a";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioLibro repositorioLibros;
	private RepositorioPrestamo repositorioPrestamo;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioLibros = sistemaPersistencia.obtenerRepositorioLibros();
		repositorioPrestamo = sistemaPersistencia.obtenerRepositorioPrestamos();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void prestarLibroTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder()
				.conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA)
				.conIsbn(ISBN_PRESTAMO_MAXIMO_15_DIAS)
				.build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), NOMBRE_USUARIO);

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertEquals(NOMBRE_USUARIO, repositorioPrestamo.obtener(libro.getIsbn()).getNombreUsuario());
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));
		Assert.assertNotNull(repositorioPrestamo.obtener(libro.getIsbn()).getFechaEntregaMaxima());

	}
	
	@Test
	public void isbnNoCumpleRequisitosDeEntregaMaxima(){
		// arrange
		Libro libro = new LibroTestDataBuilder()
				.conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA)
				.build();
		repositorioLibros.agregar(libro);
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), NOMBRE_USUARIO);

		// assert
		Assert.assertTrue(blibliotecario.esPrestado(libro.getIsbn()));
		Assert.assertEquals(NOMBRE_USUARIO, repositorioPrestamo.obtener(libro.getIsbn()).getNombreUsuario());
		Assert.assertNotNull(repositorioPrestamo.obtenerLibroPrestadoPorIsbn(libro.getIsbn()));
		Assert.assertNull(repositorioPrestamo.obtener(libro.getIsbn()).getFechaEntregaMaxima());
	}

	@Test
	public void prestarLibroNoDisponibleTest() {

		// arrange
		Libro libro = new LibroTestDataBuilder().conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA).build();
		
		repositorioLibros.agregar(libro);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		// act
		blibliotecario.prestar(libro.getIsbn(), "");
		try {
			
			blibliotecario.prestar(libro.getIsbn(), "");
			fail();
			
		} catch (PrestamoException e) {
			// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE, e.getMessage());
		}
	}
	
	@Test
	public void prestarLibroSoloEnLaBiblioteca(){
		// arrange
		Libro libro = new LibroTestDataBuilder()
				.conTitulo(CRONICA_DE_UNA_MUERTA_ANUNCIADA)
				.conIsbn(ISBN_PALINDROME)
				.build();
		
		repositorioLibros.agregar(libro);
		
		Bibliotecario blibliotecario = new Bibliotecario(repositorioLibros, repositorioPrestamo);

		try {
			// act
			blibliotecario.prestar(libro.getIsbn(), "");
			fail();
			
		} catch (PrestamoException e) {
		// assert
			Assert.assertEquals(Bibliotecario.EL_LIBRO_SOLO_PUEDE_USARSE_EN_LA_BIBLIOTECA, e.getMessage());
		}
	}
}
