package pack;
import java.util.*;
//Todo: muss beim onlinespiel dauernd laufen und empfangsbereit sein, wenn int[] kommt an den controller geben und dann das board damit aktualisieren.
// man muss schauen, dass nur ein move at a time erlaubt ist. außerdem vielleicht server als interface von controller machen, damit die kommunikation
// funktioniert.
public class Chatserver extends Server {
    public static final String Abmeldung = "ABME", Anmeldung = "ANME", Nachricht = "MSSG";
    public Chatserver(int pPort) {
        super(pPort);
    }

    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, "Server: Hallo " + pClientIP + " auf Port " + pClientPort + " auf dem ChatServer!");
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
                switch (pMessage) {
                    case (Anmeldung):
                        send(pClientIP, pClientPort, anmeldungsBehandlung(pMessage, pClientIP, pClientPort));
                        return;
                    case (Abmeldung):
                        processClosingConnection(pClientIP, pClientPort);
                        return;
                    case (Nachricht):
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
