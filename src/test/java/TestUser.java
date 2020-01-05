import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试用户
 */
public class TestUser {
    /**
     * 当前血量
     */
    public int currHp;

    /**
     * 当前血量，使用AtomicInteger 确实可以保证线程安全，
     * 当时，但用户类里有那么多字段，不能全用Atomic类型，
     * 这样会让用户类变得特别臃肿...
     * 而且，这样也不能彻底解决问题，例如：
     * 属性A本身是线程安全的，
     * 属性B本身也是线程安全的，
     * 但无法保证同时操作A和B是线程安全的！
     */

    public AtomicInteger safeCurrHp;

    /**
     * 减血
     * @param delta
     */
    public synchronized void subtractHp(int delta){
        if(delta <= 0){
            return;
        }
        currHp = currHp - 10;
    }

    /**
     * 攻击用户
     * @param targetUser
     */
    public void attkUser(TestUser targetUser){
        if(targetUser == null){
            return;
        }

        int subtractHp;
        synchronized(this){
            subtractHp = 10;
        };
        targetUser.subtractHp(subtractHp);
    }
}
