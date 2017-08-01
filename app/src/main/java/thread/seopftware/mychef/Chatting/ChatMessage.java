package thread.seopftware.mychef.Chatting;

/**
 * Created by MSI on 2017-07-29.
 */

public class ChatMessage {
    public int viewtype;
    public boolean me;
    public String email;
    public String name;
    public String time;
    public String message;
    public String profile;

    // 보내는 내용 ( 0.뷰타립, 1.나(상대방), 2.이메일, 3.이름, 4.시간, 5.메세지, 6.프로필 사진 주소 )
    public ChatMessage(int viewtype, boolean me, String email, String name, String time, String message, String profile) {
        super();
        this.viewtype = viewtype;
        this.me = me; // 1
        this.email = email; // 2
        this.name = name; // 3
        this.time = time; // 4
        this.message = message; // 5
        this.profile = profile; // 6
    }
}
