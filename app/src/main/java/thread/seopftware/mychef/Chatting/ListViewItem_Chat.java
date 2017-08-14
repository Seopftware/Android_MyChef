package thread.seopftware.mychef.Chatting;

/**
 * Created by MSI on 2017-08-01.
 */

public class ListViewItem_Chat {

    private int type;
    private String status;
    private String email;
    private String name;
    private String time;
    private String message;
    private String profile;

//    private String othername;
//    private String other

    public void setType(int type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getProfile() {
        return profile;
    }
}
