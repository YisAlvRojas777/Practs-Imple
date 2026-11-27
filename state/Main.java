public class Main {
    public static void main(String[] args) {
        System.out.println("=== Flujo normal ===");
        Pedido pedido = new Pedido();
        pedido.mostrarEstado();
        pedido.enviar();      // inválido: aún pendiente
        pedido.procesar();
        pedido.mostrarEstado();
        pedido.cancelar();    // inválido: ya procesado
        pedido.enviar();
        pedido.mostrarEstado();
        pedido.entregar();
        pedido.mostrarEstado();
        pedido.cancelar();    // inválido: ya entregado

        System.out.println("\n=== Pedido cancelado ===");
        Pedido pedido2 = new Pedido();
        pedido2.cancelar();
        pedido2.mostrarEstado();
        pedido2.procesar();   // inválido: cancelado
    }
}
