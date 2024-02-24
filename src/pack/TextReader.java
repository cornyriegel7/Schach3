package pack;

import java.io.FileReader;
import java.io.IOException;

public class TextReader {
    private final FileReader fileReader;

    public TextReader(String fileName) throws IOException {
        fileReader = new FileReader(fileName);
    }

    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder();
        int character;

        while ((character = fileReader.read()) != -1) {
            if (character == '\n') {
                break; // Zeilenende erreicht
            }
            line.append((char) character);
        }

        if (line.length() == 0 && character == -1) {
            return null; // Dateiende erreicht
        }

        return line.toString();
    }

    public void close() throws IOException {
        fileReader.close();
    }

}


