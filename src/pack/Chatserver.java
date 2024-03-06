package pack;
import java.util.*;
//Todo: muss beim onlinespiel dauernd laufen und empfangsbereit sein, wenn int[] kommt an den controller geben und dann das board damit aktualisieren.
// man muss schauen, dass nur ein move at a time erlaubt ist. außerdem vielleicht server als interface von controller machen, damit die kommunikation
// funktioniert.
public class Chatserver extends Server {
    public static final String Abmeldung = "ABME", Anmeldung = "ANME", Nachricht = "MSSG", TestNachricht = "TEST", BrettEmpfangen = "BOAR";
    public Chatserver(int pPort) {
        super(pPort);
    }

    /** Neue Verbindung: Client versucht einen Verbindungsaufbau **/
    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, "Hallo " + pClientIP + " auf Port " + pClientPort + " auf dem SchachServer!");
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {

        //command rausfinden
        String command = pMessage.substring(0, Math.min(pMessage.length(), 4));
                switch (command) {
                    case (Abmeldung): processClosingConnection(pClientIP, pClientPort);
                        return;
                    case (Nachricht): this.send(pClientIP,pClientPort,pMessage.substring(4));
                        return;
                    case (TestNachricht): this.send(pClientIP,pClientPort,"BOAR[-2, -2, -3, -5, -6, -3, -4, 2, -1, -1, -1, -1, 1, -1, -1, -1, 0, 0, 0, 0, 0, 4, 0, 0, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, 0, 1, 1, 1, -4, 1, 1, 1, 1, 4, 2, 3, 5, 6, 3, 2, 4]");
                        return;
                    case (BrettEmpfangen): this.send(pClientIP, pClientPort, pMessage);
                }
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, pClientIP + " Server: " + pClientPort + "auf Wiedersehen!");
        abmeldungsBehandlung(pClientPort);
    }

    public String anmeldungsBehandlung(String pAnmeldeString, String clientIP, int clientPort) {
        return "DA VERSUCHT SICH JEMAND ANZUMELDEN";
    }

    public void abmeldungsBehandlung(int pClientPort)
    {
        //die connection muss auch geschlossen werden nicht nur string versenden
    }
}
