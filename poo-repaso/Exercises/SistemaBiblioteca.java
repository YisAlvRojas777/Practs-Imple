package Exercises;

public class SistemaBiblioteca {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        Libro l1 = new Libro("Clean Code");
        Libro l2 = new Libro("Effective Java");
        Libro l3 = new Libro("Patrones de diseño");
        biblioteca.agregarLibro(l1);
        biblioteca.agregarLibro(l2);
        biblioteca.agregarLibro(l3);

        Usuario regular = new UsuarioRegular("Ana");
        biblioteca.prestarLibro(l1, regular);
        biblioteca.prestarLibro(l2, regular);
        try {
            biblioteca.prestarLibro(l3, regular);
        } catch (IllegalStateException e) {
            System.out.println("Ana: " + e.getMessage());
        }

        Usuario premium = new UsuarioPremium("Luis");
        biblioteca.prestarLibro(l3, premium);
        System.out.println("Luis puede tener hasta " + premium.getMaximoPrestamos() + " libros");
    }
}
