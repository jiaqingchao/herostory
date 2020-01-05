package org.tinygame.herostory.model;

import java.util.concurrent.atomic.AtomicInteger;

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
    /**
     * 移动状态
     */
    public final MoveState moveState = new MoveState();

    private int totalHp;

    private AtomicInteger currHp;

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

    public int getCurrHp() {
        return currHp.get();
    }

    public MoveState getMoveState() {
        return moveState;
    }

    public void setCurrHp(int currHp) {
        this.currHp.set(currHp);;
    }
    public void subtractHp(int currHp) {
        this.currHp.addAndGet(-currHp);;
    }
}
