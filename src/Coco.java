import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Coco extends Thread{
    private ArrayList<Host> hosts = new ArrayList<Host>();
    private ArrayList<PostMan> pms = new ArrayList<PostMan>();
    private Scanner sc;

    private String nick = "";

    private final String DEFAULT_HOST = "localhost";
    private final String DEFAULT_PORT = "1250";
    private final String DEFAULT_NAME = "Unknown";
    private final int DEFAULT_SSO_PORT = 1900;

    private ServerSocket sso;

    private ArrayList<String> messages = new ArrayList<String>();

    private String[] help = {"Commands:",
            "/c [HOST] [PORT] (NAME)  - Connect to CocoServer",
            "/c                       - Connect to CocoServer",
            "/l                       - List connections",
            "/l [WORD]                - List connections filtered by word",
            "/dc [HOST/INDEX]         - Disconnect from CocoServer",
            "/dc                      - Disconnect from CocoServer",
            "/dca                     - Disconnect from all CocoServers",
            "/tp                      - Through-put input from host1 to host2",
            "/stp                     - Stop a through-put thread",
            "/stpa                    - Stop all through-put threads",
            "/sso                     - MailBox set-up",
            "/pc [HOST] [NAME] [PORT] - Send PrivChat request",
            "/pca [CODE]              - Accept PrivChat request",
            "/pcd [CODE]              - Deny PrivChat request",
            "/exit                    - Exit Coco"};

    public Coco() {

    }

    public void run() {
        /* intro */
        System.out.println("<=|=|=| Coco |=|=|=>");
        println("Do '/help' for help");

        /* init sc */
        this.sc = new Scanner(System.in);
        String scInput = "";
        /*while(true) {
            scInput = getInput("Online(y/n):");
            if(scInput.equalsIgnoreCase("y")) {
                try {
                    this.mailBox = new ServerSocket(DEFAULT_MAILBOX_PORT);
                    startMailBox();
                } catch(IOException e) {
                    println("Port " + DEFAULT_MAILBOX_PORT + " is in use, and I can not set you online");
                }
                break;
            } else if(scInput.equalsIgnoreCase("n")) {
                break;
            }
        }*/

        startMailBox(DEFAULT_SSO_PORT);

        boolean run = true;
        ArrayList<String> cmd = new ArrayList<String>();
        while(run || cmd.size() > 0) {
            if(cmd.size() > 0) {
                scInput = cmd.get(0);
                cmd.remove(0);
            } else {
                scInput = sc.nextLine();
            }
            if(scInput != null) {
                if(scInput.length() >= 1) {
                    if (scInput.charAt(0) == '/') {
                        String[] command = scInput.split(" ");
                        command[0] = command[0].toLowerCase();

                        switch (command[0]) {
                            case "/help":
                                {
                                    for (String s : help) {
                                        println(s);
                                    }
                                }
                                break;
                            case "/c":
                                {
                                    switch (command.length) {
                                        case 1:
                                            {

                                                String host = getInput("Host:");
                                                if(host.equalsIgnoreCase("")) {
                                                    host = DEFAULT_HOST;
                                                }
                                                String port = getInput("Port:");
                                                if(port.equalsIgnoreCase("")) {
                                                    port = DEFAULT_PORT;
                                                }
                                                String name = getInput("Name:");
                                                if(name.equalsIgnoreCase("")) {
                                                    name = DEFAULT_NAME;
                                                }
                                                connect(host, port, name);
                                            }
                                            break;
                                        case 2:
                                            {
                                                String port = getInput("Port:");
                                                if(port.equalsIgnoreCase("")) {
                                                    port = DEFAULT_PORT;
                                                }
                                                String name = getInput("Name:");
                                                if(name.equalsIgnoreCase("")) {
                                                    name = DEFAULT_NAME;
                                                }
                                                connect(command[1], port, name);
                                            }
                                            break;
                                        case 3:
                                            connect(command[1], command[2], DEFAULT_NAME);
                                            break;
                                        case 4:
                                            connect(command[1], command[2], command[3]);
                                            break;
                                        default:
                                            {
                                                println("Too many args");
                                            }
                                    }
                                }
                                break;
                            case "/l":
                                {
                                    if(hosts.size() > 0) {
                                        if(command.length == 1) {
                                            int i = 0;
                                            println("Index      Host      Name");
                                            for(Host h : hosts) {
                                                println(i + "        " + h.getHost() + "   " + h.getNick());
                                                i++;
                                            }
                                        } else {
                                            int i = 0;
                                            println("Index      Host      Name");
                                            for(Host h : hosts) {
                                                if(h.getNick().contains(scInput.substring(3))) {
                                                    println(i + "        " + h.getHost() + "   " + h.getNick());
                                                }
                                                i++;
                                            }
                                        }
                                    } else {
                                        println("No hosts connected");
                                    }
                                }
                                break;
                            case "/dc":
                                {
                                    if(hosts.size() > 0) {
                                        switch (command.length) {
                                            case 1:
                                                int i = 0;
                                                println("Index      Host      Name");
                                                for(Host h : hosts) {
                                                    println(i + "        " + h.getHost() + "   " + h.getNick());
                                                    i++;
                                                }
                                                println("(q for quit)");
                                                scInput = getInput("Index:");
                                                while(true) {
                                                    if(scInput.equalsIgnoreCase("q")) {
                                                        break;
                                                    }
                                                    try {
                                                        int index = Integer.parseInt(scInput);
                                                        if(index < hosts.size()) {
                                                            hosts.get(index).close();
                                                            hosts.remove(index);
                                                            break;
                                                        } else {
                                                            println("Index " + scInput + " not found");
                                                        }
                                                    } catch(NumberFormatException e) {
                                                        println("Index " + scInput + " is not a number");
                                                    }
                                                    println("(q for quit)");
                                                    scInput = getInput("Index:");
                                                }
                                                break;
                                            case 2:
                                                if(command[1].contains(".")) {
                                                    boolean found = false;
                                                    for(Host h : hosts) {
                                                        if(h.getHost().equalsIgnoreCase(command[1])) {
                                                            h.close();
                                                            hosts.remove(h);
                                                            found = true;
                                                            break;
                                                        }
                                                    }
                                                    if(!found) {
                                                        println("Host " + command[1] + " not found");
                                                    }
                                                } else {
                                                    try {
                                                        int index = Integer.parseInt(command[1]);
                                                        if(index < hosts.size()) {
                                                            hosts.get(index).close();
                                                            hosts.remove(index);
                                                        } else {
                                                            println("Index " + command[2] + " not found");
                                                        }
                                                    } catch(NumberFormatException e) {
                                                        println("Index " + command[2] + " not a number");
                                                    }
                                                }
                                                break;
                                            default:
                                                println("Too many args");
                                                break;
                                        }
                                    }
                                    if(hosts.size() == 0) {
                                        println("No hosts connected");
                                    }
                                }
                                break;
                            case "/dca":
                                {
                                    if(hosts.size() > 0) {
                                        println("Disconnecting all hosts:");
                                        for(Host h : hosts) {
                                            h.close();
                                        }
                                        for(PostMan pm : pms) {
                                            pm.close();
                                        }
                                        try {
                                            sso.close();
                                        } catch(IOException e) {

                                        }
                                        this.pms = new ArrayList<PostMan>();
                                        this.hosts = new ArrayList<Host>();
                                    }
                                    println("No hosts connected");
                                }
                                break;
                            case "/tp":
                                {
                                    if(hosts.size() > 1) {
                                        int i = 0;
                                        System.out.println("Index      Host      Name");
                                        for(Host h : hosts) {
                                            println(i + "        " + h.getHost() + "   " + h.getNick());
                                            i++;
                                        }
                                        int src = 0;
                                        boolean runTp = true;
                                        println("(q for quit)");
                                        scInput = getInput("Source host:");
                                        while(runTp) {
                                            if(scInput.equalsIgnoreCase("q")) {
                                                runTp = false;
                                                break;
                                            }
                                            try {
                                                int index = Integer.parseInt(scInput);
                                                if(index < hosts.size()) {
                                                    src = index;
                                                    break;
                                                } else {
                                                    println("Index " + scInput + " not found");
                                                }
                                            } catch(NumberFormatException e) {
                                                println("Index " + scInput + " is not a number");
                                            }
                                            println("(q for quit)");
                                            scInput = getInput("Source host:");
                                        }
                                        int dst = 0;
                                        println("(q for quit)");
                                        scInput = getInput("Destination host:");
                                        while(runTp) {
                                            if(scInput.equalsIgnoreCase("q")) {
                                                runTp = false;
                                                break;
                                            }
                                            try {
                                                int index = Integer.parseInt(scInput);
                                                if(index < hosts.size() && index != src) {
                                                    dst = index;
                                                    break;
                                                } else {
                                                    println("Invalid index");
                                                }
                                            } catch(NumberFormatException e) {
                                                println("Index " + scInput + " is not a number");
                                            }
                                            println("(q for quit)");
                                            scInput = getInput("Destination host:");
                                        }
                                        connect(hosts.get(src), hosts.get(dst));
                                    } else {
                                        println("Less than 2(" + hosts.size() + ") hosts connected");
                                    }
                                }
                                break;
                            case "/stp":
                                {
                                    ArrayList<Connector> tps = new ArrayList<Connector>();
                                    int i = 0;
                                    println("Index      Host      Name");
                                    for(Host h : hosts) {
                                        ArrayList<Host> htps = h.getTps();
                                        if(htps.size() > 0) {
                                            for(Host tp : htps) {
                                                tps.add(new Connector(h, tp));
                                                println(i + "        " + h.getHost() + "   " + h.getNick());
                                                println(">>       " + tp.getHost() + "   " + tp.getNick());
                                                i++;
                                            }
                                        }
                                    }
                                    if(tps.size() > 0) {
                                        println("(q for quit)");
                                        scInput = getInput("Index:");
                                        while(true) {
                                            if(scInput.equalsIgnoreCase("q")) {
                                                break;
                                            }
                                            try {
                                                int index = Integer.parseInt(scInput);
                                                if(index < tps.size()) {
                                                    tps.get(index).stop();
                                                    break;
                                                } else {
                                                    println("Index " + scInput + " not found");
                                                }
                                            } catch(NumberFormatException e) {
                                                println("Index " + scInput + " is not a number");
                                            }
                                            println("(q for quit)");
                                            scInput = getInput("Index:");
                                        }
                                    }
                                    if(tps.size() == 0) {
                                        println("No tps active");
                                    }
                                }
                                break;
                            case "/stpa":
                            {
                                ArrayList<Connector> tps = new ArrayList<Connector>();
                                int i = 0;
                                println("Index      Host      Name");
                                for(Host h : hosts) {
                                    ArrayList<Host> htps = h.getTps();
                                    if(htps.size() > 0) {
                                        for(Host tp : htps) {
                                            tps.add(new Connector(h, tp));
                                            println(i + "        " + h.getHost() + "   " + h.getNick());
                                            println(">>       " + tp.getHost() + "   " + tp.getNick());
                                            i++;
                                        }
                                    }
                                }
                                if(tps.size() > 0) {
                                    println("Stopping all tps");
                                    for(Connector c : tps) {
                                        c.stop();
                                    }
                                }
                                println("No tps active");
                            }
                                break;
                            case "/pc":
                                {
                                    if(command.length == 3) {
                                        try {
                                            Socket so = new Socket(command[1], DEFAULT_SSO_PORT);
                                            Host h = new Host(so, command[2], this);
                                            h.start();
                                            hosts.add(h);
                                            scInput = "";
                                            while(scInput.length() < 1) {
                                                scInput = getInput("Caller name:");
                                            }
                                            h.write(scInput);
                                        } catch(IOException e) {
                                            println("Connection failed");
                                        }
                                    } else if(command.length == 4) {
                                        try {
                                            int port = Integer.parseInt(command[3]);
                                            Socket so = new Socket(command[1], port);
                                            Host h = new Host(so, command[2], this);
                                            h.start();
                                            hosts.add(h);
                                            scInput = "";
                                            while(scInput.length() < 1) {
                                                scInput = getInput("Caller name:");
                                            }
                                            h.write(scInput);
                                        } catch(IOException e) {
                                            println("Connection failed");
                                        } catch(NumberFormatException e) {
                                            println("Invalid port");
                                        }
                                    } else {
                                        println("Wrong amount of args");
                                    }
                                }
                                break;
                            case "/pca":
                            {
                                if(command.length == 2) {
                                    try {
                                        int index = Integer.parseInt(command[1]);
                                        pms.get(index).accept();
                                        println("Chat open with " + pms.get(index).getCaller());
                                    } catch(NumberFormatException e) {
                                        println("Code not valid");
                                    }
                                } else {
                                    println("Wrong amount of args");
                                }
                            }
                            break;
                            case "/pcd":
                            {
                                if(command.length == 2) {
                                    try {
                                        int index = Integer.parseInt(command[1]);
                                        pms.get(index).close();
                                        println("PrivChat request " + index + " denied");
                                    } catch(NumberFormatException e) {
                                        println("Code not valid");
                                    }
                                } else {
                                    println("Wrong amount of args");
                                }
                            }
                            break;
                            case "/sso":
                                {
                                    if(sso != null) {
                                        try {
                                            sso.close();
                                            println("SSO closed");
                                        } catch(IOException e) {

                                        }
                                    }

                                    println("Open MailBox");
                                    println("(q for quit)");
                                    int port = 0;
                                    while(true) {
                                        scInput = getInput("Port:");
                                        if(scInput.equalsIgnoreCase("q")) {
                                            break;
                                        }
                                        try {
                                            port = Integer.parseInt(scInput);
                                            break;
                                        } catch(NumberFormatException e) {
                                            println("Invalid port");
                                        }
                                    }
                                    if(!scInput.equalsIgnoreCase("q")) {
                                        startMailBox(port);
                                    }
                                }
                                break;
                            case "/exit":
                                run = false;
                                cmd.add("/dca");
                                break;
                            default:
                                {
                                    println("Command not found, try '/help' for help");
                                }
                        }
                    } else {
                        if (hosts.size() > 0) {
                            for (Host h : hosts) {
                                h.write(scInput);
                            }
                        } else {
                            println("No connections");
                        }
                    }
                } else {
                    if (hosts.size() > 0) {
                        for (Host h : hosts) {
                            h.write(scInput);
                        }
                    } else {
                        println("No connections");
                    }
                }
            }
        }
        println("Goodbye!");
    }

    public void println(String input) {
        System.out.println("|Coco| " + input);
    }
    private String getInput(String question) {
        System.out.print("|Coco| " + question);
        String input = sc.nextLine();
        if(input == null) input = "";
        return input;
    }

    private void connect(String host, String port, String name) {
        Host h = new Host(host, port, name, this);
        h.start();
        hosts.add(h);
    }

    private void connect(Host src, Host dst) {
        src.addTp(dst);
    }

    public void addHost(Host host) {
        hosts.add(host);
    }

    public void rmHost(Host host) {
        hosts.remove(host);
    }

    private void startMailBox(int port) {
        try {
            this.sso = new ServerSocket(port);
            MailBox mb = new MailBox(sso, this);
            mb.start();

        } catch(IOException e) {
            println("Port " + DEFAULT_SSO_PORT + " is already in use");
            println("'/sso' to start mailbox on different port");
        }
    }

    public int addPostMan(PostMan pm) {
        pms.add(pm);
        return pms.size()-1;
    }

    public static void main(String[] args) {
        Coco coco = new Coco();
        coco.start();
    }
}
