public class MainExtension {
    public static void main(String[] args) {
        System.out.println("=== Control Básico con Projector (nuevo dispositivo) ===");
        ControlRemoto basico = new ControlRemotoBasico(new Projector());
        Main.probarControlRemoto(basico);

        System.out.println("\n=== Control de Voz con Radio (nuevo control) ===");
        ControlRemotoVoz voz = new ControlRemotoVoz(new Radio());
        voz.comando("encender");
        voz.comando("subir volumen");
        voz.comando("siguiente canal");
        voz.comando("abrir ventana");
        voz.comando("apagar");

        System.out.println("\n=== Cambiar el dispositivo en tiempo de ejecución ===");
        ControlRemotoAvanzado control = new ControlRemotoAvanzado(new Radio());
        control.togglePower();
        control.setDispositivo(new TV()); // el mismo control, ahora con otra TV
        control.togglePower();
    }
}
