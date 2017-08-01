package thread.seopftware.mychef.HomeUser;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_Comment {

    String CreatedDate;
    String Comment;
    String Name;
    String Email;
    double RatingBar;

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setRatingBar(double ratingNumber) {
        RatingBar = ratingNumber;
    }

    public String getCreatedDate() {
        return this.CreatedDate;
    }

    public String getComment() {
        return this.Comment;
    }

    public String getName() {
        return this.Name;
    }

    public String getEmail() {
        return this.Email;
    }

    public double getRatingBar() {
        return this.RatingBar;
    }

}
