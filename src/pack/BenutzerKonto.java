package pack;

public class BenutzerKonto
{
    private String benutzerName, passwort, ePost, IP;
    private boolean aufDerLinie;
    private int port;

    public BenutzerKonto(String pEmail, String pBenutzerName, String pPasswort)
    {
        benutzerName = pBenutzerName;
        passwort = pPasswort;
        ePost = pEmail;
    }

    public boolean isEqual(BenutzerKonto pBenutzerKonto)
    {
        if(benutzerName.equals(pBenutzerKonto.gibBenutzerName())
                &&passwort.equals(pBenutzerKonto.gibPasswort())
                &&ePost.equals(pBenutzerKonto.gibEmail()))
        {
            return true;
        }
        return false;
    }

    public void setzeBenutzername(String pName) {
        benutzerName = pName;
    }

    public void setzeEPost(String pEPost) {
        ePost = pEPost;
    }

    public void setzePasswort(String pPasswort) {
        passwort = pPasswort;
    }

    public void setzeAufDerLinie(boolean pAufDerLinie){
        aufDerLinie=pAufDerLinie;
    }

    public void setzeIP(String IP) {
        this.IP = IP;
    }

    public void setzePortal(int portal) {
        this.port = portal;
    }

    public String gibBenutzerName(){
        return benutzerName;
    }

    public String gibPasswort(){
        return passwort;
    }

    public String gibEmail(){
        return ePost;
    }

    public String gibAlles() {
        return benutzerName + " " + passwort + " " + ePost;
    }

    public boolean isAufDerLinie() {
        return aufDerLinie;
    }

    public String gibIP()
    {
        return IP;
    }

    public int gibPortal() {
        return port;
    }
}


