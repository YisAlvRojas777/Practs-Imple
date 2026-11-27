# Patrón State - Sistema de Gestión de Estados de Pedidos

## 🎯 Objetivo

Implementar el patrón State para gestionar los diferentes estados de un pedido en un sistema de e-commerce.

## 🎭 Contexto Inicial

Actualmente tenemos una clase Pedido que maneja todos los estados mediante condicionales y flags, lo que hace el código difícil de mantener y extender.

## 📋 Instrucciones Paso a Paso

### Paso 1: Analizar el código inicial

Revisa la clase Pedido que contiene lógica condicional compleja para manejar estados.

> **Análisis:** con flags booleanos y `if/else` anidados, cada método (`procesar`, `cancelar`, `enviar`, `entregar`) tendría que revisar todas las combinaciones de estado. Agregar un estado nuevo obliga a modificar todos esos métodos, es fácil dejar combinaciones inválidas y el código crece sin control.

### Paso 2: Crear la interfaz EstadoPedido
```java
public interface EstadoPedido {
    void procesar(Pedido pedido);
    void cancelar(Pedido pedido);
    void enviar(Pedido pedido);
    void entregar(Pedido pedido);
}
```

### Paso 3: Implementar las clases de estado concretas

Crea las clases:

* EstadoPendiente
* EstadoProcesado
* EstadoEnviado
* EstadoEntregado
* EstadoCancelado

```java

// EstadoPedido.java
public interface EstadoPedido {
    void procesar(Pedido pedido);
    void cancelar(Pedido pedido);
    void enviar(Pedido pedido);
    void entregar(Pedido pedido);
}

// EstadoPendiente.java
public class EstadoPendiente implements EstadoPedido {
    @Override
    public void procesar(Pedido pedido) {
        System.out.println("Procesando pedido...");
        pedido.setEstado(new EstadoProcesado());
    }
    
    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("Cancelando pedido...");
        pedido.setEstado(new EstadoCancelado());
    }
    
    @Override
    public void enviar(Pedido pedido) {
        System.out.println("ERROR: No se puede enviar un pedido pendiente");
    }
    
    @Override
    public void entregar(Pedido pedido) {
        System.out.println("ERROR: No se puede entregar un pedido pendiente");
    }
}

// EstadoProcesado.java
public class EstadoProcesado implements EstadoPedido {
    @Override
    public void procesar(Pedido pedido) {
        System.out.println("ERROR: El pedido ya está procesado");
    }
    
    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("ERROR: No se puede cancelar un pedido ya procesado");
    }
    
    @Override
    public void enviar(Pedido pedido) {
        System.out.println("Enviando pedido...");
        pedido.setEstado(new EstadoEnviado());
    }
    
    @Override
    public void entregar(Pedido pedido) {
        System.out.println("ERROR: No se puede entregar un pedido no enviado");
    }
}


```

> **Solución:** `EstadoEnviado`, `EstadoEntregado` y `EstadoCancelado` están completos en esta carpeta. Reglas de transición:
>
> | Estado | procesar | cancelar | enviar | entregar |
> |---|---|---|---|---|
> | Pendiente | → Procesado | → Cancelado | error | error |
> | Procesado | error | error | → Enviado | error |
> | Enviado | error | error | error | → Entregado |
> | Entregado | error | error | error | error |
> | Cancelado | error | error | error | error |

### Paso 4: Refactorizar la clase Pedido

Elimina los flags booleanos
Agrega un atributo EstadoPedido estadoActual
Modifica los métodos para delegar al estado actual

```java
public class Pedido {
    private EstadoPedido estadoActual;
    
    public Pedido() {
        this.estadoActual = new EstadoPendiente();
    }
    
    public void setEstado(EstadoPedido estado) {
        this.estadoActual = estado;
    }
    
    public void procesar() {
        estadoActual.procesar(this);
    }
    
    public void cancelar() {
        estadoActual.cancelar(this);
    }
    
    public void enviar() {
        estadoActual.enviar(this);
    }
    
    public void entregar() {
        estadoActual.entregar(this);
    }
    
    public void mostrarEstado() {
        System.out.println("Estado actual: " + estadoActual.getClass().getSimpleName());
    }
}
```

## ▶️ Ejecución

```bash
javac *.java
java Main
```

**Salida obtenida:**
```text
=== Flujo normal ===
Estado actual: EstadoPendiente
ERROR: No se puede enviar un pedido pendiente
Procesando pedido...
Estado actual: EstadoProcesado
ERROR: No se puede cancelar un pedido ya procesado
Enviando pedido...
Estado actual: EstadoEnviado
Entregando pedido...
Estado actual: EstadoEntregado
ERROR: No se puede cancelar un pedido ya entregado

=== Pedido cancelado ===
Cancelando pedido...
Estado actual: EstadoCancelado
ERROR: No se puede procesar un pedido cancelado
```
