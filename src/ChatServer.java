import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class ChatServer extends Thread {

    private Scanner sc;
    private boolean l;

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> names = new HashSet<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();

    public ChatServer(Scanner sc, Boolean l) {
        this.sc = sc;
        this.l = l;
    }

    public void run() {
        while(true) {
            int port = 0;
            while(true) {
                try {
                    String input = getInput("port:", sc);
                    if(input.equalsIgnoreCase("")) input = "1250";
                    port = Integer.parseInt(input);
                    break;
                } catch(NumberFormatException e) {
                    System.out.println("Invalid port");
                }
            }
            try (ServerSocket sso = new ServerSocket(port)) {
                System.out.println("ChatServer running on port " + port);
                while(true) {
                    Socket so = sso.accept();
                    ChatServerThread t = new ChatServerThread(so, l);
                    t.start();
                }
            } catch(IOException e) {
                System.out.println("Failed to open Server Socket. Port is invalid or in use.");
            }
        }
    }

    private String getInput(String question) {
        System.out.print(question);
        String input = sc.nextLine();
        if(input == null) input = "";
        return input;
    }

    private static String getInput(String question, Scanner sc) {
        System.out.print(question);
        String input = sc.nextLine();
        if(input == null) input = "";
        return input;
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean l = false;
        String input = "";
        while(!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            input = getInput("Listen? ('y'/'n')", sc);
        }
        if(input.equalsIgnoreCase("y")) {
            l = true;
        }
        ChatServer cs = new ChatServer(sc, l);
        cs.start();
    }

    /**
     * The client handler task.
     */
    private static class ChatServerThread extends Thread {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private boolean l;

        public ChatServerThread(Socket socket, boolean l) {
            this.socket = socket;
            this.l = l;
        }

        /**
         * Services this thread's client by repeatedly requesting a screen name until a
         * unique one has been submitted, then acknowledges the name and registers the
         * output stream for the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("Nickname:");
                    name = in.nextLine();
                    name = name.replaceAll("\\s", "");
                    synchronized (names) {
                        if (!name.isBlank() && !names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                out.println("Welcome " + name);
                System.out.println(name + " has joined");
                for (PrintWriter writer : writers) {
                    writer.println(name + " has joined");
                }
                writers.add(out);

                while (true) {
                    String input = in.nextLine().trim();
                    if(l) {
                        System.out.println(name + ": " + input);
                    }
                    for (PrintWriter writer : writers) {
                        if(writer != out) {
                            writer.println(name + ": " + input);
                        }
                    }
                }
            } catch(IOException e) {
                //System.out.println(name + " lost connection");
            } catch(NoSuchElementException e) {

            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    names.remove(name);
                    System.out.println(name + " has left");
                    for (PrintWriter writer : writers) {
                        writer.println(name + " has left");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}