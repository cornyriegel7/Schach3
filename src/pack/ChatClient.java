package pack;

//todo: ich denk ist es am besten hier int[]s zu verschicken, weil die eigene spielinstanz ja checkt ob der move lagal is.
// dann muss das array an den anderen server verschickt werden, der gibt das an den anderen controller weiter und das board wird aktualisiert.
public class ChatClient extends Client {

    public Controller c;
    private int[] toBeSentArray;
    public StringListener StringEmpfangen;
    private String ip;

    private boolean gewaehrt;
    private String currentServerText;

    public ChatClient(String pServerIP, int pServerPort, Controller pController){
        super(pServerIP, pServerPort);
    }

    /*** Die Methode dient zum Senden von Nachrichten an den Server */
    public void nachrichtVersenden(String pText)
    {
        send(pText);
    }

    /*** Mit dieser Methode wird die aktuelle Verbindung getrennt */
    public void beenden()
    {
        send("ABME");
    }

    /*** Die Methode dient zur verarbeitung der Nachrichten vom Server */
    public void processMessage(String pMessage)
    {
        //weitergabe an die View
        StringEmpfangen.getString(pMessage);
    }
    public void setIntArray(int[] pIntArray) {
        toBeSentArray = pIntArray;
    }

    public void setIp(String pIp){
        ip = pIp;
    }
}