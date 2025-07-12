package com.literalura.util;

import com.literalura.util.Color;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ValidadorEntrada {

    Color color = new Color();

    public int leerOpcion(Scanner teclado, int min, int max) {
        while (true) {
            try {
                int opcion = Integer.parseInt(teclado.nextLine().trim());
                if (opcion >= min && opcion <= max) {
                    return opcion;
                } else {
                    System.out.println(color.ROJO + "- Opción Fuera De Rango -\n" + color.RESET);
                    System.out.println("SELECCIONE UNA OPCION: ");
                }
            } catch (NumberFormatException e) {
                System.out.println(color.ROJO + "- Entrada Inválida. Debe Ingresar Un Número -\n" + color.RESET);
                System.out.println("SELECCIONE UNA OPCION: ");
            }
        }
    }

    public static String validarSN(Scanner scanner, String mensaje) {
        Color color = new Color();
        String respuesta;

        while (true) {
            System.out.print(mensaje);
            respuesta = scanner.nextLine().trim().toUpperCase();
            if (respuesta.equals("S") || respuesta.equals("N")) {
                return respuesta;
            } else {
                System.out.println(color.ROJO + "- Entrada Inválida. Ingrese 'S' para SI o 'N' para NO -\n" + color.RESET);
            }
        }
    }

    public static String limitarPalabras(String texto, int maxPalabras) {
        return Arrays.stream(texto.trim().split("\\s+"))
                .limit(maxPalabras)
                .collect(Collectors.joining(" "));
    }

    public static String truncar(String texto, int maxCaracteres) {
        return texto.length() > maxCaracteres ? texto.substring(0, maxCaracteres) : texto;
    }

    public static boolean esNumeroEntero(String entrada) {
        try {
            Integer.parseInt(entrada.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean esAnioValido(int anio) {
        int anioActual = LocalDate.now().getYear();
        return anio >= -1000 && anio <= anioActual;
    }

    public static int pedirAnioValido(Scanner teclado) {
        while (true) {
            Color color = new Color();
            System.out.print(color.AZUL + "INGRESE EL AÑO A CONSULTAR: " + color.RESET);
            String entrada = teclado.nextLine().trim();

            if (!esNumeroEntero(entrada)) {
                System.out.println(color.ROJO + "- Año Inválido. Debe Ingresar un Número -\n" + color.RESET);
                continue;
            }

            int anio = Integer.parseInt(entrada);

            if (!esAnioValido(anio)) {
                System.out.println(color.ROJO + "- Año Fuera de Rango. Debe Ser Entre -1000 y " + LocalDate.now().getYear() + " -\n" + color.RESET);
                continue;
            }

            return anio;
        }
    }

    private static final Map<String, String> IDIOMAS_DISPONIBLES = Map.of(
            "EN", "Inglés",
            "ES", "Español",
            "FR", "Francés",
            "DE", "Alemán",
            "PT", "Portugués"
    );

    public static boolean esCodigoIdiomaValido(String codigo) {
        return IDIOMAS_DISPONIBLES.containsKey(codigo.toUpperCase());
    }

    public static void mostrarIdiomasDisponibles() {
        System.out.println("*** IDIOMAS DISPONIBLES ***");
        System.out.printf("   %-9s|   %-15s\n", "CÓDIGO", "IDIOMA");
        System.out.println("---------------------------");
        IDIOMAS_DISPONIBLES.forEach((codigo, nombre) ->
                System.out.printf("   %-9s|   %-15s\n", codigo, nombre)
        );
    }


}
