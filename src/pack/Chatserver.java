package pack;
import java.util.*;
//Todo: muss beim onlinespiel dauernd laufen und empfangsbereit sein, wenn int[] kommt an den controller geben und dann das board damit aktualisieren.
// man muss schauen, dass nur ein move at a time erlaubt ist. außerdem vielleicht server als interface von controller machen, damit die kommunikation
// funktioniert.
public class Chatserver extends Server
{
    //nachricht an Sender zurück
    //interne Schlüsselwörter
    public static final String Abmeldung = "*bye", Anmeldung = "ABCD";
    public static final String eMailAdresse = "MAIL", benutzerName = "USER", passwort = "PASS";
    public static final String anmeldungErfolgreich = "OKOK", anmeldungFehlgeschlagen = "NONO";
    public static final String nachricht = "MSSG";
    public final static  String neuenRaumErstellen = "NEWW";
    public final static String raumName = "RNAM";

    //Commands vom USER
    public static final String werIstOnline = "/onl", adden = "/add";

    ChatRaum Alle;

    private ArrayList<ChatRaum> raumList;
    private ArrayList<BenutzerKonto> konten; // die Liste von allen bekannten Konten

    public class ChatRaum{
        ArrayList<BenutzerKonto> memberList; // die Liste mit allen Leuten die in die Gruppe gekommen sind
        String Name;
        public ChatRaum(String Name){
            memberList = new ArrayList<BenutzerKonto>();
            this.Name = Name;
        }

        public String getName() {
            return Name;
        }

        public void addTeilnehmer(BenutzerKonto pBenutzer){
            memberList.add(pBenutzer);
        }

        public void entferneTeilnehmer(BenutzerKonto pBenutzer){
            memberList.remove(pBenutzer);
        }

        public ArrayList<BenutzerKonto> getAlleTeilnehmer()
        {
            return memberList;
        }
    }
    public Chatserver(int pPort)
    {
        super (pPort);

        raumList = new ArrayList<>();
        kontenabgleich(); //bereits abgespeicherte Konten werden aus dem Speicher geladen
        Alle = new ChatRaum("Raum mit Allen");
        raumList.add(Alle);
    }

    public BenutzerKonto getKontoByPort(int pClientPort)
    {
        for(int i = 0;i< konten.size();i++)
        {
            if(konten.get(i).gibPortal() == pClientPort)
            {
                return konten.get(i);
            }
        }
        return null;
    }

    //gleiche Konten Mit Der Konten Datei Ab
    private void kontenabgleich()
    {
        BenutzerKontenListe bKontenListe = new BenutzerKontenListe();
        konten = bKontenListe.gibListe();
    }

    /**
     * Wenn ein Client anfragt, bekommt er auftomatisch diese Verbindung
     * und somit diesen Satz geschickt!
     */
    public void processNewConnection(String pClientIP, int pClientPort)
    {
        this.send(pClientIP, pClientPort, "Server: Hallo " + pClientIP + " auf Port " + pClientPort + " auf dem ChatServer!");
    }

    public void processMessage(String pClientIP, int pClientPort, String pMessage) {

        String neueNachricht = null;
        if (pMessage != null) {
            neueNachricht = "";
            char[] messageArray = new char[0];
            messageArray = pMessage.toCharArray();
            char[] ersteVierZeichen = new char[4];
            if (pMessage.length() > 3) {
                for (int i = 0; i < 4; i++) {
                    ersteVierZeichen[i] = messageArray[i];
                }
                String command = String.valueOf(ersteVierZeichen);

                switch (command) {
                    case (Anmeldung):
                        send(pClientIP, pClientPort, anmeldungsBehandlung(pMessage, pClientIP, pClientPort));
                        return;
                    case (Abmeldung):
                        processClosingConnection(pClientIP, pClientPort);
                        return;
                    case (neuenRaumErstellen):
                        String nameNeuerRaum = pMessage.replace(neuenRaumErstellen, "");
                        for(int i = 0;i<raumList.size();i++){
                            if(nameNeuerRaum.equals(raumList.get(i).getName())){
                                this.send(pClientIP,pClientPort,"Server: Der Name '"+nameNeuerRaum+"' ist schon belegt");
                                return;}}
                        ChatRaum c = new ChatRaum(nameNeuerRaum);
                        raumList.add(c);
                        c.addTeilnehmer(getKontoByPort(pClientPort));
                        this.send(pClientIP, pClientPort, "RONL");
                        return;
                    case (nachricht):
                        neueNachricht = pMessage.replace(nachricht, "");
                        normaleNachrichtBehandlung(pClientIP,pClientPort,neueNachricht);
                }
            }
        }
    }

    private void normaleNachrichtBehandlung(String pClientIP,int pClientPort,String pMessage) {
        String[] a = pMessage.split(Chatserver.raumName);
        String raumName = a[1];
        String nachricht = a[0];
        ChatRaum chatRaum = new ChatRaum("");

        for (int i = 0; i < raumList.size(); i++) {
            if (raumList.get(i).getName().equals(raumName)) {
                chatRaum = raumList.get(i);
            }
        }

        char[] ersteVierZeichen = new char[4];
        if (nachricht.length() > 3) {
            for (int i = 0; i < 4; i++) {
                //Fehler bei einem Zeichen
                ersteVierZeichen[i] = nachricht.toCharArray()[i];
            }
            String command = String.valueOf(ersteVierZeichen);

            switch (command) // befehle die über die Nachrichten übergeben werden
            {
                case (werIstOnline):
                    this.send(pClientIP, pClientPort, "Server: Es sind " + onlineZaehler() + " Personen verbunden");
                    return;
                case (adden):
                    String benutzerName = nachricht.substring(4);
                    for (int i = 0; i < konten.size(); i++) {
                        if (benutzerName.equals(konten.get(i).gibBenutzerName())) {
                            //nachgucken ob der Benutzer schon im Raum ist
                            for(int k = 0;k<chatRaum.getAlleTeilnehmer().size();k++)
                            {
                                if(benutzerName.equals(chatRaum.getAlleTeilnehmer().get(k).gibBenutzerName()))
                                {
                                    send("Server: " + pClientIP,pClientPort, "Dieser Nutzer ist bereits in diesem Raum!");
                                    return;
                                }
                            }

                            for(int j = 0;j<chatRaum.getAlleTeilnehmer().size();j++)
                            {
                                BenutzerKonto b = chatRaum.getAlleTeilnehmer().get(j);
                                this.send("Server: " + b.gibIP(),b.gibPortal(),benutzerName + " ist diesem Raum beigetreten");
                            }
                            chatRaum.addTeilnehmer(konten.get(i));
                            this.send(konten.get(i).gibIP(), konten.get(i).gibPortal(), "RONL" + chatRaum.getName());
                            return;
                        }
                    }
                    break;
                case("/lev"):
                    for(int i = 0;i<chatRaum.getAlleTeilnehmer().size();i++)
                    {
                        BenutzerKonto b = chatRaum.getAlleTeilnehmer().get(i);
                        if(b.gibPortal() == pClientPort)
                        {
                            this.send(pClientIP,pClientPort, "Du hast diesen Raum verlassen!");
                            this.send(b.gibIP(),b.gibPortal(),"RLEV" + chatRaum.getName());
                            chatRaum.entferneTeilnehmer(getKontoByPort(pClientPort));
                        }
                    }
                    for(int j = 0;j<chatRaum.getAlleTeilnehmer().size();j++)
                    {
                        BenutzerKonto b = chatRaum.getAlleTeilnehmer().get(j);
                        this.send(b.gibIP(),b.gibPortal(),b.gibBenutzerName() + " hat den Raum verlassen");
                    }
                    return;

            }
        }

        for (int i = 0; i < chatRaum.getAlleTeilnehmer().size(); i++) {
            BenutzerKonto tempKonto = chatRaum.getAlleTeilnehmer().get(i);
            if (tempKonto.isAufDerLinie() && tempKonto.gibPortal() != pClientPort) {
                this.send(tempKonto.gibIP(), tempKonto.gibPortal(),"MSSG"+getKontoByPort(pClientPort).gibBenutzerName() + ": " + nachricht + "RNAM" + chatRaum.getName());
            } else if (tempKonto.gibPortal() == pClientPort) {
                this.send(tempKonto.gibIP(), tempKonto.gibPortal(), "Du: " + nachricht );
            }
        }

    }

    public String anmeldungsBehandlung(String pAnmeldeString, String clientIP, int clientPort)
    {
        BenutzerKonto kZuVergleichen;

        //aus den gegebenen Konto erstellen
        String a = pAnmeldeString.split(Chatserver.eMailAdresse)[1];
        String b[] = a.split(Chatserver.benutzerName);
        String c[] = b[1].split(Chatserver.passwort);

        kZuVergleichen = new BenutzerKonto(b[0],c[0],c[1]);

        //Vergleichsschleife
        for(int i=0; i<konten.size(); i++) {

            if(kZuVergleichen.isEqual(konten.get(i)) && !konten.get(i).isAufDerLinie()) //konto existiert bereits
            {

                konten.get(i).setzeAufDerLinie(true);
                konten.get(i).setzeIP(clientIP);
                konten.get(i).setzePortal(clientPort);

                Alle.addTeilnehmer(konten.get(i));
                return anmeldungErfolgreich;
            }
        }
        return anmeldungFehlgeschlagen;
    }

    public void abmeldungsBehandlung(int pClientPort)
    {
        for(int i = 0;i< konten.size();i++)
        {
            if(konten.get(i).gibPortal() == pClientPort) //ist kürzer zu vergleichen
            {
                konten.get(i).setzeAufDerLinie(false);
                konten.get(i).setzeIP(null);
                konten.get(i).setzePortal(0);
            }
        }
    }

    public void processClosingConnection(String pClientIP, int pClientPort)
    {
        this.send(pClientIP, pClientPort, pClientIP + " Server: " + pClientPort + "auf Wiedersehen!");
        abmeldungsBehandlung(pClientPort);
        //die connection muss auch geschlossen werden nicht nur string versenden
    }

    public String onlineZaehler() {
        int count=0;
        for(int i=0; i<konten.size(); i++) {
            if(konten.get(i).isAufDerLinie()){
                count++;
            }
        }
        return String.valueOf(count);
    }
}
