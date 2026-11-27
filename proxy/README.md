# Ejercicio: Patrón Proxy en Java

## 🎭 Contexto del Problema

Tenemos un servicio `Image` que carga una imagen desde disco, una operación costosa. Queremos postponer esta carga hasta que sea absolutamente necesario (carga perezosa - lazy loading). Además, queremos controlar el acceso al objeto real.

## 🎯 Tu Tarea

Transformar el código usando el **Patrón Proxy** para implementar carga perezosa (lazy loading) y controlar el acceso a los objetos costosos.

## 📋 Instrucciones Paso a Paso

### Paso 1: Crea la Clase Proxy

Crea una clase `ProxyImage` que implemente la interfaz `Image`:

```java
public class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;
    
    public ProxyImage(String filename) {
        this.filename = filename;
    }
}
```
### Paso 2: Implementa la Lógica de Carga Perezosa

En el método display() del ProxyImage, implementa la carga perezosa:
```java
@Override
public void display() {
    if (realImage == null) {
        realImage = new RealImage(filename); // Carga solo cuando es necesario
    }
    realImage.display(); // Delega al objeto real
}
```
### Paso 3: Modifica el Código Cliente

En la clase Main, cambia la creación de objetos para usar el Proxy en lugar del objeto real directamente:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Application started.");
        
        // Usando Proxy - la carga no ocurre inmediatamente
        Image image = new ProxyImage("photo.jpg");
        System.out.println("Proxy created, but RealImage not loaded yet.");
        
        // La carga solo ocurre aquí, cuando realmente necesitamos mostrar la imagen
        image.display();
        
        // Segunda llamada - ya está cargada, no se vuelve a cargar
        System.out.println("Second call:");
        image.display();
    }
}
```

> **Solución:** `Image.java`, `RealImage.java`, `ProxyImage.java` y `Main.java` están en esta carpeta. `MainSinProxy.java` es la versión original (sin Proxy) para comparar.

### 🤔 Preguntas de Análisis

* Carga Perezosa: ¿Cuándo se ejecuta el constructor de RealImage en el código original vs en el código con Proxy?

  **Respuesta:** En el código original, al crear el objeto (`new RealImage(...)`), por lo que la carga ocurre de inmediato. Con Proxy, el constructor de `RealImage` se ejecuta hasta la primera llamada a `display()`.

* Transparencia: ¿El código cliente necesita saber si está trabajando con un Proxy o con el objeto real? ¿Por qué?

  **Respuesta:** No. El cliente usa la interfaz `Image` y ambos implementan los mismos métodos, así que puede trabajar con cualquiera sin cambiar su código.

* Rendimiento: ¿Qué ventajas de rendimiento ofrece el patrón Proxy en este escenario?

  **Respuesta:** Evita cargar desde disco imágenes que quizá nunca se muestren (ahorra tiempo de inicio y memoria) y, una vez cargada, no se vuelve a cargar en llamadas posteriores.

* Control de Acceso: Además de la carga perezosa, ¿qué otros tipos de control podríamos implementar en el Proxy? (Ej: verificación de permisos, logging, cache)

  **Respuesta:** Protección (verificar permisos antes de mostrar), caché (reutilizar imágenes ya cargadas), logging (registrar cada acceso) y acceso remoto (obtener la imagen desde un servidor).

* Principio SOLID: ¿Qué principio SOLID se aplica aquí al permitir que el Proxy y el RealImage implementen la misma interfaz?

  **Respuesta:** El principio de sustitución de Liskov (LSP): el Proxy puede sustituir al objeto real sin alterar el comportamiento esperado. También se apoya en el de inversión de dependencias (el cliente depende de la abstracción `Image`) y en el abierto/cerrado (se añade control sin modificar `RealImage`).

### 🧪 Pruebas a Realizar

Ejecuta el código para verificar que:

- [x] Sin Proxy: El mensaje "Loading image..." aparece inmediatamente al crear el objeto
- [x] Con Proxy: El mensaje "Loading image..." aparece solo al llamar a display()
- [x] Múltiples llamadas: La carga solo ocurre una vez, incluso con múltiples llamadas a display()

**Salida con Proxy (`java Main`):**
```text
Application started.
Proxy created, but RealImage not loaded yet.
Loading image... photo.jpg
Displaying photo.jpg
Second call:
Displaying photo.jpg
```

**Salida sin Proxy (`java MainSinProxy`):**
```text
Application started.
Loading image... photo.jpg
RealImage created (already loaded).
Displaying photo.jpg
Second call:
Displaying photo.jpg
```

### 💡 Escenarios de Extensión

Considera cómo extenderías este patrón para:

- **Proxy de Protección:** Verificar permisos antes de cargar/mostrar la imagen → recibir un usuario en el proxy y lanzar una excepción en `display()` si no tiene permiso.
- **Proxy de Cache:** Almacenar en cache imágenes ya cargadas → un `Map<String, RealImage>` compartido, consultado antes de crear una nueva.
- **Proxy de Logging:** Registrar cada vez que se accede a una imagen → imprimir o guardar un registro en `display()` antes de delegar.
- **Proxy Remoto:** Acceder a imágenes en servidores remotos → el proxy descarga la imagen por red y oculta esa comunicación al cliente.
