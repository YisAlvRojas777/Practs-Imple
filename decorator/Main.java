public class Main {
    public static void main(String[] args) {
        Usuario ana = new Usuario("Ana");
        ana.activarSuscripcion(ServicioStreaming.DISNEY);
        ana.activarSuscripcion(ServicioStreaming.WARNER);

        CatalogoPeliculas disney = new SuscripcionDecorator(new CatalogoDisney(), ana);
        CatalogoPeliculas paramount = new SuscripcionDecorator(new CatalogoParamount(), ana);
        CatalogoPeliculas warner = new SuscripcionDecorator(new CatalogoWarner(), ana);

        consultar(disney);
        consultar(paramount);
        consultar(warner);

        System.out.println("\n-- Ana activa Paramount --");
        ana.activarSuscripcion(ServicioStreaming.PARAMOUNT);
        consultar(paramount);
    }

    private static void consultar(CatalogoPeliculas catalogo) {
        String nombre = catalogo.getServicio().getNombre();
        try {
            System.out.println(nombre + ": " + catalogo.obtenerPeliculas());
        } catch (IllegalStateException e) {
            System.out.println(nombre + ": acceso denegado -> " + e.getMessage());
        }
    }
}
