package uk.ac.core.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;

/**
 *
 * @author mc26486
 */
public abstract class AsyncQueueWorker extends QueueWorker {

    private static final int THREADPOOL_SIZE = 3;

    protected List<TaskItemStatus> taskItemStatuses = new ArrayList<TaskItemStatus>();

    @Override
    public abstract List<TaskItem> collectData();

    private void onTaskItemCompleted(TaskItemStatus taskItemStatus) {
        workerStatus.getTaskStatus().incProcessed();
        if (taskItemStatus.isSuccess()) {
            workerStatus.getTaskStatus().incSuccessful();
        }
        /**
         * Step 3ex: handling of start, pause, stop
         */
        if (this.getPause().equals(Boolean.TRUE)) {
            waitOnPause();
        }
        if (this.getStop().equals(Boolean.TRUE)) {
            //TODO
        }
    }

    @Override
    public abstract void collectStatistics(List<TaskItemStatus> results);

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {

        ExecutorService executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
        CompletionService<Object> completionService = new ExecutorCompletionService<Object>(executorService);

        for (TaskItem item : taskItems) {
            workerStatus.getTaskStatus().incProcessed();
            //completionService.submit(new CrawlingSingleDocumentDownloadWorker())
            Object runner = this.getSingleItemWorker(item);
            if (runner instanceof Runnable) {
                completionService.submit((Runnable) this.getSingleItemWorker(item), new TaskItemStatus());

            } else {
                completionService.submit((Callable) this.getSingleItemWorker(item));
            }

        }
        for (TaskItem item : taskItems) {
            try {
                completionService.take().get(); // find the first completed task

                /**
                 * Step 3ex: handling of start, pause, stop
                 */
                if (this.getPause().equals(Boolean.TRUE)) {
                    //TODO: how you implement pause here?
                }
                if (this.getStop().equals(Boolean.TRUE)) {
                    executorService.shutdown();
                }
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        System.out.println("this.taskItemStatuses = " + this.taskItemStatuses);
        return this.taskItemStatuses;

    }

    public abstract Object getSingleItemWorker(TaskItem item);

}
