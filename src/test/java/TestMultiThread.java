import java.util.concurrent.atomic.AtomicInteger;

public class TestMultiThread {
    public static void main(String[] args) {
        for (int i = 1; i < 10000; i++) {
            System.out.println("第 " + i + " 次测试");
            new TestMultiThread().test4();
        }
    }
    private void test1() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                newUser.currHp = newUser.currHp - 10;
            });
        }

        threads[0].start();
        threads[1].start();

        try {
            threads[0].join();
            threads[1].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (newUser.currHp != 80) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.currHp);
        }
        System.out.println("当前血量正确, currHp = " + newUser.currHp);
    }

    private void test2() {
        TestUser newUser = new TestUser();
        newUser.currHp = 100;
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                newUser.subtractHp(10);
            });
        }

        threads[0].start();
        threads[1].start();

        try {
            threads[0].join();
            threads[1].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (newUser.currHp != 80) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.currHp);
        }
        System.out.println("当前血量正确, currHp = " + newUser.currHp);
    }

    private void test3() {
        TestUser newUser1 = new TestUser();
        newUser1.currHp = 100;

        TestUser newUser2 = new TestUser();
        newUser2.currHp = 100;

        Thread[] threads = new Thread[2];

        threads[0] = new Thread(() -> {
            newUser1.attkUser(newUser2);
        });
        threads[1] = new Thread(() -> {
            newUser2.attkUser(newUser1);
        });

        threads[0].start();
        threads[1].start();

        try {
            threads[0].join();
            threads[1].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("执行互砍完成");
    }

    private void test4() {
        TestUser newUser = new TestUser();
        newUser.safeCurrHp = new AtomicInteger(100);
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                newUser.safeCurrHp.addAndGet(-10);
            });
        }

        threads[0].start();
        threads[1].start();

        try {
            threads[0].join();
            threads[1].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (newUser.safeCurrHp.get() != 80) {
            throw new RuntimeException("当前血量错误, currHp = " + newUser.safeCurrHp.get());
        }
        System.out.println("当前血量正确, currHp = " + newUser.safeCurrHp.get());
    }

}
