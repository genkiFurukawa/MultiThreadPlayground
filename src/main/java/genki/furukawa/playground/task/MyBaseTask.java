package genki.furukawa.playground.task;

import genki.furukawa.playground.TaskScheduler;

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
        sleep(2);
        TaskScheduler.completeTask(id);
    }

    /**
     * seconds秒間、処理を止める
     *
     * @param seconds 止めたい秒数
     */
    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
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
