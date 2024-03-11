package pack;
//diese klasse dient nur zur nachrichtenweitergabe von view an den
// chatserver bzw. vom chatserver an die view
public class ChatClient extends Client {
    public StringListener StringEmpfangen;

    public ChatClient(String pServerIP, int pServerPort){
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