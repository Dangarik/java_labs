import java.util.concurrent.atomic.AtomicLong;

public class Account {
    private static final AtomicLong NEXT_ID = new AtomicLong();
    private final long id = NEXT_ID.getAndIncrement();
    private long balance;

    public Account(long balance) {
        if (balance < 0) throw new IllegalArgumentException("Negative balance");
        this.balance = balance;
    }

    public long getId() { return id; }
    public synchronized long getBalance() { return balance; }

    public synchronized void withdraw(int amount) {
        if (amount < 0 || balance < amount) {
            throw new IllegalArgumentException("Invalid withdrawal");
        }
        balance -= amount;
    }

    public synchronized void deposit(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        balance = Math.addExact(balance, amount);
    }
}
