import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Host extends Thread{
    private Coco coco;

    private String host;
    private String port;
    private String nick;

    private Socket so;
    private BufferedReader reader;
    private PrintWriter writer;

    private ArrayList<Host> tps = new ArrayList<Host>();

    public Host(String host, String port, String nick, Coco coco) {
        this.coco = coco;

        this.host = host;
        this.port = port;
        this.nick = nick;
        try {
            this.so = new Socket(host, Integer.parseInt(port));
        } catch(IOException e) {

        }
    }

    public Host(Socket so, String nick, Coco coco) {
        this.coco = coco;
        this.so = so;
        this.host = so.getRemoteSocketAddress().toString();
        this.port = "PrivChat";
        this.nick = nick;
    }

    public void write(String input) {
        writer.println(input);
    }

    public BufferedReader getReader() {
        try {
            InputStreamReader stream = new InputStreamReader(so.getInputStream());
            return new BufferedReader(stream);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        try {
            InputStreamReader stream = new InputStreamReader(so.getInputStream());
            this.reader = new BufferedReader(stream);
            this.writer = new PrintWriter(so.getOutputStream(), true);
            String input = reader.readLine();
            while(input != null) {
                println(input);
                tp(input);
                input = reader.readLine();
            }
            this.reader.close();
            this.writer.close();
            stream.close();
            this.so.close();
        } catch(IOException e) {
            //do something?
        } catch(NumberFormatException e) {
            System.out.println("Invalid port");
        }
        System.out.println(this + " disconnected");
        coco.rmHost(this);
    }

    public void close() {
        try {
            this.so.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public String getNick() {
        return nick;
    }

    private void tp(String input) {
        for(Host h : tps) {
            h.write(input);
        }
    }

    public void addTp(Host h) {
        tps.add(h);
    }

    public void rmTp(Host h) {
        tps.remove(h);
    }

    public ArrayList<Host> getTps() {
        return tps;
    }

    public void println(String input) {
        System.out.println("|" + nick + "| " + input);
    }

    public String toString() {
        return host + ":" + port;
    }
}
