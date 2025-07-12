package com.literalura.main;

import com.literalura.model.*;
import com.literalura.repository.AutorRepository;
import com.literalura.repository.LibroRepository;
import com.literalura.service.ConsumoAPI;
import com.literalura.service.ConvierteDatos;
import com.literalura.util.Color;
import com.literalura.util.ValidadorEntrada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Principal {

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;


    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private Color color = new Color();

    public void mostrarMenuConLogo() {
        String logo = """
                 ______       __         __                            \s
                |      .---.-|  |_.---.-|  .-----.-----.-----.         \s
                |   ---|  _  |   _|  _  |  |  _  |  _  |  _  |         \s
                |______|___._|____|___._|__|_____|___  |_____|         \s
                 _____   __ __              _____|_____|               \s
                |     |_|__|  |_.-----.----|   _   |  .--.--.----.---.-.
                |       |  |   _|  -__|   _|       |  |  |  |   _|  _  |
                |_______|__|____|_____|__| |___|___|__|_____|__| |___._|
                """;
        System.out.println(color.CYAN + logo + color.RESET);
    }

    public void mostrarMenu() {
        ValidadorEntrada validador = new ValidadorEntrada();
        int opcion;
        do {
            System.out.println(color.MAGENTA + "\n******************************************************************");
            System.out.println("***    BIENVENIDOS(AS)  AL  CATALOGO  DE  LIBROS  LITERALURA   ***");
            System.out.println("******************************************************************\n" + color.RESET);
            System.out.println(color.AZUL + "1. BUSCAR LIBROS POR TITULO");
            System.out.println("2. BUSCAR LIBROS POR AUTOR");
            System.out.println("3. VER LIBROS REGISTRADOS");
            System.out.println("4. VER AUTORES REGISTRADOS");
            System.out.println("5. VER AUTORES REGISTRADOS VIVOS POR AÑO");
            System.out.println("6. VER LIBROS REGISTRADOS POR IDIOMA");
            System.out.println("7. BUSCAR LOS TOP 10 LIBROS MAS DESCARGADOS");
            System.out.println("0. SALIR\n" + color.RESET);
            System.out.println(color.MAGENTA + "******************************************************************\n" + color.RESET);
            System.out.println("SELECCIONE UNA OPCION: ");

            opcion = validador.leerOpcion(teclado, 0, 7);

            switch (opcion) {
                case 1 -> buscarPorTitulo();
                case 2 -> buscarPorAutor();
                case 3 -> listarLibrosRegistrados();
                case 4 -> listarAutoresRegistrados();
                case 5 -> listarAutoresVivosEn();
                case 6 -> listarLibrosPorIdioma();
                case 7 -> buscarTop10Descargados();
                case 0 -> {
                    System.out.println(color.VERDE + "\n******************************************************************");
                    System.out.println("***            GRACIAS POR UTILIZAR NUESTRO CATALOGO            **");
                    System.out.println("*** PIENSA ANTES DE HABLAR, LEE ANTES DE PENSAR - Fran Lebowitz **");
                    System.out.println("******************************************************************\n" + color.RESET);
                }
                default -> System.out.println(color.ROJO + "Opción Inválida" + color.RESET);
            }
        } while (opcion != 0);
    }

    private void buscarPorTitulo() {
        System.out.print(color.AZUL + "INGRESE EL TITULO DEL LIBRO: " + color.RESET);
        var tituloLibro = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, DatosAPI.class);

        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibros datos = libroBuscado.get();

            mostrarLibro(datos);

            String respuesta = ValidadorEntrada.validarSN(teclado, color.AZUL + "\n¿DESEA REGISTRAR ESTE LIBRO EN LA BASE DE DATOS? (S/N): " + color.RESET);


            if (!respuesta.equals("S")) {
                System.out.println(color.AMARILLO + "\n***************** EL LIBRO NO HA SIDO REGISTRADO *****************" + color.RESET);
                return;
            }

            Optional<Libro> yaExiste = libroRepository.findByTitulo(datos.titulo());
            if (yaExiste.isPresent()) {
                System.out.println(color.AMARILLO + "\n********* ESTE LIBRO YA SE ENCUENTRA EN LA BASE DE DATOS *********" + color.RESET);
                return;
            }

            DatosAutor datosAutor = datos.autor().isEmpty() ? null : datos.autor().getFirst();
            Autor autor = null;

            if (datosAutor != null) {
                autor = autorRepository.findByNombre(datosAutor.nombre())
                        .orElseGet(() -> {
                            Autor nuevo = new Autor();
                            nuevo.setNombre(datosAutor.nombre());
                            nuevo.setFechaNacimiento(datosAutor.fechaNacimiento());
                            nuevo.setFechaFallecimiento(datosAutor.fechaFallecimiento());
                            return autorRepository.save(nuevo);
                        });
            }

            Libro libro = new Libro();
            libro.setTitulo(datos.titulo());
            libro.setIdioma(!datos.idioma().isEmpty() ? datos.idioma().getFirst() : "Desconocido");
            libro.setNumeroDescargas(datos.numeroDescargas());
            libro.setAutor(autor);

            libroRepository.save(libro);
            System.out.println(color.VERDE + "\n************ EL LIBRO HA SIDO REGISTRADO EXITOSAMENTE ************" + color.RESET);

        } else {
            System.out.println(color.AMARILLO + "\n***************  ESTE LIBRO NO HA SIDO ENCONTRADO ****************" + color.RESET);
        }
    }


    private void buscarPorAutor() {
        System.out.print(color.CYAN + "INGRESE EL NOMBRE DEL AUTOR: " + color.RESET);
        var nombreAutor = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreAutor.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, DatosAPI.class);

        List<DatosLibros> librosDelAutor = datosBusqueda.resultados().stream()
                .filter(libro -> libro.autor().stream()
                        .anyMatch(autor -> autor.nombre().toUpperCase().contains(nombreAutor.toUpperCase())))
                .toList();

        if (librosDelAutor.isEmpty()) {
            System.out.println(color.AMARILLO + "\n**************** ESTE AUTOR NO HA SIDO ENCONTRADO ****************" + color.RESET);
            return;
        }

        DatosAutor datosAutor = librosDelAutor.getFirst().autor().getFirst();
        String nombreAutorReal = ValidadorEntrada.limitarPalabras(datosAutor.nombre(), 4);

        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("* LIBROS ENCONTRADOS AUTOR : " + nombreAutorReal.toUpperCase());
        System.out.println("******************************************************************" + color.RESET);
        librosDelAutor.forEach(libro -> System.out.println(color.GRIS + "* " + ValidadorEntrada.limitarPalabras(libro.titulo(), 5) + color.RESET));

        String respuesta = ValidadorEntrada.validarSN(teclado, color.AZUL + "\n¿DESEA REGISTRAR ESTOS LIBROS EN LA BASE DE DATOS? (S/N): " + color.RESET);

        if (!respuesta.equals("S")) {
            System.out.println(color.AMARILLO + "\n*************** LOS LIBROS NO HAN SIDO REGISTRADOS ***************" + color.RESET);
            return;
        }

        Autor autor = autorRepository.findByNombre(nombreAutorReal)
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(nombreAutorReal);
                    nuevoAutor.setFechaNacimiento(datosAutor.fechaNacimiento());
                    nuevoAutor.setFechaFallecimiento(datosAutor.fechaFallecimiento());
                    return autorRepository.save(nuevoAutor);
                });

        for (DatosLibros datosLibro : librosDelAutor) {
            String tituloLibro = datosLibro.titulo();

            boolean yaExiste = libroRepository.findByTitulo(tituloLibro).isPresent();
            String tituloCorto = ValidadorEntrada.limitarPalabras(tituloLibro, 3);

            if (!yaExiste) {
                Libro libro = new Libro();
                libro.setTitulo(tituloLibro);
                libro.setIdioma(!datosLibro.idioma().isEmpty() ? datosLibro.idioma().getFirst() : "Desconocido");
                libro.setNumeroDescargas(datosLibro.numeroDescargas());
                libro.setAutor(autor);
                libroRepository.save(libro);

                System.out.println(color.VERDE + "\n* " + tituloCorto + "/ HA SIDO REGISTRADO EXITOSAMENTE *" + color.RESET);
            } else {
                System.out.println(color.AMARILLO + "\n* " + tituloCorto + "/ YA SE ENCUENTRA EN LA BASE DE DATOS *" + color.RESET);
            }
        }
    }

    private void mostrarLibro(DatosLibros libro) {
        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("* LIBRO ENCONTRADO : " + ValidadorEntrada.limitarPalabras(libro.titulo().toUpperCase(), 5));
        System.out.println("******************************************************************" + color.RESET);
        System.out.println(color.GRIS + "* TITULO: " + libro.titulo() + color.RESET);

        if (!libro.autor().isEmpty()) {
            DatosAutor autor = libro.autor().getFirst();
            System.out.println(color.GRIS + "* AUTOR: " + autor.nombre());
            System.out.println("* AÑO NACIMIENTO: " + (autor.fechaNacimiento() != null ? autor.fechaNacimiento() : "Desconocido"));
            System.out.println("* AÑO FALLECIMIENTO: " + (autor.fechaFallecimiento() != null ? autor.fechaFallecimiento() : "Desconocido"));
        } else {
            System.out.println("* AUTOR: Desconocido");
        }

        if (!libro.idioma().isEmpty()) {
            String idioma = convertirIdioma(libro.idioma().getFirst());
            System.out.println("* IDIOMA: " + idioma);
        } else {
            System.out.println("* IDIOMA: Desconocido");
        }

        System.out.println("* DESCARGAS: " + libro.numeroDescargas() + color.RESET);
    }

    private String convertirIdioma(String codIdioma) {
        return switch (codIdioma.toLowerCase()) {
            case "en" -> "Inglés";
            case "es" -> "Español";
            case "fr" -> "Francés";
            case "de" -> "Alemán";
            case "pt" -> "Portugués";
            default -> codIdioma;
        };
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println(color.AMARILLO + "\n********** NO HAY LIBROS REGISTRADOS EN LA BASE DE DATOS *********" + color.RESET);
            return;
        }
        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("*     LISTADO DE LIBROS REGISTRADOS EN LA BASE DE DATOS          *");
        System.out.println("******************************************************************");

        System.out.printf("%-3s %-25s %-18s %-7s %-10s%n",
                "ID", "TÍTULO", "AUTOR", "IDIOMA", "DESCARGAS");
        System.out.println("------------------------------------------------------------------" + color.RESET);

        for (Libro libro : libros) {
            String autor = (libro.getAutor() != null) ? libro.getAutor().getNombre() : "Desconocido";

            System.out.print(color.GRIS);
            System.out.printf("%-3d %-25s %-18s %-7s %-10d%n",
                    libro.getId(),
                    ValidadorEntrada.truncar(libro.getTitulo(), 22),
                    ValidadorEntrada.truncar(autor, 15),
                    libro.getIdioma().toUpperCase(),
                    libro.getNumeroDescargas()
            );
        }

        System.out.print(color.RESET);
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println(color.AMARILLO + "********* NO HAY AUTORES REGISTRADOS EN LA BASE DE DATOS *********" + color.RESET);
            return;
        }

        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("*       LISTADO DE AUTORES REGISTRADOS EN LA BASE DE DATOS       *");
        System.out.println("******************************************************************");

        System.out.printf("%-3s %-32s %-16s %-10s%n",
                "ID", "AUTOR", "AÑO NACIMIENTO", "AÑO DECESO");
        System.out.println("------------------------------------------------------------------" + color.RESET);

        for (Autor autor : autores) {
            String nacimiento = autor.getFechaNacimiento() != null ? autor.getFechaNacimiento().toString() : "Desconocido";
            String deceso = autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento().toString() : "Desconocido";

            System.out.print(color.GRIS);
            System.out.printf("%-3d %-32s %-16s %-10s%n",
                    autor.getId(),
                    ValidadorEntrada.truncar(autor.getNombre(), 27),
                    nacimiento,
                    deceso
            );
        }
        System.out.print(color.RESET);
    }

    public void listarAutoresVivosEn() {

        int ano = ValidadorEntrada.pedirAnioValido(teclado);

        List<Autor> autoresVivos = autorRepository.findAutoresVivosEn(ano);

        if (autoresVivos.isEmpty()) {
            System.out.println(color.AMARILLO + "\n********* NO SE ENCONTRARON AUTORES VIVOS EN EL AÑO " + ano + " *********" + color.RESET);
            return;
        }

        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("*      LISTADO DE AUTORES REGISTRADOS VIVOS EN EL AÑO " + ano + "       *");
        System.out.println("******************************************************************");

        System.out.printf("%-3s %-32s %-16s %-10s%n",
                "ID", "AUTOR", "AÑO NACIMIENTO", "AÑO DECESO");
        System.out.println("------------------------------------------------------------------" + color.RESET);

        for (Autor autor : autoresVivos) {
            String nacimiento = autor.getFechaNacimiento() != null ? autor.getFechaNacimiento().toString() : "Desconocido";
            String deceso = autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento().toString() : "Desconocido";

            System.out.print(color.GRIS);
            System.out.printf("%-3d %-32s %-16s %-10s%n",
                    autor.getId(),
                    ValidadorEntrada.truncar(autor.getNombre(), 27),
                    nacimiento,
                    deceso);
        }

        System.out.println(color.RESET);
    }

    public void listarLibrosPorIdioma() {
        String codIdioma;

        System.out.println(color.CYAN);
        ValidadorEntrada.mostrarIdiomasDisponibles();
        System.out.println(color.RESET);

        while (true) {
            System.out.print(color.AZUL + "\nINGRESE EL CÓDIGO DEL IDIOMA A BUSCAR : " + color.RESET);
            codIdioma = teclado.nextLine().trim().toUpperCase();

            if (!ValidadorEntrada.esCodigoIdiomaValido(codIdioma)) {
                System.out.println(color.ROJO + "- Código de Idioma Inválido. Intente Nuevamente -" + color.RESET);
                continue;
            }
            break;
        }

        List<Libro> libros = libroRepository.findByIdioma(codIdioma.toLowerCase());

        if (libros.isEmpty()) {
            System.out.println(color.AMARILLO + "\n********* NO SE ENCONTRARON LIBROS EN IDIOMA " + codIdioma + " *********" + color.RESET);
            return;
        }

        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("*      LISTADO DE LIBROS REGISTRADOS EN IDIOMA " + convertirIdioma(codIdioma).toUpperCase());
        System.out.println("******************************************************************");

        System.out.printf("%-3s %-35s %-25s%n", "ID", "TÍTULO", "AUTOR");
        System.out.println("------------------------------------------------------------------" + color.RESET);

        for (Libro libro : libros) {
            String autor = (libro.getAutor() != null) ? libro.getAutor().getNombre() : "Desconocido";

            System.out.print(color.GRIS);
            System.out.printf("%-3d %-35s %-25s%n",
                    libro.getId(),
                    ValidadorEntrada.limitarPalabras(libro.getTitulo(), 4),
                    ValidadorEntrada.limitarPalabras(autor, 2)
            );
        }
        System.out.println(color.RESET);
    }

    public void buscarTop10Descargados() {

        System.out.println(color.CYAN + "\n******************************************************************");
        System.out.println("*     TOP 10 LIBROS MAS DESCARGADOS DESDE PROYECTO GUTENBERG     *");
        System.out.println("******************************************************************" + color.RESET);

        var json = consumoAPI.obtenerDatos(URL_BASE + "?sort=popular");
        var datosBusqueda = conversor.obtenerDatos(json, DatosAPI.class);

        List<DatosLibros> top10Libros = datosBusqueda.resultados().stream()
                .sorted((l1, l2) -> l2.numeroDescargas().compareTo(l1.numeroDescargas()))
                .limit(10)
                .toList();

        if (top10Libros.isEmpty()) {
            System.out.println(color.AMARILLO + "\n********* NO SE PUDIERON OBTENER LOS LIBROS MAS DESCARGADOS *********" + color.RESET);
            return;
        }
        System.out.print(color.CYAN);
        System.out.printf("%-3s %-29s %-22s %-10s%n", "Nº", "TÍTULO", "AUTOR", "DESCARGAS");
        System.out.println("------------------------------------------------------------------" + color.RESET);

        for (int i = 0; i < top10Libros.size(); i++) {
            DatosLibros libro = top10Libros.get(i);
            String autorNombre = libro.autor().isEmpty() ? "Desconocido" : ValidadorEntrada.limitarPalabras(libro.autor().getFirst().nombre(), 2);

            System.out.print(color.GRIS);
            System.out.printf("%-3d %-29s %-22s %-10d%n",
                    (i + 1),
                    ValidadorEntrada.truncar(libro.titulo(), 27),
                    ValidadorEntrada.limitarPalabras(autorNombre, 2),
                    libro.numeroDescargas()
            );
        }
        System.out.println(color.RESET);
    }
}