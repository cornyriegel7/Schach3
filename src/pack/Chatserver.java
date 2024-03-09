package pack;
import java.util.*;
import java.util.ArrayList;
//Todo: muss beim onlinespiel dauernd laufen und empfangsbereit sein, wenn int[] kommt an den controller geben und dann das board damit aktualisieren.
// man muss schauen, dass nur ein move at a time erlaubt ist. außerdem vielleicht server als interface von controller machen, damit die kommunikation
// funktioniert.
public class Chatserver extends Server {
    public static final String logout = "ABME", test = "TEST", board = "BOAR", nrOfConnections = "CONN";
    private String hostIP;
    private String joinIP;

    private boolean isHost;
    int connections;
    public Chatserver(int pPort) {
        super(pPort);
    }

    /** Neue Verbindung: Client versucht einen Verbindungsaufbau **/
    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
            if(connections==0)  { hostIP = pClientIP; isHost=true; }
            if(connections==1) { joinIP = pClientIP;
            this.send(hostIP,pClientPort,"Neue Anmeldung von " + pClientIP);}
            connections++;

            if(isHost) this.send(hostIP, pClientPort, "Hallo " + hostIP + " auf Port " + pClientPort + " auf dem SchachServer!");
            else this.send(joinIP, pClientPort, "Hallo " + joinIP + " auf Port " + pClientPort + " auf dem SchachServer!");
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        String addressee;
        if(pClientIP.equals(hostIP))  addressee = joinIP;
        else  addressee = hostIP;

        //command rausfinden
        String command = pMessage.substring(0, Math.min(pMessage.length(), 4));
                switch (command) {
                    case (logout): {processClosingConnection(pClientIP, pClientPort); }//if host dann alles plattmachen
                        return;
                    case(nrOfConnections): this.send(pClientIP,pClientPort, String.valueOf(getConnections()));
                        return;
                    case (test): this.send(pClientIP,pClientPort,"BOAR[-2, -2, -3, -5, -6, -3, -4, 2, -1, -1, -1, -1, 1, -1, -1, -1, 0, 0, 0, 0, 0, 4, 0, 0, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, 0, 1, 1, 1, -4, 1, 1, 1, 1, 4, 2, 3, 5, 6, 3, 2, 4]");
                        return;
                    case (board): this.send(addressee, pClientPort, pMessage); //??
                }
                this.send(addressee,pClientPort,pMessage);
    }

    private int getConnections() {
        return connections;
    }

    public void anAlleSenden(String pMessage) {
        this.sendToAll(pMessage);
    }
    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, "Lieber Benutzer " + pClientIP + " auf Port " + pClientPort + " - auf Wiedersehen!");
    }
}