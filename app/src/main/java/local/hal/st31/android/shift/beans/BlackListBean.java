package local.hal.st31.android.shift.beans;

public class BlackListBean {
    private int id;
    private int myId;
    private int userId;
    private String nickName;
    private int blackRank;
    private int colorCode;
    private int groupId;

    public BlackListBean(){
        blackRank = 0;
    }

    @Override
    public String toString() {
        return "BlackListBean{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", blankRank=" + blackRank +
                ", colorCode='" + colorCode + '\'' +
                '}';
    }

    public int getMyId() {
        return myId;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getBlackRank() {
        return blackRank;
    }

    public void setBlackRank(int blackRank) {
        this.blackRank = blackRank;
    }

    public int getColorCode() {
        return colorCode;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}