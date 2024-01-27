package pack;

//todo: ich denk ist es am besten hier int[]s zu verschicken, weil die eigene spielinstanz ja checkt ob der move lagal is.
// dann muss das array an den anderen server verschickt werden, der gibt das an den anderen controller weiter und das board wird aktualisiert.
public class ChatClient extends Client {
    private int[] toBeSentArray;

    //das ist alles kopiert, //todo: braucht man das überhaupt alles
    public final static String zuRaumHinzufügen = "ADDC";
    public final static String ausRaumEntfernen = "REMC";
    public final static String verbindungGewaehrt = "OKOK";
    public final static String verbindungVerweigert = "NONO";
    public final static  String neuenRaumErstellen = "NEWW";

    public final static String guiLoeschen = "DELG";

    public StringListener StringEmpfangen;

    private boolean gewaehrt;
    private String currentServerText;
    public ChatClient(String pServerIP, int pServerPort)
    {
        super(pServerIP, pServerPort);
        // receive braucht es nicht mehr, da es die neue Methode
        // processMasage gibt, die automatisch empfängt
    }

    /**
     * Die Methode dient zum Senden von Nachrichten an den Server
     */
    public void nachrichtVersenden(String pText)
    {
        send(pText);
    }

    public void anAlleVersenden(String pText)
    {
        send("ALLE" + pText);
    }

    /**
     * Mit dieser Methode wird die aktuelle Verbindung getrennt
     */
    public void beenden()
    {
        send("*bye");
    }

    /**
     * Die Methode dient zur verarbeitung der Nachrichten vom Server
     */
    public void processMessage(String pMessage)
    {
        //Vorgehen bei schlüsselwort
        String neueNachricht ="";
        char[] messageArray = new char[0];
        messageArray = pMessage.toCharArray();

        char[] ersteVierZeichen = new char[4];
        if(pMessage.length()>3) {
            for (int i = 0; i < 4; i++) {
                ersteVierZeichen[i] = messageArray[i];
            }
            String command = String.valueOf(ersteVierZeichen);



            switch (command) {
                case (guiLoeschen):
                    pMessage = guiLoeschen;
                    break;
                case ("NONO"):
                    pMessage = "Anmeldung fehlgeschlagen  \n(Anmeldedaten falsch/Account bereits online)";
                    break;

            }
        }

        //vorgehen ohne schlüsselwort
        StringEmpfangen.getString(pMessage);
        // for(int i=0;i<pMessage.length();i++) {
        // neueNachricht = neueNachricht + messageArray[i];
        // }

    }

    //credentials in lesbarer form an den server
    public void checkCredentials(String email, String name, String passwort) {
        //Credentials mit schlüsselwörtern an server
        nachrichtVersenden("ABCD" + "MAIL" + email
                + "USER" + name + "PASS" + passwort);
    }
    public void setIntArray(int[] pIntArray) {
        toBeSentArray = pIntArray;
        System.out.println("AMOGUS");
    }
}

