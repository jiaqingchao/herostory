import java.util.concurrent.atomic.AtomicInteger;

public class TestUser {
    public int currHp;

    public AtomicInteger safeCurrHp;

    public synchronized void subtractHp(int delta){
        if(delta <= 0){
            return;
        }
        currHp = currHp - 10;
    }

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
