package genki.furukawa.playground;

import genki.furukawa.playground.task.MyBaseTask;

import java.util.concurrent.*;

/**
 * 最大実行数5のジョブを定期実行する
 */
public class TaskScheduler implements Runnable {
    private static final int MAXIMUM_POOL_SIZE = 5;
    private static final TaskScheduler taskScheduler;
    private static final ExecutorService executorService;
    private static final BlockingDeque<Runnable> taskQueue;
    private static final BlockingQueue<Runnable> waitingTaskQueue;
    private static final ScheduledExecutorService waitingTaskQueueExecutorService;


    static {
        System.out.println(">> static initializer");

        // NOTE: 実行待ちキューを定期チェックするため（run()メソッドを実行するため）に、インスタンスを生成する
        taskScheduler = new TaskScheduler();

        // コアプールサイズ, 最大プールサイズ, Keep-Aliveタイムアウト, 時間の単位, タスクを保持するキュー
        taskQueue = new LinkedBlockingDeque<>();
        executorService = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE, MAXIMUM_POOL_SIZE, 0, TimeUnit.MINUTES, taskQueue);

        // 定期的に実行できるジョブを確認する
        waitingTaskQueue = new LinkedBlockingDeque<>();
        waitingTaskQueueExecutorService = Executors.newSingleThreadScheduledExecutor();
        waitingTaskQueueExecutorService.scheduleWithFixedDelay(taskScheduler,
                1, 1, TimeUnit.SECONDS);

        System.out.println("<< static initializer");
    }

    private TaskScheduler() {
        System.out.println(">> constructor");
        System.out.println("<< constructor");
    }

    public static void addTask(MyBaseTask task) {
        // タスクキューの数がMAXIMUM_POOL_SIZE)であれば実行待ちのタスクキューに入れて処理を終了
        // System.out.println(task.getId() + " タスクキューサイズ: " + taskQueue.size());
        if (taskQueue.size() == MAXIMUM_POOL_SIZE) {
            addWaitingTaskQueue(task);
            return;
        }

        // System.out.println("タスクキューにid: " + task.getId() + "のタスクをsubmitする");
        executorService.submit(task);
    }

    private static void addWaitingTaskQueue(MyBaseTask task) {
         System.out.println("実行待ちタスクキューにid: " + task.getId() + "のタスクをaddする");
        waitingTaskQueue.add(task);
    }

    @Override
    public void run() {
//        System.out.println(">> 実行待ちタスクキュー定期チェック");
//        System.out.println("タスクキューの長さ: " + taskQueue.size());
//        System.out.println("実行待ちタスクキューの長さ: " + waitingTaskQueue.size());

        if (waitingTaskQueue.size() == 0) {
            return;
        }

        if (taskQueue.size() < MAXIMUM_POOL_SIZE) {
            Runnable nextTask = waitingTaskQueue.poll();
            if (nextTask != null) {
                taskQueue.add(nextTask);
            }
        } else {
            // System.out.println("上限数のタスクを実行中のため、何もしない");
        }

//        System.out.println("<< 実行待ちタスクキュー定期チェック");
    }
}
