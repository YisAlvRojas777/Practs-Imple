/** Versión original (sin Proxy), solo para comparar el momento de la carga. */
public class MainSinProxy {
    public static void main(String[] args) {
        System.out.println("Application started.");

        // Sin Proxy - la carga ocurre inmediatamente al crear el objeto
        Image image = new RealImage("photo.jpg");
        System.out.println("RealImage created (already loaded).");

        image.display();

        System.out.println("Second call:");
        image.display();
    }
}
