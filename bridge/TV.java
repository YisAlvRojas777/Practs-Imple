public class TV implements Dispositivo {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() {
        on = true;
        System.out.println("TV encendida");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("TV apagada");
    }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int percent) {
        this.volume = Math.max(0, Math.min(100, percent));
        System.out.println("TV: volumen = " + volume);
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = Math.max(1, channel);
        System.out.println("TV: canal = " + this.channel);
    }

    @Override
    public void printStatus() {
        System.out.println("TV -> " + (on ? "encendida" : "apagada")
                + ", volumen: " + volume + ", canal: " + channel);
    }
}
