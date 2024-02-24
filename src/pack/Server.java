package pack;
import java.net.*;
import java.io.*;

/**
 * Objekte von Unterklassen der abstrakten Klasse Server ermoeglichen das
 * Anbieten von Serverdiensten, so dass Clients Verbindungen zum Server mittels
 * TCP/IP-Protokoll aufbauen koennen. Zur Vereinfachung finden Nachrichtenversand
 * und -empfang zeilenweise statt, d. h., beim Senden einer Zeichenkette wird ein
 * Zeilentrenner ergaenzt und beim Empfang wird dieser entfernt.
 * Verbindungsannahme, Nachrichtenempfang und Verbindungsende geschehen
 * nebenlaeufig. Auf diese Ereignisse muss durch Ueberschreiben der entsprechenden
 * Ereignisbehandlungsmethoden reagiert werden. Es findet nur eine rudimentaere
 * Fehlerbehandlung statt, so dass z.B. Verbindungsabbrueche nicht zu einem
 * Programmabbruch fuehren. Einmal unterbrochene oder getrennte Verbindungen
 * koennen nicht reaktiviert werden.
 */
public abstract class Server
{
    private final NewConnectionHandler connectionHandler;
    private final List<ClientMessageHandler> messageHandlers;

    private class NewConnectionHandler extends Thread
    {
        private ServerSocket serverSocket;
        private boolean active;

        public NewConnectionHandler(int pPort)
        {
            try
            {
                serverSocket = new ServerSocket(pPort);
                start();
                active = true;
            }
            catch (Exception e)
            {
                serverSocket = null;
                active = false;
            }
        }

        public void run()
        {
            while (active)
            {
                try
                {
                    //Warten auf Verbdinungsversuch durch Client:
                    Socket clientSocket = serverSocket.accept();
                    // Eingehende Nachrichten vom neu verbundenen Client werden
                    // in einem eigenen Thread empfangen:
                    addNewClientMessageHandler(clientSocket);
                    processNewConnection(clientSocket.getInetAddress().getHostAddress(),clientSocket.getPort());
                }

                catch (IOException e)
                {
                    /*
                     * Kann keine Verbindung zum anfragenden Client aufgebaut werden,
                     * geschieht nichts.
                     */
                }
            }
        }

        public void close()
        {
            active = false;
            if(serverSocket != null)
                try
                {
                    serverSocket.close();
                }
                catch (IOException e)
                {
                    /*
                     * Befindet sich der ServerSocket im accept()-Wartezustand oder wurde
                     * er bereits geschlossen, geschieht nichts.
                     */
                }
        }
    }

    private class ClientMessageHandler extends Thread
    {
        private final ClientSocketWrapper socketWrapper;
        private boolean active;

        private class ClientSocketWrapper
        {
            private Socket clientSocket;
            private BufferedReader fromClient;
            private PrintWriter toClient;

            public ClientSocketWrapper(Socket pSocket)
            {
                try
                {
                    clientSocket = pSocket;
                    toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                    fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                }
                catch (IOException e)
                {
                    clientSocket = null;
                    toClient = null;
                    fromClient = null;
                }
            }

            public String receive()
            {
                if(fromClient != null)
                    try
                    {
                        return fromClient.readLine();
                    }
                    catch (IOException e)
                    {
                    }
                return(null);
            }

            public void send(String pMessage)
            {
                if(toClient != null)
                {
                    toClient.println(pMessage);
                }
            }

            public String getClientIP()
            {
                if(clientSocket != null)
                    return(clientSocket.getInetAddress().getHostAddress());
                else
                    return(null); //Gemaess Java-API Rueckgabe bei nicht-verbundenen Sockets
            }

            public int getClientPort()
            {
                if(clientSocket != null)
                    return(clientSocket.getPort());
                else
                    return(0); //Gemaess Java-API Rueckgabe bei nicht-verbundenen Sockets
            }

            public void close()
            {
                if(clientSocket != null)
                    try
                    {
                        clientSocket.close();
                    }
                    catch (IOException e)
                    {
                        /*
                         * Falls eine Verbindung getrennt werden soll, deren Endpunkt
                         * nicht mehr existiert bzw. ihrerseits bereits beendet worden ist,
                         * geschieht nichts.
                         */
                    }
            }
        }

        private ClientMessageHandler(Socket pClientSocket)
        {
            socketWrapper = new ClientSocketWrapper(pClientSocket);
            if(pClientSocket!=null)
            {
                start();
                active = true;
            }
            else
            {
                active = false;
            }
        }

        public void run()
        {
            String message = null;
            while (active)
            {
                message = socketWrapper.receive();
                if (message != null)
                    processMessage(socketWrapper.getClientIP(), socketWrapper.getClientPort(), message);
                else
                {
                    ClientMessageHandler aMessageHandler = findClientMessageHandler(socketWrapper.getClientIP(), socketWrapper.getClientPort());
                    if (aMessageHandler != null)
                    {
                        aMessageHandler.close();
                        removeClientMessageHandler(aMessageHandler);
                        processClosingConnection(socketWrapper.getClientIP(), socketWrapper.getClientPort());
                    }
                }
            }
        }

        public void send(String pMessage)
        {
            if(active)
                socketWrapper.send(pMessage);
        }

        public void close()
        {
            if(active)
            {
                active=false;
                socketWrapper.close();
            }
        }

        public String getClientIP()
        {
            return(socketWrapper.getClientIP());
        }

        public int getClientPort()
        {
            return(socketWrapper.getClientPort());
        }

    }

    public Server(int pPort)
    {
        connectionHandler = new NewConnectionHandler(pPort);
        messageHandlers = new List<ClientMessageHandler>();
    }

    public boolean isOpen()
    {
        return(connectionHandler.active);
    }

    public boolean isConnectedTo(String pClientIP, int pClientPort)
    {
        ClientMessageHandler aMessageHandler = findClientMessageHandler(pClientIP, pClientPort);
        if (aMessageHandler != null)
            return(aMessageHandler.active);
        else
            return(false);
    }

    public void send(String pClientIP, int pClientPort, String pMessage)
    {
        ClientMessageHandler aMessageHandler = this.findClientMessageHandler(pClientIP, pClientPort);
        if (aMessageHandler != null)
            aMessageHandler.send(pMessage);
    }

    public void sendToAll(String pMessage)
    {
        synchronized(messageHandlers)
        {
            messageHandlers.toFirst();
            while (messageHandlers.hasAccess())
            {
                messageHandlers.getContent().send(pMessage);
                messageHandlers.next();
            }
        }
    }

    public void closeConnection(String pClientIP, int pClientPort)
    {
        ClientMessageHandler aMessageHandler = findClientMessageHandler(pClientIP, pClientPort);
        if (aMessageHandler != null)
        {
            processClosingConnection(pClientIP, pClientPort);
            aMessageHandler.close();
            removeClientMessageHandler(aMessageHandler);
        }

    }

    public void close()
    {
        connectionHandler.close();

        synchronized(messageHandlers)
        {
            ClientMessageHandler aMessageHandler;
            messageHandlers.toFirst();
            while (messageHandlers.hasAccess())
            {
                aMessageHandler = messageHandlers.getContent();
                processClosingConnection(aMessageHandler.getClientIP(), aMessageHandler.getClientPort());
                aMessageHandler.close();
                messageHandlers.remove();
            }
        }

    }

    public abstract void processNewConnection(String pClientIP, int pClientPort);

    public abstract void processMessage(String pClientIP, int pClientPort, String pMessage);

    public abstract void processClosingConnection(String pClientIP, int pClientPort);

    private void addNewClientMessageHandler(Socket pClientSocket)
    {
        synchronized(messageHandlers)
        {
            messageHandlers.append(new Server.ClientMessageHandler(pClientSocket));
        }
    }

    private void removeClientMessageHandler(ClientMessageHandler pClientMessageHandler)
    {
        synchronized(messageHandlers)
        {
            messageHandlers.toFirst();
            while (messageHandlers.hasAccess())
            {
                if (pClientMessageHandler ==  messageHandlers.getContent())
                {
                    messageHandlers.remove();
                    return;
                }
                else
                    messageHandlers.next();
            }
        }
    }

    public List<ClientMessageHandler> getClientMessageHandlers(){
        return messageHandlers;
    }

    private ClientMessageHandler findClientMessageHandler(String pClientIP, int pClientPort)
    {
        synchronized(messageHandlers)
        {
            ClientMessageHandler aMessageHandler;
            messageHandlers.toFirst();

            while (messageHandlers.hasAccess())
            {
                aMessageHandler = messageHandlers.getContent();
                if (aMessageHandler.getClientIP().equals(pClientIP) && aMessageHandler.getClientPort() == pClientPort)
                    return (aMessageHandler);
                messageHandlers.next();
            }
            return (null);
        }
    }

}
