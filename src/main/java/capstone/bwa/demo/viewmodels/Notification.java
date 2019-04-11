package capstone.bwa.demo.viewmodels;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class Notification {
    private int uid;
    private Date when;
    private List<Map<String, Object>> notifications;

    public Notification(int uid, Date when, List<Map<String, Object>> notifications) {
        this.uid = uid;
        this.when = when;
        this.notifications = notifications;

    }

    public Notification(int uid, Date when) {
        this.uid = uid;
        this.when = when;
    }

    public Notification(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public List<Map<String, Object>> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Map<String, Object>> notifications) {
        this.notifications = notifications;

    }
}
