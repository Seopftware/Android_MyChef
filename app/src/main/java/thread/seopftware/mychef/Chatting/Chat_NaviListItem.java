package thread.seopftware.mychef.Chatting;

import android.graphics.drawable.Drawable;

/**
 * Created by MSI on 2017-08-14.
 */

public class Chat_NaviListItem {

    String email;
    String name;
    String image;
    String friend;
    Drawable iconDrawable;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getFriend() {
        return friend;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }
}
