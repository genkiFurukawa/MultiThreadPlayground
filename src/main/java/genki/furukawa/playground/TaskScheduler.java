package genki.furukawa.playground;

import genki.furukawa.playground.task.MyBaseTask;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 最大実行数5のジョブを定期実行する
 */
public class TaskScheduler implements Runnable {
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final ThreadPoolExecutor executorService;
    private static final BlockingDeque<Runnable> taskQueue = new LinkedBlockingDeque<>();
    private static final BlockingQueue<Runnable> waitingTaskQueue = new LinkedBlockingDeque<>();

    // 実行中のタスクを管理する
    private static Set<Integer> runningTaskSet = Collections.synchronizedSet(new HashSet<>());

    static {
        // コアプールサイズ, 最大プールサイズ, Keep-Aliveタイムアウト, 時間の単位, タスクを保持するキュー
        executorService = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE, MAXIMUM_POOL_SIZE, 0, TimeUnit.MINUTES, taskQueue);

        // 定期的に実行できるジョブを確認する
        TaskScheduler taskScheduler = new TaskScheduler();
        ScheduledExecutorService waitingTaskQueueExecutorService = Executors.newSingleThreadScheduledExecutor();
        waitingTaskQueueExecutorService.scheduleWithFixedDelay(taskScheduler, 1, 1, TimeUnit.SECONDS);
    }

    private TaskScheduler() {
    }

    public static void addTask(MyBaseTask task) {
        // タスクキューの数がMAXIMUM_POOL_SIZE)であれば実行待ちのタスクキューに入れて処理を終了
        if (runningTaskSet.size() == MAXIMUM_POOL_SIZE) {
            addWaitingTaskQueue(task);
            return;
        }

        runningTaskSet.add(task.getId());
        executorService.submit(task);
        // System.out.println("taskId: " + task.getId() + " 実行中のジョブの個数: " + runningTaskSet.size());
    }

    /**
     * ジョブの終了を通知する
     *
     * @param id 終了したジョブ
     */
    public static void completeTask(int id) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("[" + now + "] id: " + id + "のタスク完了 " +
                ", 待ちのタスクのサイズ: " + waitingTaskQueue.size() +
                ", 実行中のジョブの個数: " + runningTaskSet.size()
        );

        runningTaskSet.remove(id);
    }

    private static void addWaitingTaskQueue(MyBaseTask task) {
        // System.out.println("実行待ちタスクキューにid: " + task.getId() + "のタスクをaddする");
        waitingTaskQueue.add(task);
    }

    @Override
    public void run() {
        // タスクの実行のexecutorServiceがシャットダウンされていない間は無限ループして実行するジョブをチェックする
        while (!executorService.isShutdown()) {
            if (waitingTaskQueue.size() == 0) {
                return;
            }

            if (runningTaskSet.size() < MAXIMUM_POOL_SIZE) {
                Runnable nextTask = waitingTaskQueue.poll();
                if (nextTask != null) {
                    int taskId = ((MyBaseTask) nextTask).getId();
                    runningTaskSet.add(taskId);
                    executorService.submit(nextTask);
                    // System.out.println("taskId: " + taskId + " 実行中のジョブの個数: " + runningTaskSet.size());
                }
            }
        }
    }
}
