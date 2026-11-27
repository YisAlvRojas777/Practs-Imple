/** Nuevo control: se agrega sin tocar ningún dispositivo. */
public class ControlRemotoVoz extends ControlRemoto {
    public ControlRemotoVoz(Dispositivo dispositivo) {
        super(dispositivo);
    }

    // Interpreta un comando de voz ya convertido a texto
    public void comando(String texto) {
        System.out.println("Control de Voz: \"" + texto + "\"");
        switch (texto.toLowerCase()) {
            case "encender", "apagar" -> togglePower();
            case "subir volumen" -> volumeUp();
            case "bajar volumen" -> volumeDown();
            case "siguiente canal" -> channelUp();
            case "canal anterior" -> channelDown();
            default -> System.out.println("Control de Voz: comando no reconocido");
        }
    }

    @Override
    public void togglePower() {
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    @Override
    public void volumeDown() { dispositivo.setVolume(dispositivo.getVolume() - 10); }

    @Override
    public void volumeUp() { dispositivo.setVolume(dispositivo.getVolume() + 10); }

    @Override
    public void channelDown() { dispositivo.setChannel(dispositivo.getChannel() - 1); }

    @Override
    public void channelUp() { dispositivo.setChannel(dispositivo.getChannel() + 1); }
}
