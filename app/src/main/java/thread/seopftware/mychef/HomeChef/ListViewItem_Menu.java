package thread.seopftware.mychef.HomeChef;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_Menu {

    private String KoreaName;
    private String EnglishName;
    private String Price;
    private String ImagePath;

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
}
