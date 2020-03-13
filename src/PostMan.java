import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PostMan extends Thread {
    private Coco coco;
    private Socket so;
    private String caller;

    public PostMan(Socket so, Coco coco) {
        this.coco = coco;
        this.so = so;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(so.getInputStream()));
            PrintWriter writer = new PrintWriter(so.getOutputStream(), true);
            this.caller = "";
            while(caller.length() < 1) {
                caller =  reader.readLine();
                if(caller == null) {
                    caller = "";
                }
            }
            coco.println(caller + " wants to open PrivChat");
            int code = coco.addPostMan(this);
            coco.println("'/pca " + code + "' to accept");
            coco.println("'/pcd " + code + "' to deny");
        } catch(IOException e) {

        }
    }

    public void accept() {
        Host h = new Host(so, caller, coco);
        h.start();
        coco.addHost(h);
    }

    public void close() {
        try {
            so.close();
        } catch(IOException e) {

        }
    }

    public String getCaller() {
        return caller;
    }
}