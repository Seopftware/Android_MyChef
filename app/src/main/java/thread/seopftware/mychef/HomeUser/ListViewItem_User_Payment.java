package thread.seopftware.mychef.HomeUser;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_User_Payment {

    String KoreaName;
    String EnglishName;
    String ChefName;
    String Price;
    String Count;
    String ImagePath;


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

    public void setChefName(String chefName) {
        ChefName = chefName;
    }

    public void setCount(String count) {
        Count=count;
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

    public String getChefName() {
        return this.ChefName;
    }

    public String getCount() {
        return this.Count;
    }

}
