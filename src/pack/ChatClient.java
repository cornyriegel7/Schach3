package pack;
public class ChatClient extends Client {

    public Controller c;
    public StringListener StringEmpfangen;
    private String ip;

    private boolean gewaehrt;

    public ChatClient(String pServerIP, int pServerPort, Controller pController){
        super(pServerIP, pServerPort);
    }

    /*** Die Methode dient zum Senden von Nachrichten an den Server */
    public void nachrichtVersenden(String pText)
    {
        send(pText);
    }

    /*** Die Methode dient zur verarbeitung der Nachrichten vom Server */
    public void processMessage(String pMessage)
    {
        StringEmpfangen.getString(pMessage);
    }
}