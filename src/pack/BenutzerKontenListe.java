package pack;

import java.util.ArrayList;
public class BenutzerKontenListe
{
    private ArrayList<BenutzerKonto> konten;

    private TextReader emailReader;
    private TextReader usernameReader;
    private TextReader passwordReader;
    public BenutzerKontenListe()
    {
        konten = new ArrayList<BenutzerKonto>();
        try
        {
            emailReader = new TextReader("emailList.txt");
        }
        catch (java.io.IOException ioe) { ioe.printStackTrace();}
        try
        {
            usernameReader = new TextReader("usernameList.txt");
        }
        catch (java.io.IOException ioe) { ioe.printStackTrace();}
        try
        {
            passwordReader = new TextReader("passwordList.txt");
        }
        catch (java.io.IOException ioe) { ioe.printStackTrace();}
        try
        {
            füllen();
        }
        catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void füllen() throws java.io.IOException {
        String tempMail = "";
        String tempUser = "";
        String tempPass = "";
        while(true) {
            BenutzerKonto tempBK = new BenutzerKonto("a", "b", "c");
            //Mail lesen
            try
            {
                tempMail = emailReader.readLine();
            }
            catch (java.io.IOException ioe)
            {
                ioe.printStackTrace();
            }
            if(tempMail==null) {
                emailReader.close();
                usernameReader.close();
                passwordReader.close();
                break;
            }

            tempMail = tempMail.substring(0, tempMail.length()-1);

            tempBK.setzeEPost(tempMail);

            //////////////////////Username///////////////////////////////////////////////////////
            try
            {
                tempUser = usernameReader.readLine();
            }
            catch (java.io.IOException ioe)
            {
                ioe.printStackTrace();
            }
            tempUser = tempUser.substring(0, tempUser.length()-1);

            tempBK.setzeBenutzername(tempUser);

            //////////////////////Password///////////////////////////////////////////////////////
            try
            {
                tempPass = passwordReader.readLine();
            }
            catch (java.io.IOException ioe)
            {
                ioe.printStackTrace();
            }
            tempPass = tempPass.substring(0, tempPass.length()-1);

            tempBK.setzePasswort(tempPass);
            konten.add(tempBK);
        }
    }

    public ArrayList<BenutzerKonto> gibListe() {
        return konten;
    }
}

