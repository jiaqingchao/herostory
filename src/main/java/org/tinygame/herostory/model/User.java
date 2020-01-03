package org.tinygame.herostory.model;

/**
 * 用户
 */
public class User {
    /**
     * 用户ID
     */
    private int userId;
    /**
     * 英雄形象
     */
    private String heroAvatar;

    private int totalHp = 100;

    private int surplusHp = 100;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }

    public int getUserId() {
        return userId;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public int getTotalHp() {
        return totalHp;
    }

    public void setTotalHp(int totalHp) {
        this.totalHp = totalHp;
    }

    public int getSurplusHp() {
        return surplusHp;
    }

    public void setSurplusHp(int surplusHp) {
        this.surplusHp = surplusHp;
    }
}
