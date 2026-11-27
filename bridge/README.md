# Ejercicio: Patrón Bridge en Java

## 🌉 Contexto del Problema

Tenemos diferentes tipos de dispositivos (e.g., `Radio`, `TV`) y diferentes tipos de controles remotos (e.g., `ControlRemotoBasico`, `ControlRemotoAvanzado`). Queremos evitar una explosión de clases como `ControlRemotoBasicoParaRadio`, `ControlRemotoAvanzadoParaTV`, etc., y permitir que ambas jerarquías evolucionen independientemente.

## 🎯 Tu Tarea

Analizar y comprender cómo el patrón Bridge desacopla la abstracción (controles remotos) de la implementación (dispositivos), permitiendo que ambas jerarquías varíen independientemente.

## 📋 Instrucciones Paso a Paso

### Paso 1: Identifica las Dos Jerarquías

**Jerarquía de Implementación (Dispositivos):**
- Interfaz `Dispositivo` con operaciones básicas de dispositivo
- Clases `Radio` y `TV` que implementan la interfaz

**Jerarquía de Abstracción (Controles):**
- Clase abstracta `ControlRemoto` que contiene referencia a un `Dispositivo`
- Clases `ControlRemotoBasico` y `ControlRemotoAvanzado` que extienden `ControlRemoto`

### Paso 2: Implementa la Interfaz Dispositivo

Crea la interfaz que define las operaciones que todos los dispositivos deben implementar:

```java
public interface Dispositivo {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    int getChannel();
    void setChannel(int channel);
    void printStatus();
}
```
### Paso 3: Implementa las Clases Concretas de Dispositivos

Para Radio:

```java
public class Radio implements Dispositivo {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() { return on; }
    
    @Override
    public void enable() { 
        on = true;
        System.out.println("Radio encendida");
    }
    
    @Override
    public void disable() { 
        on = false;
        System.out.println("Radio apagada");
    }
    
    // Implementa los demás métodos: getVolume, setVolume, getChannel, setChannel
    // y printStatus para mostrar el estado actual
}
```

Para TV:

```java
public class TV implements Dispositivo {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() { return on; }
    
    @Override
    public void enable() { 
        on = true;
        System.out.println("TV encendida");
    }
    
    @Override
    public void disable() { 
        on = false;
        System.out.println("TV apagada");
    }
    
    // Implementa los demás métodos similares a Radio
}
```

> **Solución:** `Dispositivo.java`, `Radio.java` y `TV.java` están completos en esta carpeta. `setVolume` limita el valor entre 0 y 100 y `setChannel` evita canales menores a 1; ambos imprimen el cambio y `printStatus` muestra el estado actual.

### Paso 4: Crea la Abstracción Base (ControlRemoto)

Implementa la clase abstracta que contiene el "puente" hacia la implementación:

```java
public abstract class ControlRemoto {
    protected Dispositivo dispositivo; // ¡ESTE ES EL PUENTE!

    public ControlRemoto(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public abstract void togglePower();
    public abstract void volumeDown();
    public abstract void volumeUp();
    public abstract void channelDown();
    public abstract void channelUp();
}
```

### Paso 5: Implementa las Abstracciones Refinadas

Control Remoto Básico:

```java
public class ControlRemotoBasico extends ControlRemoto {
    public ControlRemotoBasico(Dispositivo dispositivo) {
        super(dispositivo);
    }

    @Override
    public void togglePower() {
        System.out.println("Control Básico: Botón power presionado");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    @Override
    public void volumeDown() {
        System.out.println("Control Básico: Bajar volumen");
        dispositivo.setVolume(dispositivo.getVolume() - 10);
    }

    @Override
    public void volumeUp() {
        System.out.println("Control Básico: Subir volumen");
        dispositivo.setVolume(dispositivo.getVolume() + 10);
    }

    @Override
    public void channelDown() {
        System.out.println("Control Básico: Canal anterior");
        dispositivo.setChannel(dispositivo.getChannel() - 1);
    }

    @Override
    public void channelUp() {
        System.out.println("Control Básico: Canal siguiente");
        dispositivo.setChannel(dispositivo.getChannel() + 1);
    }
}
```

Control Remoto Avanzado:

```java
public class ControlRemotoAvanzado extends ControlRemoto {
    public ControlRemotoAvanzado(Dispositivo dispositivo) {
        super(dispositivo);
    }

    // Implementa los mismos métodos básicos
    @Override
    public void togglePower() {
        System.out.println("Control Avanzado: Power inteligente");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    // ... implementa volumeDown, volumeUp, channelDown, channelUp

    // Método adicional solo disponible en el control avanzado
    public void mute() {
        System.out.println("Control Avanzado: Silenciar");
        dispositivo.setVolume(0);
    }

    public void record() {
        System.out.println("Control Avanzado: Grabando programa actual");
        // Lógica específica del control avanzado
    }
}
```

> **Solución:** `ControlRemoto.java`, `ControlRemotoBasico.java` y `ControlRemotoAvanzado.java` (con `volumeDown`, `volumeUp`, `channelDown`, `channelUp`, `mute` y `record`) están completos en esta carpeta. En `ControlRemoto` se añadió además `setDispositivo(...)` para poder cambiar el dispositivo en tiempo de ejecución.

### Paso 6: Experimenta con Diferentes Combinaciones

En la clase Main, prueba diferentes combinaciones de controles y dispositivos:

```java
public class Main {
    public static void main(String[] args) {
        // Combinación 1: Control básico con Radio
        System.out.println("=== Control Básico con Radio ===");
        ControlRemoto control1 = new ControlRemotoBasico(new Radio());
        probarControlRemoto(control1);

        // Combinación 2: Control avanzado con TV
        System.out.println("\n=== Control Avanzado con TV ===");
        ControlRemotoAvanzado control2 = new ControlRemotoAvanzado(new TV());
        probarControlRemoto(control2);
        control2.mute(); // Funcionalidad exclusiva del control avanzado

        // Combinación 3: Control básico con TV
        System.out.println("\n=== Control Básico con TV ===");
        ControlRemoto control3 = new ControlRemotoBasico(new TV());
        probarControlRemoto(control3);

        // Combinación 4: Control avanzado con Radio
        System.out.println("\n=== Control Avanzado con Radio ===");
        ControlRemotoAvanzado control4 = new ControlRemotoAvanzado(new Radio());
        probarControlRemoto(control4);
        control4.record();
    }

    public static void probarControlRemoto(ControlRemoto control) {
        control.togglePower(); // Encender
        control.volumeUp();
        control.channelUp();
        control.volumeDown();
        control.togglePower(); // Apagar
    }
}
```

**Salida obtenida (`java Main`):**
```text
=== Control Básico con Radio ===
Control Básico: Botón power presionado
Radio encendida
Control Básico: Subir volumen
Radio: volumen = 40
Control Básico: Canal siguiente
Radio: canal = 2
Control Básico: Bajar volumen
Radio: volumen = 30
Control Básico: Botón power presionado
Radio apagada

=== Control Avanzado con TV ===
Control Avanzado: Power inteligente
TV encendida
Control Avanzado: Subir volumen
TV: volumen = 40
Control Avanzado: Canal siguiente
TV: canal = 2
Control Avanzado: Bajar volumen
TV: volumen = 30
Control Avanzado: Power inteligente
TV apagada
Control Avanzado: Silenciar
TV: volumen = 0

=== Control Básico con TV ===
Control Básico: Botón power presionado
TV encendida
Control Básico: Subir volumen
TV: volumen = 40
Control Básico: Canal siguiente
TV: canal = 2
Control Básico: Bajar volumen
TV: volumen = 30
Control Básico: Botón power presionado
TV apagada

=== Control Avanzado con Radio ===
Control Avanzado: Power inteligente
Radio encendida
Control Avanzado: Subir volumen
Radio: volumen = 40
Control Avanzado: Canal siguiente
Radio: canal = 2
Control Avanzado: Bajar volumen
Radio: volumen = 30
Control Avanzado: Power inteligente
Radio apagada
Control Avanzado: Grabando programa actual
```

## 🔍 Puntos Clave del Bridge

El "Puente":

La composición protected Dispositivo dispositivo; en la clase ControlRemoto es el puente que conecta las dos jerarquías independientes.

Ventajas del Patrón:

Evita explosión de clases: Sin Bridge necesitaríamos:

ControlRemotoBasicoRadio
ControlRemotoBasicoTV
ControlRemotoAvanzadoRadio
ControlRemotoAvanzadoTV
Extensibilidad independiente: Podemos agregar nuevos dispositivos O nuevos controles sin afectar la otra jerarquía
### 🤔 Preguntas de Análisis

* Explosión de Clases: ¿Cuántas clases necesitaríamos si tuviéramos 3 tipos de controles y 4 tipos de dispositivos sin el patrón Bridge? ¿Y con el patrón Bridge?

  **Respuesta:** Sin Bridge, una clase por cada combinación: 3 × 4 = **12 clases** concretas. Con Bridge, una por cada variante de cada jerarquía: 3 + 4 = **7 clases** (más la clase abstracta `ControlRemoto` y la interfaz `Dispositivo`).
* Acoplamiento: ¿Qué pasaría si la clase ControlRemotoBasico heredara directamente de Radio o TV en lugar de usar composición?

  **Respuesta:** El control quedaría atado a un solo tipo de dispositivo (herencia es una relación fija en compilación): no podría usarse con otro dispositivo ni cambiarse en ejecución, y se mezclarían dos responsabilidades distintas (ser control y ser dispositivo), con lo que volvería la explosión de clases.
* Extensibilidad: Si quiero agregar un nuevo dispositivo (ej: Projector), ¿qué cambios necesito hacer? ¿Necesito modificar las clases de controles existentes?

  **Respuesta:** Solo hay que crear la clase `Projector` que implemente `Dispositivo`. No se modifica ninguna clase de control, porque dependen de la interfaz y no de dispositivos concretos.
* Principios SOLID: ¿Qué principios SOLID se aplican en este patrón? (Especialmente Principio de Inversión de Dependencias y * Principio Abierto/Cerrado)

  **Respuesta:** Inversión de Dependencias (el control depende de la abstracción `Dispositivo`, no de `Radio` o `TV`), Abierto/Cerrado (se agregan dispositivos o controles sin modificar lo existente), Responsabilidad Única (cada jerarquía tiene una sola razón de cambio) y Sustitución de Liskov (cualquier dispositivo puede reemplazar a otro en el control).
* Flexibilidad: ¿Cómo nos beneficiamos al poder cambiar el dispositivo de un control en tiempo de ejecución?

  **Respuesta:** Un mismo control puede reutilizarse con distintos dispositivos sin crear objetos nuevos (por ejemplo, con `setDispositivo(...)`), se puede cambiar el comportamiento según el contexto y es más fácil probar el control con un dispositivo de prueba.
### 🧪 Ejercicios de Extensión

* Agrega un nuevo dispositivo: Implementa la clase Projector y prueba con los controles existentes.

  **Solución:** `Projector.java` implementa `Dispositivo` y se prueba con el control básico en `MainExtension.java`; ningún control existente cambió.
* Agrega un nuevo control: Crea ControlRemotoVoz que responda a comandos de voz.

  **Solución:** `ControlRemotoVoz.java` extiende `ControlRemoto` y traduce comandos de texto ("encender", "subir volumen", etc.) a las operaciones del dispositivo.
* Funcionalidad cruzada: ¿Qué pasaría si algunos controles avanzados solo funcionaran con ciertos dispositivos? ¿Cómo manejarías esta restricción?

  **Respuesta:** Declararía una interfaz con la capacidad (por ejemplo, `Grabable`) que solo implementan los dispositivos compatibles, y el control avanzado comprobaría `dispositivo instanceof Grabable` antes de usarla (o usaría genéricos/un constructor que valide el tipo) y avisaría o lanzaría una excepción si no es compatible.

**Salida obtenida de los ejercicios de extensión (`java MainExtension`):**
```text
=== Control Básico con Projector (nuevo dispositivo) ===
Control Básico: Botón power presionado
Projector encendido
Control Básico: Subir volumen
Projector: volumen = 30
Control Básico: Canal siguiente
Projector: entrada = 2
Control Básico: Bajar volumen
Projector: volumen = 20
Control Básico: Botón power presionado
Projector apagado

=== Control de Voz con Radio (nuevo control) ===
Control de Voz: "encender"
Radio encendida
Control de Voz: "subir volumen"
Radio: volumen = 40
Control de Voz: "siguiente canal"
Radio: canal = 2
Control de Voz: "abrir ventana"
Control de Voz: comando no reconocido
Control de Voz: "apagar"
Radio apagada

=== Cambiar el dispositivo en tiempo de ejecución ===
Control Avanzado: Power inteligente
Radio encendida
Control Avanzado: Power inteligente
TV encendida
```
