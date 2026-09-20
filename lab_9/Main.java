import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    private static final int ACCOUNTS = 100;
    private static final int THREADS = 3000;
    private static final int TRANSFERS = 100;

    public static void main(String[] args) throws InterruptedException {
        String mode = args.length == 0 ? "all" : args[0];
        if (!mode.equals("bank") && !mode.equals("buffer") && !mode.equals("all")) {
            System.out.println("Usage: java Main [bank|buffer|all]");
            return;
        }
        if (mode.equals("bank") || mode.equals("all")) showBank();
        if (mode.equals("buffer") || mode.equals("all")) showBuffers();
    }

    private static void showBank() throws InterruptedException {
        Account[] accounts = new Account[ACCOUNTS];
        Random random = new Random();
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(1000 + random.nextInt(9001));
        }
        long before = total(accounts);
        Bank bank = new Bank();
        CountDownLatch ready = new CountDownLatch(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[THREADS];
        System.out.println("BANK");
        System.out.println("Accounts: " + ACCOUNTS);
        System.out.println("Threads: " + THREADS);
        System.out.println("Transfers per thread: " + TRANSFERS);
        System.out.println("Total before: " + before);
        try {
            for (int i = 0; i < workers.length; i++) {
                workers[i] = new Thread(null, () -> {
                    ready.countDown();
                    try {
                        start.await();
                        ThreadLocalRandom rnd = ThreadLocalRandom.current();
                        for (int j = 0; j < TRANSFERS; j++) {
                            Account from = accounts[rnd.nextInt(ACCOUNTS)];
                            Account to = accounts[rnd.nextInt(ACCOUNTS)];
                            bank.transfer(from, to, rnd.nextInt(1, 1001));
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        failure.compareAndSet(null, e);
                    } catch (RuntimeException e) {
                        failure.compareAndSet(null, e);
                    }
                }, "transfer-" + i, 256 * 1024L);
                workers[i].start();
            }
            ready.await();
        } finally {
            start.countDown();
        }
        for (Thread worker : workers) worker.join();
        if (failure.get() != null) throw new IllegalStateException("Worker failed", failure.get());
        long after = total(accounts);
        boolean nonNegative = true;
        for (Account account : accounts) {
            if (account.getBalance() < 0) nonNegative = false;
        }
        System.out.println("Completed: " + bank.getCompleted());
        System.out.println("Rejected (insufficient funds): " + bank.getRejected());
        System.out.println("Skipped (same account): " + bank.getSameAccount());
        System.out.println("Total after: " + after);
        System.out.println("Money conserved: " + (before == after));
        System.out.println("No negative balances: " + nonNegative);
        if (before != after || !nonNegative) throw new AssertionError("Bank invariant failed");
    }

    private static long total(Account[] accounts) {
        long sum = 0;
        for (Account account : accounts) sum += account.getBalance();
        return sum;
    }

    private static void showBuffers() throws InterruptedException {
        RingBuffer<String> first = new RingBuffer<>(10);
        RingBuffer<String> second = new RingBuffer<>(10);
        Thread[] workers = new Thread[7];
        for (int i = 0; i < 5; i++) {
            final int id = i + 1;
            workers[i] = new Thread(() -> {
                int number = 1;
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        first.put("Потік № " + id + " згенерував повідомлення " + number++);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "producer-" + id);
        }
        for (int i = 0; i < 2; i++) {
            final int id = i + 6;
            workers[i + 5] = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        String message = first.take();
                        second.put("Потік № " + id + " переклав повідомлення: [" + message + "]");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "translator-" + id);
        }
        for (Thread worker : workers) {
            worker.setDaemon(true);
            worker.start();
        }
        System.out.println("\nBUFFERS: 5 producers, 2 translators, all 7 are daemon threads");
        try {
            for (int i = 1; i <= 100; i++) {
                System.out.println(i + ". " + second.take());
            }
            System.out.println("Printed messages: 100");
        } finally {
            for (Thread worker : workers) worker.interrupt();
            for (Thread worker : workers) worker.join();
        }
    }
}
