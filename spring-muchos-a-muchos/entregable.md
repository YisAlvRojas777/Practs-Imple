# Entregable: relación muchos a muchos (Producto – Categoría)

**1. ¿Cómo afectará JPA a la base de datos? ¿Qué tabla creará y qué columnas tendrá?**
Además de las tablas `categorias` (de `Department`) y `producto`, JPA creará la **tabla de unión `producto_deparments`**, porque una relación muchos a muchos no puede guardarse con una sola llave foránea. Tendrá dos columnas, ambas de tipo texto (los ids son UUID en `String`) y juntas forman la clave primaria:

- `product_id`: viene de `joinColumns` y referencia al `id` de la entidad propietaria, es decir, `Department` (`categorias`).
- `deparments_id`: viene de `inverseJoinColumns` y referencia al `id` de `Producto`.

Cada fila representa una asociación categoría–producto. Los nombres de las columnas son confusos (`product_id` apunta a la categoría y `deparments_id` al producto), pero así lo define el `@JoinTable`. `Producto` no tiene columna propia para esta relación porque es el lado inverso (`mappedBy`).

**2. ¿Qué significa `@JsonIgnore` y por qué se usa en `Producto`?**
`@JsonIgnore` hace que Jackson omita ese campo al convertir el objeto a JSON (y al leerlo). Se usa en `Producto.deparments` porque la relación es bidireccional: un producto contiene sus categorías, y cada categoría contiene sus productos, así que al serializar se produciría un ciclo infinito (`Producto → Department → Producto → ...`) y un error de desbordamiento. Las categorías de un producto se consultan con el endpoint `GET /categoria/producto/{productId}`.

**3. ¿Cuál es la función del endpoint `addProductoCategoria/{productId}/{categoriaId}`?**
Asociar un producto existente con una categoría existente. Busca ambos por su ID; si existen, agrega el producto al conjunto `productos` de la categoría y la guarda. Como `Department` es el lado propietario, JPA inserta una nueva fila en la tabla `producto_deparments`.

**4. ¿Por qué se declaran dos repositorios, `categoriaRepo` y `productoRepo`?**
Porque cada `JpaRepository` trabaja con una sola entidad y su tabla (`Department` o `Producto`). El endpoint necesita leer un producto y una categoría, por lo que requiere un repositorio para cada uno.
