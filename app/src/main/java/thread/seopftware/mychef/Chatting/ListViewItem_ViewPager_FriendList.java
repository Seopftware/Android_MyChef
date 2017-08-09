package thread.seopftware.mychef.Chatting;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewItem_ViewPager_FriendList {

    String UpperTitle, BottomTitle;
    String Profile, Name, Message;

    public String getUpperTitle() {
        return this.UpperTitle;
    }

    public String getBottomTitle() {
        return BottomTitle;
    }

    public String getProfile() {
        return Profile;
    }

    public String getName() {
        return Name;
    }

    public String getMessage() {
        return Message;
    }

    public void setUpperTitle(String upperTitle) {
        UpperTitle = upperTitle;
    }

    public void setBottomTitle(String bottomTitle) {
        BottomTitle = bottomTitle;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
