package thread.seopftware.mychef.HomeUser;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_User_Search {

    String Count;
    String Word;
    String Ranking;


    public void setCount(String count) {
        Count = count;
    }

    public void setWord(String word) {
        Word = word;
    }

    public void setRanking(String ranking) {
        Ranking=ranking;
    }


    public String getCount() {
        return this.Count;
    }

    public String getWord() {
        return this.Word;
    }


    public String getRanking() {
        return Ranking;
    }
}
