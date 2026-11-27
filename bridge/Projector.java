/** Nuevo dispositivo: se agrega sin tocar ningún control remoto. */
public class Projector implements Dispositivo {
    private boolean on = false;
    private int volume = 20;
    private int channel = 1; // en el proyector representa la entrada (HDMI 1, 2...)

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() {
        on = true;
        System.out.println("Projector encendido");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("Projector apagado");
    }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int percent) {
        this.volume = Math.max(0, Math.min(100, percent));
        System.out.println("Projector: volumen = " + volume);
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = Math.max(1, channel);
        System.out.println("Projector: entrada = " + this.channel);
    }

    @Override
    public void printStatus() {
        System.out.println("Projector -> " + (on ? "encendido" : "apagado")
                + ", volumen: " + volume + ", entrada: " + channel);
    }
}
