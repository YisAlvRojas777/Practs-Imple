# Entregable: relación uno a muchos (Producto – Comentarios)

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
