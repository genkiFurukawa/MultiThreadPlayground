package genki.furukawa;

import genki.furukawa.playground.TaskScheduler;
import genki.furukawa.playground.task.MyBaseTask;

public class Main {
    public static void main(String[] args) {
        for (int i = 1; i < 21; i++) {
            TaskScheduler.addTask(new MyBaseTask(i));
        }
    }
}