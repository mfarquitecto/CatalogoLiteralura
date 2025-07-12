# <img src="Literalura/docs/icons/Icon_0B.jpg" width="75"/> LiterAlura: Catálogo de Libros y Autores

​         **LiterAlura** es un Catálogo de Libros diseñado para funcionar como una aplicación de consola en Java. Construida con Spring Boot, permite a los usuarios buscar y explorar una vasta colección de obras literarias y sus creadores, obteniendo sus datos directamente desde la API pública de [Gutendex](https://gutendex.com/). Los libros y autores buscados se almacenan en una base de datos PostgreSQL local, lo que permite consultas rápidas y eficientes sobre el catálogo registrado.

---

## <img src="Literalura/docs/icons/Icon_1B.jpg" width="50"/> Funcionalidades Principales

Literalura ofrece las siguientes características interactivas a través de su menú de consola:
* **Buscar Libros por Título**: Permite al usuario encontrar libros específicos utilizando su título y, opcionalmente, registrarlos en la base de datos local.
* **Buscar Libros por Autor**: Busca libros relacionados con un autor específico, ofreciendo la opción de registrar múltiples libros de dicho autor, en la base de datos.
* **Ver Libros Registrados**: Muestra un listado completo de todos los libros guardados en la base de datos local, con detalles como título, autor, idioma y número de descargas.
* **Ver Autores Registrados**: Presenta una lista de todos los autores almacenados, incluyendo sus años de nacimiento y fallecimiento.
* **Ver Autores Vivos por Año**: Permite consultar autores que estaban vivos en un año determinado.
* **Ver Libros por Idioma**: Filtra y muestra los libros registrados según un idioma específico (Español, Inglés, Francés, Alemán y Portugués).
* **Buscar los Top 10 Libros más descargados**: Directamente desde el sitio [Project Gutemberg](https://www.gutenberg.org/browse/languages/es).
---
## <img src="Literalura/docs/icons/Icon_2B.jpg" width="50"/> Tecnologías Utilizadas

* **Java 17+**
* **Spring Boot**
* **Spring Data JPA**
* **Hibernate**
* **PostgreSQL**
* **Jackson**
* **[Gutendex API](https://gutendex.com/)**
---
## <img src="Literalura/docs/icons/Icon_3B.jpg" width="50"/> Requisitos

* **Java 17** 

* **Maven** 

* **PostgreSQL**

---

## <img src="Literalura/docs/icons/Icon_4B.jpg" width="50"/> Estructura del Proyecto

```tex
🗄 Literalura
├─────  LiteraluraApplication.java         
├── 📁 main
│   └───  Principal.java                 
├── 📁 model
│   ├───  Autor.java                     
│   ├───  Libro.java                     
│   ├───  DatosAPI.java                   
│   ├───  DatosAutor.java                
│   └───  DatosLibros.java               
├── 📁 repository
│   ├───  AutorRepository.java           
│   └───  LibroRepository.java           
├── 📁 service
│   ├───  ConsumoAPI.java                
│   ├───  ConvierteDatos.java           
│   └───  IConvierteDatos.java          
└── 📁 util
    ├───  Color.java                    
    └───  ValidadorEntrada.java        
```

---

## <img src="Literalura/docs/icons/Icon_5B.jpg" width="60"/> Configuración y Ejecución

1. **Clonar el repositorio:**

   ```properties
   git clone https://github.com/mfarquitecto/Literalura.git
   cd Literalura
   ```

2. **Crear una Base de Datos en PostgreSQL**:

   Crea un Usuario y una Base de Datos para el proyecto

   (Ej. `literalura` con usuario `postgres` y tu contraseña).

   ```properties
   CREATE DATABASE literalura;
   ```

3. **Abre el archivo** `src/main/resources/application.properties` y actualiza las credenciales de tu base de datos PostgreSQL:
```properties
    spring.datasource.url=jdbc:postgresql://localhost/literalura
    spring.datasource.username= tu_usuario_de_PostgreSQL
    spring.datasource.password= tu_contraseña_de_PostgreSQL
    spring.jpa.hibernate.ddl-auto=update
```
4.  **Compilar y Ejecutar**:

    * **Desde tu IDE (IntelliJ IDEA, Eclipse, VS Code):**
      
        * Importa el proyecto como un proyecto Maven.
        * Busca la clase `LiteraluraApplication.java` (en `src/main/java/com/literalura/`).
        * Haz clic derecho y selecciona "Run `LiteraluraApplication.main()`".
        
    * **Desde la terminal (usando Maven):**
        Navega a la raíz del proyecto en tu terminal y ejecuta:
        
        ```properties
        mvn clean install
        mvn spring-boot:run
        ```
---
## <img src="Literalura/docs/icons/Icon_6B.jpg" width="45"/> Como Utilizar

El programa opera a través de una interfaz por consola que presenta un menú interactivo. Mediante la selección de opciones numéricas, el usuario puede realizar diversas operaciones relacionadas con la consulta y visualización de Libros y Autores.

Al ejecutar el programa, se despliega el menú principal con las siguientes funcionalidades:

```properties
 ______       __         __
|      .---.-|  |_.---.-|  .-----.-----.-----.          
|   ---|  _  |   _|  _  |  |  _  |  _  |  _  |          
|______|___._|____|___._|__|_____|___  |_____|          
 _____   __ __              _____|_____|                
|     |_|__|  |_.-----.----|   _   |  .--.--.----.---.-.
|       |  |   _|  -__|   _|       |  |  |  |   _|  _  |
|_______|__|____|_____|__| |___|___|__|_____|__| |___._|


******************************************************************
***    BIENVENIDOS(AS)  AL  CATALOGO  DE  LIBROS  LITERALURA   ***
******************************************************************

1. BUSCAR LIBROS POR TITULO
2. BUSCAR LIBROS POR AUTOR
3. VER LIBROS REGISTRADOS
4. VER AUTORES REGISTRADOS
5. VER AUTORES REGISTRADOS VIVOS POR AÑO
6. VER LIBROS REGISTRADOS POR IDIOMA
7. BUSCAR LOS TOP 10 LIBROS MAS DESCARGADOS
0. SALIR

*****************************************************************
```
Ejemplo al seleccionar la opción Nº1: Buscar Libros por Título.
```properties
SELECCIONE UNA OPCION: 1

INGRESE EL TITULO DEL LIBRO: Gatsby

******************************************************************
* LIBRO ENCONTRADO : THE GREAT GATSBY
******************************************************************
* TITULO: The Great Gatsby
* AUTOR: Fitzgerald, F. Scott (Francis Scott)
* AÑO NACIMIENTO: 1896
* AÑO FALLECIMIENTO: 1940
* IDIOMA: Inglés
* DESCARGAS: 22363

¿DESEA REGISTRAR ESTE LIBRO EN LA BASE DE DATOS? (S/N): s

************ EL LIBRO HA SIDO REGISTRADO EXITOSAMENTE ************
```

---


## <img src="Literalura/docs/icons/Icon_7B.jpg" width="70"/> Autor

[![mfarquitecto](https://github.com/mfarquitecto.png?size=65)](https://github.com/mfarquitecto)  

##### [mfarquitecto](https://github.com/mfarquitecto)

Este proyecto fue desarrollado como parte del programa One Oracle Next Education (ONE) + Alura Latam.

---

## <img src="Literalura/docs/icons/Icon_8B.jpg" width="60"/> Licencia

Este proyecto está bajo la licencia MIT.


