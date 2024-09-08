package course.concurrency.m6_streams;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTask {

  private class LIFOQueue<E> extends LinkedBlockingDeque<E> {
    @Override
    public E take() throws InterruptedException {
      return super.takeLast();
    }

    @Override
    public E poll() {
      return super.pollLast();
    }

    @Override
    public E peek() {
      return super.peekLast();
    }
  }

  // Task #1
  public ThreadPoolExecutor getLifoExecutor() {
    return new ThreadPoolExecutor(
        0, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new LIFOQueue<>());
  }

  // Task #2
  public ThreadPoolExecutor getRejectExecutor() {
    return new ThreadPoolExecutor(
        8,
        8,
        0,
        TimeUnit.MILLISECONDS,
        new SynchronousQueue<>(),
        new ThreadPoolExecutor.DiscardPolicy());
  }
}
