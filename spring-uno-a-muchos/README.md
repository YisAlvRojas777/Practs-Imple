# Spring Boot con JPA: relación uno a muchos

Este proyecto muestra una API REST para administrar productos y sus comentarios.
Usa Spring Boot, Spring Data JPA, Hibernate y PostgreSQL. Un producto puede tener
varios comentarios; cada comentario pertenece a un producto.

## Requisitos

- Java 21
- PostgreSQL
- Maven (el proyecto incluye el wrapper `demo/mvnw`)

## Preparar PostgreSQL

La configuración del proyecto espera una base de datos llamada `chochos`, con el
usuario y la contraseña `chochos`.

Si usas el Dev Container de este repositorio, PostgreSQL corre en otro servicio
llamado `db`. Abre una terminal en el contenedor e inicia `psql`:

```bash
psql -h db -U postgres -d postgres
```

La contraseña inicial del servicio es `postgres`. En `psql`, crea el usuario y
la base de datos:

```sql
CREATE USER chochos WITH PASSWORD 'chochos';
CREATE DATABASE chochos OWNER chochos;
```

En `demo/src/main/resources/application.properties`, la URL inicial usa
`localhost`. Al ejecutar Spring Boot dentro del Dev Container, cambia el host
de la URL a `db`:

```properties
spring.datasource.url=jdbc:postgresql://db:5432/chochos
```

Si PostgreSQL está instalado directamente en tu equipo y Spring Boot también se
ejecuta ahí, conserva `localhost`. Para conectarte desde una herramienta local
a PostgreSQL del Dev Container, reenvía el puerto `5432` en
`.devcontainer/devcontainer.json`.

`spring.jpa.hibernate.ddl-auto=update` permite que Hibernate actualice el
esquema a partir de las entidades. Es práctico para este ejercicio; para una
aplicación real, normalmente se administran los cambios de esquema con
migraciones.

## Ejecutar la aplicación

Desde la raíz del repositorio:

```bash
cd demo
./mvnw spring-boot:run
```

## Modelo y relación

Las entidades están en `demo/src/main/java/com/example/demo/model/`.

- `Product` representa la tabla `producto` y contiene un conjunto de comentarios.
- `Comment` representa la tabla `comentarios` y contiene la referencia a su producto.
- `@Entity` marca una clase persistente y `@Id` marca su clave primaria.
- `@GeneratedValue(strategy = GenerationType.UUID)` genera un UUID para cada
  identificador, guardado aquí como `String`.
- Las entidades usan Lombok: `@Getter` genera getters, `@Builder` permite
  construir objetos con el patrón builder, y `@NoArgsConstructor` y
  `@AllArgsConstructor` generan constructores sin argumentos y con todos los
  campos, respectivamente. Ninguna de estas anotaciones genera setters.

La relación está declarada en ambos lados:

```java
// Product
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Setter
private String id;

@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL,
           fetch = FetchType.EAGER)
@Setter
private Set<Comment> comentarios;

// Comment
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Setter
private String id;

@ManyToOne(fetch = FetchType.LAZY)
@Setter
private Product producto;
```

En estos modelos, `@Setter` se agrega solo a los campos que los controladores
necesitan modificar: los identificadores y la relación `Comment.producto`. Por
eso los controladores pueden asignar el ID de la ruta al actualizar (`PUT`) y
asociar un comentario con un producto al crearlo. En las peticiones `POST`,
normalmente se omite `id` para que JPA genere el UUID.

### ¿Dónde queda la llave foránea?

La llave foránea queda en la tabla de muchos, es decir, en `comentarios`. La
propiedad `Comment.producto` es el lado propietario de la relación y es la que
indica qué producto referencia cada comentario. Hibernate suele nombrar la
columna `producto_id`; el nombre exacto puede depender de la estrategia de
nombres configurada.

`mappedBy = "producto"` le dice a JPA que `Product.comentarios` es el lado
inverso: el valor se administra por medio de la propiedad `producto` de
`Comment`. `mappedBy` se refiere al nombre de la propiedad Java, no al nombre de
la tabla o columna.

Al actualizar una relación desde Java, mantén sincronizadas las dos referencias:
asigna el producto al comentario y agrega el comentario al conjunto del
producto. En este proyecto, el endpoint para crear un comentario busca el
producto y asigna `Comment.producto`.

### Fetch types: cuándo se carga la relación

El tipo de carga determina cuándo JPA obtiene las entidades relacionadas:

- `FetchType.LAZY`: carga la relación cuando se accede a ella. Evita traer datos
  que no se necesitan, pero el acceso puede fallar si la sesión de persistencia
  ya se cerró.
- `FetchType.EAGER`: pide que la relación esté disponible al cargar la entidad.
  Puede traer más datos de los necesarios y afectar el rendimiento. No significa
  necesariamente que Hibernate use un único `JOIN`.

En JPA, `@OneToMany` es `LAZY` por defecto y `@ManyToOne` es `EAGER` por defecto.
Este proyecto los configura explícitamente al revés: los comentarios de un
producto son `EAGER` y el producto de un comentario es `LAZY`. La configuración
`spring.jpa.open-in-view=false` hace especialmente importante entender en qué
momento se accede a las relaciones.

### Cascade types: qué operaciones se propagan

`cascade` indica qué operaciones JPA se propagan desde una entidad a sus
relacionadas. `CascadeType.ALL` equivale a `PERSIST`, `MERGE`, `REMOVE`,
`REFRESH` y `DETACH`.

En este proyecto, `Product.comentarios` tiene `CascadeType.ALL`. Por ejemplo, al
guardar un producto con comentarios nuevos asociados, JPA puede persistirlos;
al eliminar el producto mediante JPA, la operación de eliminación también se
propaga a sus comentarios. La propagación va de `Product` hacia `Comment`, no al
revés. Esto es comportamiento de JPA y no necesariamente una regla `ON DELETE
CASCADE` creada en PostgreSQL.

No se declara `orphanRemoval = true`: quitar un comentario del conjunto no
significa por sí solo que JPA deba eliminarlo de la base de datos. Elige
cascadas según el ciclo de vida real de los datos; `ALL` no es una opción segura
por defecto para cualquier relación.

### `Set` y `List`

Una `List` conserva el orden y permite elementos repetidos. Un `Set` no promete
orden y está pensado para no repetir elementos según `equals` y `hashCode`. La
entidad `Product` usa `Set<Comment>`; para colecciones de entidades, considera
con cuidado cómo se implementan `equals` y `hashCode` antes de depender de la
unicidad por identificador.

### JSON y referencias

`@JsonManagedReference` en `Product.comentarios` y `@JsonBackReference` en
`Comment.producto` evitan que Jackson recorra la relación en ambas direcciones
indefinidamente al convertirla a JSON. La respuesta del producto puede incluir
los comentarios, sin volver a anidar el producto completo dentro de cada
comentario.

## Probar la API

Los controladores están en `demo/src/main/java/com/example/demo/controllers/`.
Puedes usar `curl`, Postman u otra herramienta HTTP.

1. Crea un producto. La ruta es `POST /producto` (singular):

```bash
curl --location 'http://localhost:8080/producto' \
  --header 'Content-Type: application/json' \
  --data '{
    "nombre": "Proteina Fresa 2.2 kg",
    "descripcion": "Proteina de suero de leche en polvo",
    "lote": "12345",
    "precioUnitario": 500.5,
    "fechaCaducidad": "2027-05-04"
  }'
```

2. Copia el `id` UUID de la respuesta y úsalo para crear un comentario:

```bash
curl --location 'http://localhost:8080/comentario/ID_DEL_PRODUCTO' \
  --header 'Content-Type: application/json' \
  --data '{
    "username": "usuario1",
    "comentario_corto": "Buen producto",
    "estrellas": 4,
    "comentario_largo": "La entrega llegó a tiempo",
    "imagen1": "https://example.com/imagen1.jpeg",
    "imagen2": "https://example.com/imagen2.jpeg"
  }'
```

3. Consulta los productos y sus comentarios:

```bash
curl 'http://localhost:8080/productos'
```

O consulta los comentarios de un producto en particular:

```bash
curl 'http://localhost:8080/comentarios/ID_DEL_PRODUCTO'
```

Rutas adicionales implementadas:

| Método | Ruta | Acción |
| --- | --- | --- |
| `GET` | `/producto/{id}` | Consultar un producto |
| `PUT` | `/producto/{id}` | Actualizar un producto |
| `DELETE` | `/producto/{id}` | Eliminar un producto |
| `GET` | `/comentarios` | Listar comentarios |
| `GET` | `/comentario/{id}` | Consultar un comentario |
| `PUT` | `/comentario/{id}` | Actualizar un comentario |
| `DELETE` | `/comentario/{id}` | Eliminar un comentario |

## Actividad

1. Revisa los modelos, controladores y repositorios. Identifica qué anotaciones
   configuran la relación y qué endpoint asigna el producto al comentario.
2. Consulta las tablas creadas por Hibernate e identifica la clave primaria y la
   llave foránea.
3. Crea un archivo `entregable` en la raíz del repositorio y responde:
   - ¿En qué tabla queda la llave foránea y qué propiedad de Java la controla?
   - ¿Cuál es la diferencia entre `List` y `Set`?
   - ¿Por qué los identificadores son `String` y qué tipo de valor guardan?
   - ¿Qué ocurre al eliminar un producto con comentarios asociados? ¿Qué cambia
     si se quita `CascadeType.ALL`?
   - ¿Qué diferencia práctica hay entre los `FetchType` usados en `Product` y
     `Comment`?
4. Compara tus respuestas con el esquema y el comportamiento observado al probar
   la API. Ajusta el entregable si es necesario.

## Resolución de la actividad

1. **Anotaciones de la relación y endpoint que asigna el producto al comentario**
   - En `Product`: `@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.EAGER)` y `@JsonManagedReference`.
   - En `Comment`: `@ManyToOne(fetch = FetchType.LAZY)` y `@JsonBackReference`.
   - El endpoint que asigna el producto al comentario es `POST /comentario/{productId}`: busca el producto y ejecuta `comentario.setProducto(...)` antes de guardar.
2. **Tablas, clave primaria y llave foránea** (según las entidades): se crean `producto` y `comentarios`. En ambas la clave primaria es `id` (UUID guardado como texto). La llave foránea es la columna `producto_id` de `comentarios`, que referencia `producto.id`.
3. **Entregable** (también en el archivo `entregable.md`):

**1. ¿En qué tabla queda la llave foránea y qué propiedad de Java la controla?**
En la tabla `comentarios` (el lado "muchos"), en una columna que Hibernate suele llamar `producto_id`. La controla la propiedad `Comment.producto` (`@ManyToOne`), que es el lado propietario. `Product.comentarios` es el lado inverso (`mappedBy = "producto"`).

**2. ¿Cuál es la diferencia entre `List` y `Set`?**
`List` conserva el orden y permite elementos repetidos; `Set` no garantiza orden y no admite duplicados (según `equals` y `hashCode`).

**3. ¿Por qué los identificadores son `String` y qué tipo de valor guardan?**
Porque con `GenerationType.UUID` JPA genera un UUID (identificador único de 128 bits) y aquí se guarda como texto, por ejemplo `3f2b8c1e-...`. Así los ids se generan sin depender de la base de datos y no son secuenciales ni fáciles de adivinar.

**4. ¿Qué ocurre al eliminar un producto con comentarios asociados? ¿Qué cambia si se quita `CascadeType.ALL`?**
Con `CascadeType.ALL` la eliminación se propaga: JPA borra primero los comentarios del producto y después el producto. Si se quita `CascadeType.ALL` (sin `REMOVE`), JPA solo intenta borrar el producto y la base de datos rechaza la operación por violar la llave foránea, porque los comentarios aún lo referencian (a menos que se borren antes). Tampoco se guardarían automáticamente los comentarios nuevos incluidos al guardar un producto.

**5. ¿Qué diferencia práctica hay entre los `FetchType` usados en `Product` y `Comment`?**
En `Product.comentarios` es `EAGER`: al cargar un producto sus comentarios se cargan de inmediato y aparecen en la respuesta JSON, aunque puede traer más datos de los necesarios. En `Comment.producto` es `LAZY`: el producto no se carga hasta que se accede a él, lo que ahorra consultas, pero con `open-in-view=false` acceder fuera de la transacción provoca `LazyInitializationException`; por eso el JSON no serializa ese campo (`@JsonBackReference`).

4. Las respuestas corresponden al modelo de entidades y a los endpoints descritos en este documento.
