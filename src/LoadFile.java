import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.CREATE;

public class LoadFile {
    public static ArrayList retriveFile()
    {
        JFileChooser chooser = new JFileChooser();
        File desiredFile;
        String readLine = "";

        ArrayList<String> storedData = new ArrayList<String>();
        try
        {
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                desiredFile = chooser.getSelectedFile();
                Path file = desiredFile.toPath();
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                while (reader.ready()) {
                    readLine = reader.readLine();
                    String[] temp = readLine.split(" ");
                    for(int l = 0; l < temp.length ; l++) //store each string into an array list
                    {
                        storedData.add(temp[l]); //remove spaces
                    }
                }
                reader.close();
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println(("File not found"));
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return storedData;

    }
}
