import java.util.ArrayList;

public interface Filter
{
    public boolean accept(String word, ArrayList<String> stopWords);
}
