package pack;
public class Chatserver extends Server {
    public static final String logout = "ABME", activeNow = "ACTN", inActiveNow = "ACTF", test = "TEST", board = "BOAR", nrOfConnections = "CONN", reciever = "ADDR", weiss = "WHIT", schwarz = "BLAC";
    private String hostIP, joinIP, addressee;
    private boolean isHost;
    int connections;
    public Chatserver(int pPort) {
        super(pPort);
    }

    /** Neue Verbindung: Client versucht einen Verbindungsaufbau **/
    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        if(connections==0)  {
            hostIP = pClientIP;
            isHost=true;
            this.send(hostIP,pClientPort,weiss);
            this.send(hostIP, pClientPort, activeNow);
        }
        if(connections==1) {
            joinIP = pClientIP;
            this.send(joinIP,pClientPort,schwarz);
            this.send(joinIP,pClientPort,inActiveNow);
            this.send(hostIP,pClientPort,"Neue Anmeldung von " + pClientIP);
        }
        connections++;

        if(isHost) this.send(hostIP, pClientPort, "Hallo " + hostIP + " auf Port " + pClientPort + " auf dem SchachServer!");
        else this.send(joinIP, pClientPort, "Hallo " + joinIP + " auf Port " + pClientPort + " auf dem SchachServer!");
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        if(pClientIP.equals(hostIP))  addressee = joinIP;
        else  addressee = hostIP;

        //command rausfinden
        String command = pMessage.substring(0, Math.min(pMessage.length(), 4));
        switch (command) {
            case (logout): {processClosingConnection(pClientIP, pClientPort);
            }
            return;
            case(nrOfConnections): this.send(pClientIP,pClientPort, String.valueOf(getConnections()));
                return;
            case (test): this.send(pClientIP,pClientPort,"BOAR[-2, -2, -3, -5, -6, -3, -4, 2, -1, -1, -1, -1, 1, -1, -1, -1, 0, 0, 0, 0, 0, 4, 0, 0, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, 0, 1, 1, 1, -4, 1, 1, 1, 1, 4, 2, 3, 5, 6, 3, 2, 4]");
                return;
            //verschickt das neue brett
            case (board): this.send(addressee, pClientPort, pMessage);
                return;
            case(reciever): this.send(pClientIP, pClientPort, getAddressee());
                return;
        }
        this.send(addressee,pClientPort,pMessage);
    }

    //alle menschen, die verbunden sind - moeglich 1 oder 2
    private int getConnections() {
        return connections;
    }

    //der, dem die nachrichten aus geschickt werden müssen. von host zu join und von join zu host
    private String getAddressee() {
        return addressee;
    }
    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        this.send(pClientIP, pClientPort, "Lieber Benutzer " + pClientIP + " auf Port " + pClientPort + " - auf Wiedersehen!");
    }
}