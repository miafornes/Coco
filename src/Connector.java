public class Connector {
    private Host src;
    private Host dst;

    public Connector(Host src, Host dst) {
        this.src = src;
        this.dst = dst;
    }

    public Host getDst() {
        return dst;
    }

    public Host getSrc() {
        return src;
    }

    public void stop() {
        src.rmTp(dst);
        System.out.println(src + ">>" + dst + " stopped");
    }
}
