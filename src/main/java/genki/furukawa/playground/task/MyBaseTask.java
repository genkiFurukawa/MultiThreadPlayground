package genki.furukawa.playground.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MyBaseTask implements Runnable, ThreadFactory {
    private int id;

    public MyBaseTask(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        System.out.println("[START] id: " + id + "のタスクを実行");
//        System.out.println(Thread.currentThread().getName()); // スレッド名を表示
        sleep(id);
        System.out.println("[END] id: " + id + "のタスクを実行");
    }

    /**
     * i秒間、処理を止める
     *
     * @param i 止めたい秒数
     */
    private void sleep(int i) {
        try {
//            TimeUnit.SECONDS.sleep(i);
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = "[id: " + this.id + "]";
        return new Thread(null, r, name);
    }
}
