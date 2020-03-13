import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MailBox extends Thread {
    private Coco coco;

    private ServerSocket sso;

    public MailBox(ServerSocket sso, Coco coco) {
        this.coco = coco;
        this.sso = sso;
    }

    public void run() {
        coco.println("MailBox started on port " + sso.getLocalPort());
        while(true) {
            try {
                Socket so = sso.accept();
                PostMan pm = new PostMan(so, coco);
                pm.start();
            } catch(IOException e) {

            }
        }
    }
}
