package thread.seopftware.mychef.HomeChef;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_Menu {

    String Id;
    String KoreaName;
    String EnglishName;
    String Price;
    String Date;
    String ImagePath;

    public void setId(String id) {
        Id = id;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    public void setKoreaName(String koreaName) {
        KoreaName = koreaName;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setImagePath(String imagePath) {
        ImagePath=imagePath;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getId() {
        return this.Id;
    }

    public String getKoreaName() {
        return this.KoreaName;
    }

    public String getEnglishName() {
        return this.EnglishName;
    }

    public String getPrice() {
        return this.Price;
    }

    public String getImagePath() {
        return this.ImagePath;
    }

    public String getDate() {
        return this.Date;
    }

}
