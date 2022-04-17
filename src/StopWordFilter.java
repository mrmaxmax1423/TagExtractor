import java.util.ArrayList;

public class StopWordFilter implements Filter
{
    public boolean accept(String word, ArrayList<String> stopWords) {
        for(int i = 0 ; i < stopWords.size() ; i++) {
            if (word.equalsIgnoreCase(stopWords.get(i))) {
                return false;
            }
        }
            return true;
    }
}
