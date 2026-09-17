import java.time.LocalDate;

public class Tests {
    static int checks;
    static void check(boolean value) {
        checks++;
        if (!value) throw new AssertionError("Перевірка " + checks);
    }
    static void bad(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException e) { checks++; return; }
        throw new AssertionError("Очікувався виняток");
    }
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2026, 1, 31);
        Main.CardSystem s = new Main.CardSystem();
        Main.Turnstile t = new Main.Turnstile(s, 1500);
        for (Main.CardType type : Main.CardType.values()) {
            Main.Card month = s.issue(type, Main.Plan.MONTH, date);
            check(month.validUntil.equals(LocalDate.of(2026, 2, 27)));
            check(t.pass(month, month.validUntil));
            check(!t.pass(month, month.validUntil.plusDays(1)));
            Main.Card days = s.issue(type, Main.Plan.TEN_DAYS, date);
            check(days.validUntil.equals(date.plusDays(9)));
            check(!t.pass(days, date.minusDays(1)));
            check(t.pass(days, date.plusDays(9)));
            check(!t.pass(days, date.plusDays(10)));
            for (Main.Plan plan : new Main.Plan[]{Main.Plan.FIVE_RIDES, Main.Plan.TEN_RIDES}) {
                Main.Card rides = s.issue(type, plan, date);
                int count = rides.rides;
                for (int i = 0; i < count; i++) check(t.pass(rides, date));
                check(!t.pass(rides, date));
                check(rides.rides == 0);
            }
        }
        bad(() -> s.issue(Main.CardType.PUPIL, Main.Plan.BALANCE, date));
        bad(() -> s.issue(Main.CardType.STUDENT, Main.Plan.BALANCE, date));
        Main.Card balance = s.issue(Main.CardType.REGULAR, Main.Plan.BALANCE, date);
        check(!t.pass(balance, date));
        s.topUp(balance.id, 1500);
        check(t.pass(balance, date.plusYears(20)));
        check(balance.balance == 0);
        check(!t.pass(balance, date.plusYears(20)));
        s.topUp(balance.id, 3000);
        balance.readable = false;
        check(!t.pass(balance, date)); check(balance.balance == 3000);
        balance.readable = true;
        Main.CardSystem other = new Main.CardSystem();
        check(!t.pass(other.issue(Main.CardType.REGULAR, Main.Plan.MONTH, date), date));
        check(!t.pass(null, date));
        bad(() -> s.topUp(balance.id, 0));
        bad(() -> s.topUp(balance.id, -100));
        bad(() -> s.topUp(9999, 100));
        bad(() -> s.topUp(1, 100));
        check(s.find(balance.id) == balance);
        check(s.find(9999) == null);
    }
}
