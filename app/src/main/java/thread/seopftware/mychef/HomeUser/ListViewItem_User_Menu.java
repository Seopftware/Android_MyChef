package thread.seopftware.mychef.HomeUser;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_User_Menu {

    String Id;
    String KoreaName;
    String EnglishName;
    String ChefName;
    String Price;
    String ImagePath;
    String ReviewTotal;
    double RatingBar;

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

    public void setChefName(String chefName) {
        ChefName = chefName;
    }

    public void setReviewTotal(String reviewTotal) {
        ReviewTotal = reviewTotal;
    }

    public void setRatingBar(double ratingNumber) {
        RatingBar = ratingNumber;
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

    public String getChefName() {
        return this.ChefName;
    }

    public String getReviewTotal() {
        return this.ReviewTotal;
    }

    public double getRatingBar() {
        return this.RatingBar;
    }

}
