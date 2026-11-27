import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Exercises.*;

/**
 * Pruebas propias (no sustituyen a Tests.java). Cubren los cuatro ejercicios
 * base y las prácticas adicionales de POO.
 */
public class PruebasAdicionales {
    static boolean ok = true;

    static void check(String nombre, boolean condicion) {
        if (!condicion) {
            ok = false;
            System.out.println("FALLO: " + nombre);
        }
    }

    static boolean lanza(Runnable r) {
        try {
            r.run();
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        // DataTypes
        DataTypes d = new DataTypes();
        check("DataTypes por defecto", d.cadena.equals("Hola Mundo") && d.entero == 10
                && d.flotante == 5.9f && d.caracter == 'A' && d.boleano);
        DataTypes d2 = new DataTypes("x", 1, 2f, 'z', false);
        check("DataTypes con parámetros", d2.cadena.equals("x") && !d2.boleano);

        // Colecciones
        Colecciones c = new Colecciones();
        c.inicializar();
        HashMap<Integer, String> h = c.obtenerHash();
        check("Colecciones tamaño", h.size() == 10);
        check("Colecciones claves 1..n", h.containsKey(1) && h.containsKey(10) && !h.containsKey(0));
        check("Colecciones sin repetidos", new java.util.HashSet<>(h.values()).size() == h.size());

        // Herencia
        check("Padre.test() es false", new Padre("Perez").test() == false);
        check("Padre.test() true si coincide", new Padre("Lopez").test() == true);

        // Sobrecarga / polimorfismo
        check("Alumno reprueba", new Alumno("a", "m", 5).reprobar().equals(":("));
        check("Alumno aprueba", new Alumno("a", "m", 6).reprobar().equals(":)"));
        check("Profesor", new Profesor("p", "Mate", 8).reprobar().equals(">:) Sacaste 8 en Mate"));
        Sobrecarga s = new Alumno("a", "m", 3);
        check("Polimorfismo", s.reprobar().equals(":("));

        // CuentaBancaria
        CuentaBancaria cuenta = new CuentaBancaria("Ana", 100);
        cuenta.depositar(50);
        cuenta.retirar(30);
        check("Saldo", cuenta.getSaldo() == 120);
        check("Depósito negativo", lanza(() -> cuenta.depositar(-1)));
        check("Retiro excesivo", lanza(() -> cuenta.retirar(1000)));

        // Figuras
        check("Círculo", Math.abs(new Circulo(1).calcularArea() - Math.PI) < 1e-9);
        check("Rectángulo", new Rectangulo(2, 3).calcularArea() == 6);
        check("Dimensiones inválidas", lanza(() -> new Circulo(0)) && lanza(() -> new Rectangulo(1, -1)));

        // Vehículos
        check("Automóvil", new Automovil("VW", "Vento", 4).mostrarInformacion().equals("VW Vento - Puertas: 4"));
        check("Motocicleta", new Motocicleta("Honda", "Rebel", true).mostrarInformacion().equals("Honda Rebel - Canastilla: true"));

        // Notificaciones
        List<Notificacion> notificaciones = Arrays.asList(
                new NotificacionCorreo("alumno@ejemplo.com"),
                new NotificacionConsola());
        for (Notificacion n : notificaciones) {
            n.enviar("La actividad fue registrada");
        }

        // Biblioteca
        Biblioteca b = new Biblioteca();
        Libro l1 = new Libro("A"), l2 = new Libro("B"), l3 = new Libro("C");
        b.agregarLibro(l1); b.agregarLibro(l2); b.agregarLibro(l3);
        Usuario regular = new UsuarioRegular("Ana");
        b.prestarLibro(l1, regular);
        b.prestarLibro(l2, regular);
        check("Límite regular", lanza(() -> b.prestarLibro(l3, regular)));
        b.devolverLibro(l1, regular);
        check("Libro devuelto", !l1.estaPrestado());
        b.prestarLibro(l3, new UsuarioPremium("Luis"));
        check("Libro prestado", l3.estaPrestado());

        System.out.println(ok);
    }
}
