## El Patrón Builder

### Contexto del Problema
Necesitamos crear objetos `User` que tienen muchos parámetros opcionales (algunos obligatorios, otros no). Usar constructores telescópicos (múltiples constructores con diferentes parámetros) o setters directamente en el objeto principal hace el código difícil de leer, mantener y el objeto podría ser inconsistente durante su construcción.

### Tu Tarea: Transformar el código usando el Patrón Builder.

#### Instrucciones Paso a Paso:
1. **Crea una Clase Builder Estática Interna:**
   - En la clase `User`, crea una clase estática interna llamada `Builder`.
   - El `Builder` debe tener los mismos campos que la clase `User`.
   ```java
   public static class Builder {
    // Los campos del Builder serán los mismos que los de User
    private final String firstName; // Obligatorio
    private final String lastName;  // Obligatorio
    private int age = 0;           // Opcional con valor por defecto
    private String phone = "";     // Opcional con valor por defecto
    private String address = "";   // Opcional con valor por defecto
    }
   ```

2. **Constructor del Builder para Parámetros Obligatorios:**
   - El constructor del `Builder` debe recibir solo los parámetros obligatorios (`firstName`, `lastName`).
   ```java
   public Builder(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    }
   ```

3. **Métodos de Configuración para Parámetros Opcionales:**
   - Para cada parámetro opcional, crea un método en el `Builder` que establezca el valor y devuelva la instancia actual del `Builder` (`return this;`). Esto permite el *method chaining*.
   ```java
   public Builder age(int age) {
    this.age = age;
    return this;
    }

    public Builder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public Builder address(String address) {
        this.address = address;
        return this;
    }
   ```

4. **Método `build()`:**
   - Crea un método `build()` en el `Builder` que finalice la construcción y devuelva una nueva instancia de `User`, pasándose a sí mismo (`this`) normalmente a un constructor privado de `User`.
   ```java
   public User build() {
        return new User(this);
    }
   ```

5. **Constructor Privado en User:**
   - Haz que el constructor de `User` sea privado y que acepte un `Builder` como parámetro. Este constructor copiará los valores del `Builder` al `User`.
   ```java
   private User(Builder builder) {
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.age = builder.age;
    this.phone = builder.phone;
    this.address = builder.address;
    }
   ```

6. **Usa el Builder en el Código Cliente:**
   - En `Main`, crea el usuario usando `User.Builder`.
   ```java
   public class Main {
    public static void main(String[] args) {
        // Creación legible y flexible con Builder
        User user1 = new User.Builder("Juan", "Pérez")
            .age(30)
            .phone("+123456789")
            .address("Calle Falsa 123")
            .build();
        
        System.out.println(user1);
        
        // Solo nombre, apellido y teléfono - muy claro
        User user2 = new User.Builder("María", "Gómez")
            .phone("+987654321")
            .build();
            
        System.out.println(user2);
        
        // Todas las combinaciones son posibles
        User user3 = new User.Builder("Carlos", "López")
            .age(25)
            .address("Avenida Siempre Viva 742")
            .build();
            
        System.out.println(user3);
    }
    }
   ```

> **Solución:** las clases completas están en `User.java` y `Main.java` de este directorio. Los campos de `User` son `private final` y no tiene setters.

## 🧪 Pruebas

Ejecuta el método main para verificar que:

* [x] Los objetos se crean correctamente con diferentes combinaciones de parámetros
* [x] Los objetos son inmutables después de su creación
* [x] El código es más legible y expresivo

> **Salida obtenida:**
> ```text
> User{firstName='Juan', lastName='Pérez', age=30, phone='+123456789', address='Calle Falsa 123'}
> User{firstName='María', lastName='Gómez', age=0, phone='+987654321', address=''}
> User{firstName='Carlos', lastName='López', age=25, phone='', address='Avenida Siempre Viva 742'}
> ```

#### Preguntas de Análisis:
- ¿Cómo mejora la legibilidad del código cliente el uso del patrón Builder?

  **Respuesta:** Cada valor se asigna con un método con nombre (`.age(30)`, `.phone(...)`), así se entiende qué es cada dato sin depender del orden de parámetros de un constructor largo, y solo se escriben los opcionales necesarios.

- ¿Qué ventaja tiene hacer la clase `Builder` estática e interna?

  **Respuesta:** Al ser estática se puede crear sin una instancia previa de `User` (`new User.Builder(...)`); al ser interna puede acceder al constructor privado de `User` y queda agrupada con la clase que construye.

- ¿Garantiza este patrón la inmutabilidad del objeto `User`? ¿Por qué?

  **Respuesta:** Por sí solo no; la garantiza la implementación: aquí `User` tiene campos `final`, constructor privado y sin setters, y sus atributos (`String`, `int`) son inmutables. Si tuviera atributos mutables (p. ej. una lista) habría que copiarlos defensivamente.

---
