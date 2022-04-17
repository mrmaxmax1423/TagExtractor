import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;

public class TaxExtractorFrame extends JFrame{
    JPanel mainPanel;

    JPanel resultsPanel;
    JLabel textAreaLabel;
    JTextArea resultsTextArea;
    JScrollPane scrollPane;

    JPanel controlPanel;
    JButton loadTextButton;
    JButton loadStopWordsButton;
    JButton runCheckButton;

    JPanel savePanel;
    JButton saveFileButton;
    JTextArea fileNameArea;

    ArrayList<String> rawText = new ArrayList<String>();
    ArrayList<String> stopWords = new ArrayList<String>();

    LinkedHashMap<String, Integer> sortedWordFreq = new LinkedHashMap<>();

    Map<String, Integer> wordFreq = new HashMap<String, Integer>();
    public TaxExtractorFrame()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        createSavePanel();
        mainPanel.add(savePanel, BorderLayout.SOUTH);

        add(mainPanel);
        setSize(900,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void createControlPanel()
    {
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1,3));

        loadTextButton = new JButton("Pick Text file To Read From");
        loadTextButton.addActionListener((ActionEvent ae) -> rawText = readFile());
        loadStopWordsButton = new JButton("Pick Stop Words File");
        loadStopWordsButton.addActionListener((ActionEvent ae) -> stopWords = readFile());
        runCheckButton = new JButton("Process File");
        runCheckButton.addActionListener((ActionEvent ae) -> filterFile(rawText, stopWords));

        loadStopWordsButton.setFont(new java.awt.Font("Serif", 0, 20));
        loadTextButton.setFont(new java.awt.Font("Serif", 0, 20));
        runCheckButton.setFont(new java.awt.Font("Serif", 0, 20));
        controlPanel.add(loadStopWordsButton);
        controlPanel.add(loadTextButton);
        controlPanel.add(runCheckButton);
    }

    public void createResultsPanel()
    {
        resultsPanel = new JPanel();
        resultsTextArea = new JTextArea(20, 75);
        scrollPane = new JScrollPane(resultsTextArea);
        textAreaLabel = new JLabel("Tag Extractor Results:");

        DefaultCaret caret = (DefaultCaret)resultsTextArea.getCaret(); //stop auto scroll so new fortunes can be added at the top
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        resultsPanel.add(textAreaLabel);
        resultsPanel.add(scrollPane);
    }

    public void createSavePanel()
    {
        savePanel = new JPanel();
        fileNameArea = new JTextArea();
        saveFileButton = new JButton("Save as file");

        saveFileButton.setFont(new java.awt.Font("Serif", 0, 20));
        saveFileButton.addActionListener((ActionEvent ae) -> saveFile());
        fileNameArea.setFont(new java.awt.Font("Serif", 0, 20));
        fileNameArea.setText("fileName");

        savePanel.add(saveFileButton);
        savePanel.add(fileNameArea);
    }


    public void filterFile(ArrayList<String> text, ArrayList<String> tags)
    {
        StopWordFilter sWFilter = new StopWordFilter();
        for(String word : text)
        {
            if(!word.equalsIgnoreCase("")){ //stop spaces from being recorded
                if (sWFilter.accept(word, tags)) //checks if word is a stop word
                {
                    if (wordFreq.containsKey(word)) //check if word is already tracked
                    {
                        wordFreq.replace(word, wordFreq.get(word) + 1); //increment value by 1
                    } else
                        wordFreq.put(word, 1); //create new Map entry with key-word
                }
            }
        }
        displayResults(wordFreq);
    }

    public void displayResults(Map<String, Integer> wordFreq)
    {
        LinkedHashMap<String, Integer> sortedWordFreq = new LinkedHashMap<>();
        int counter = 0; //seperates textArea lines every 10 words added

        wordFreq.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedWordFreq.put(x.getKey(), x.getValue())); //sort data by values (which words appear most)
        for(String key : sortedWordFreq.keySet())
        {
            resultsTextArea.append(key.substring(0, 1).toUpperCase() + key.substring(1) + ": " + sortedWordFreq.get(key) + " "); //Capitalize first letter
            counter ++;
            if(counter % 12 == 0)
            {
                resultsTextArea.append("\n");
            }
        }
    }

    public void saveFile()
    {
        String fileName;
        Scanner fileNameInput = new Scanner(System.in);
        fileName = fileNameArea.getText();

        File workingDirectory = new File(System.getProperty("user.dir"));
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + fileName + ".txt");

        try
        {
            OutputStream out =
                    new BufferedOutputStream(Files.newOutputStream(file, CREATE));
            BufferedWriter writer =
                    new BufferedWriter(new OutputStreamWriter(out));
            writer.write(resultsTextArea.getText());
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList readFile()
    {
        JFileChooser chooser = new JFileChooser();
        File desiredFile;
        String rec = "";
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
                    rec = reader.readLine();
                    String[] temp = rec.split(" ");
                    for(int l = 0; l < temp.length ; l++) //store each string into an array list
                    {
                        if(temp[l] != "/" && temp[l]!= "'"){
                            storedData.add(temp[l].replaceAll("[^a-zA-Z0-9]", "").toLowerCase());
                        }
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


