import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Bank {
    private final AtomicInteger completed = new AtomicInteger();
    private final AtomicInteger rejected = new AtomicInteger();
    private final AtomicInteger sameAccount = new AtomicInteger();

    public void transfer(Account from, Account to, int amount) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (from == to) {
            sameAccount.incrementAndGet();
            return;
        }

        Account first = from.getId() < to.getId() ? from : to;
        Account second = first == from ? to : from;
        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() < amount) {
                    rejected.incrementAndGet();
                    return;
                }
                Math.addExact(to.getBalance(), amount);
                from.withdraw(amount);
                to.deposit(amount);
                completed.incrementAndGet();
            }
        }
    }

    public int getCompleted() { return completed.get(); }
    public int getRejected() { return rejected.get(); }
    public int getSameAccount() { return sameAccount.get(); }
}
