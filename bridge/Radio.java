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

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int percent) {
        // El volumen se mantiene entre 0 y 100
        this.volume = Math.max(0, Math.min(100, percent));
        System.out.println("Radio: volumen = " + volume);
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        // La radio no tiene canales menores a 1
        this.channel = Math.max(1, channel);
        System.out.println("Radio: canal = " + this.channel);
    }

    @Override
    public void printStatus() {
        System.out.println("Radio -> " + (on ? "encendida" : "apagada")
                + ", volumen: " + volume + ", canal: " + channel);
    }
}
