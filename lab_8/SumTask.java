import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
    private static final long serialVersionUID = 1L;
    private static final int LIMIT = 20;
    private final int[] numbers;
    private final int start;
    private final int end;

    public SumTask(int[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start < LIMIT) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += numbers[i];
            }
            return sum;
        }

        int middle = start + (end - start) / 2;
        SumTask left = new SumTask(numbers, start, middle);
        SumTask right = new SumTask(numbers, middle, end);

        left.fork();
        long rightSum = right.compute();
        long leftSum = left.join();
        return leftSum + rightSum;
    }
}
