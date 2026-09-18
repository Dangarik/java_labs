import java.time.LocalDate;
import java.io.Serializable;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Tram {
    enum CardType {
        PUPIL("Учнівська"), STUDENT("Студентська"), REGULAR("Звичайна");
        final String title;
        CardType(String title) { this.title = title; }
    }

    enum Plan {
        MONTH("На місяць"), TEN_DAYS("На 10 днів"),
        FIVE_RIDES("На 5 поїздок"), TEN_RIDES("На 10 поїздок"),
        BALANCE("Накопичувальна");
        final String title;
        Plan(String title) { this.title = title; }
    }

    static class Card implements Serializable {
        private static final long serialVersionUID = 1L;
        final int id;
        final CardType type;
        final Plan plan;
        final LocalDate validFrom, validUntil;
        int rides;
        long balance;
        boolean readable = true;

        Card(int id, CardType type, Plan plan, LocalDate today) {
            this.id = id;
            this.type = type;
            this.plan = plan;
            validFrom = today;
            if (plan == Plan.MONTH) validUntil = today.plusMonths(1).minusDays(1);
            else if (plan == Plan.TEN_DAYS) validUntil = today.plusDays(9);
            else validUntil = null;
            if (plan == Plan.FIVE_RIDES) rides = 5;
            else if (plan == Plan.TEN_RIDES) rides = 10;
        }

        public String toString() {
            String details;
            if (validUntil != null) details = "діє " + validFrom + " — " + validUntil;
            else if (plan == Plan.BALANCE) details = "баланс " + money(balance) + " грн";
            else details = "залишок поїздок: " + rides;
            return "ID=" + id + ", " + type.title + ", " + plan.title + ", " + details;
        }
    }

    static class CardSystem implements Serializable {
        private static final long serialVersionUID = 1L;
        private final ArrayList<Card> cards = new ArrayList<>();
        private int nextId = 1;

        Card issue(CardType type, Plan plan, LocalDate today) {
            if (type == null || plan == null || today == null)
                throw new IllegalArgumentException("Не задано параметри картки");
            if (plan == Plan.BALANCE && type != CardType.REGULAR)
                throw new IllegalArgumentException("Накопичувальна картка може бути лише звичайною");
            Card card = new Card(nextId++, type, plan, today);
            cards.add(card);
            return card;
        }

        Card find(int id) {
            for (Card card : cards) if (card.id == id) return card;
            return null;
        }

        boolean registered(Card card) {
            return card != null && find(card.id) == card;
        }

        void topUp(int id, long amount) {
            Card card = find(id);
            if (card == null) throw new IllegalArgumentException("Картку не знайдено");
            if (card.plan != Plan.BALANCE)
                throw new IllegalArgumentException("Поповнювати можна лише накопичувальну картку");
            if (amount <= 0) throw new IllegalArgumentException("Сума має бути додатною");
            card.balance = Math.addExact(card.balance, amount);
        }

        void showCards() {
            if (cards.isEmpty()) System.out.println("Реєстр порожній");
            for (Card card : cards) System.out.println(card);
        }
    }

    static class Turnstile {
        private final CardSystem system;
        private final long fare;
        private int allowed, denied;
        private final int[][] byType = new int[CardType.values().length][2];
        private final int[][] byPlan = new int[Plan.values().length][2];
        private int unknownDenied;

        Turnstile(CardSystem system, long fare) {
            if (system == null || fare <= 0)
                throw new IllegalArgumentException("Некоректні параметри турнікета");
            this.system = system;
            this.fare = fare;
        }

        boolean pass(Card card, LocalDate today) {
            if (today == null) throw new IllegalArgumentException("Не задано дату");
            if (card == null || !card.readable)
                return record(null, false, "Не вдалося зчитати картку");
            if (!system.registered(card))
                return record(card, false, "Картку не зареєстровано");
            if (today.isBefore(card.validFrom))
                return record(card, false, "Картка ще не діє");
            if (card.validUntil != null && today.isAfter(card.validUntil))
                return record(card, false, "Картка прострочена");
            if (card.plan == Plan.FIVE_RIDES || card.plan == Plan.TEN_RIDES) {
                if (card.rides == 0) return record(card, false, "Поїздки закінчилися");
                card.rides--;
            } else if (card.plan == Plan.BALANCE) {
                if (card.balance < fare) return record(card, false, "Недостатньо коштів");
                card.balance -= fare;
            }
            return record(card, true, "Прохід дозволено");
        }

        private boolean record(Card card, boolean success, String reason) {
            int column = success ? 0 : 1;
            if (success) allowed++; else denied++;
            if (card == null) unknownDenied++;
            else {
                byType[card.type.ordinal()][column]++;
                byPlan[card.plan.ordinal()][column]++;
            }
            System.out.println((card == null ? "Картка невідома" : "ID=" + card.id)
                    + ": " + (success ? "ДОЗВІЛ" : "ВІДМОВА") + " — " + reason);
            return success;
        }

        void showTotals() {
            System.out.println("Усього спроб: " + (allowed + denied));
            System.out.println("Дозволено: " + allowed + ", відмовлено: " + denied);
        }

        void showDetails() {
            System.out.println("За категоріями карток (дозволи / відмови):");
            for (CardType type : CardType.values())
                System.out.println(type.title + ": " + byType[type.ordinal()][0]
                        + " / " + byType[type.ordinal()][1]);
            System.out.println("За тарифами (дозволи / відмови):");
            for (Plan plan : Plan.values())
                System.out.println(plan.title + ": " + byPlan[plan.ordinal()][0]
                        + " / " + byPlan[plan.ordinal()][1]);
            System.out.println("Невідомий тип, відмови: " + unknownDenied);
        }
    }

    static String money(long cents) {
        return (cents / 100) + "." + String.format("%02d", cents % 100);
    }

    static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Введіть ціле число від " + min + " до " + max);
        }
    }

    static void demo() {
        LocalDate today = LocalDate.of(2026, 9, 16);
        CardSystem system = new CardSystem();
        Turnstile gate = new Turnstile(system, 1500);
        Card pupil = system.issue(CardType.PUPIL, Plan.TEN_DAYS, today);
        Card student = system.issue(CardType.STUDENT, Plan.FIVE_RIDES, today);
        Card regular = system.issue(CardType.REGULAR, Plan.BALANCE, today);
        Card expired = system.issue(CardType.REGULAR, Plan.MONTH, today.minusMonths(2));
        system.topUp(regular.id, 3000);
        System.out.println("РЕЄСТР КАРТОК"); system.showCards();
        System.out.println("\nПЕРЕВІРКА ПРОХОДІВ, вартість проїзду 15.00 грн");
        gate.pass(pupil, today);
        for (int i = 0; i < 6; i++) gate.pass(student, today);
        for (int i = 0; i < 3; i++) gate.pass(regular, today);
        gate.pass(expired, today);
        pupil.readable = false; gate.pass(pupil, today); pupil.readable = true;
        System.out.println("\nПІСЛЯ ПРОХОДІВ"); system.showCards();
        System.out.println("\nПІДСУМКОВА СТАТИСТИКА"); gate.showTotals(); gate.showDetails();
        System.out.println("\nПЕРЕВІРКА ОБМЕЖЕННЯ ТИПУ");
        try { system.issue(CardType.STUDENT, Plan.BALANCE, today); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) { demo(); return; }
        run(new Scanner(System.in));
    }

    static void run(Scanner scanner) {
        CardSystem system = new CardSystem();
        Turnstile gate = new Turnstile(system, 1500);
        while (true) {
            System.out.println("\n1 — Випустити картку\n2 — Поповнити\n3 — Пройти"
                    + "\n4 — Реєстр\n5 — Загальна статистика\n6 — Детальна статистика"
                    + "\n7 — Змінити можливість зчитування\n8 — Зберегти реєстр"
                    + "\n9 — Прочитати реєстр\n10 — Пошук картки за ID\n0 — Вихід");
            int command = readInt(scanner, "Ваш вибір: ", 0, 10);
            if (command == 0) break;
            try {
                if (command == 8 || command == 9) {
                    System.out.print("Повний шлях та ім'я файлу: ");
                    String path = scanner.nextLine().trim();
                    if (path.isEmpty()) throw new IllegalArgumentException("Шлях порожній");
                    if (command == 8) {
                        FileStorage.save(Paths.get(path), system);
                        System.out.println("Реєстр збережено");
                    } else {
                        CardSystem loaded = FileStorage.load(Paths.get(path), CardSystem.class);
                        system = loaded;
                        gate = new Turnstile(system, 1500);
                        System.out.println("Реєстр прочитано. Статистику нового турнікета обнулено.");
                        system.showCards();
                    }
                } else if (command == 10) {
                    int id = readInt(scanner, "ID для пошуку: ", 1, Integer.MAX_VALUE);
                    Card found = system.find(id);
                    System.out.println(found == null ? "Картку не знайдено" : found);
                } else if (command == 1) {
                    int type = readInt(scanner, "Тип: 1 учнівська, 2 студентська, 3 звичайна: ", 1, 3);
                    int plan = readInt(scanner, "Тариф: 1 місяць, 2 десять днів, 3 п'ять поїздок,"
                            + " 4 десять поїздок, 5 накопичувальна: ", 1, 5);
                    System.out.println(system.issue(CardType.values()[type - 1],
                            Plan.values()[plan - 1], LocalDate.now()));
                } else if (command == 4) system.showCards();
                else if (command == 5) gate.showTotals();
                else if (command == 6) gate.showDetails();
                else {
                    int id = readInt(scanner, "ID картки: ", 1, Integer.MAX_VALUE);
                    if (command == 2) {
                        int amount = readInt(scanner, "Сума поповнення у цілих гривнях: ", 1, 1000000);
                        system.topUp(id, amount * 100L);
                        System.out.println(system.find(id));
                    } else if (command == 3) gate.pass(system.find(id), LocalDate.now());
                    else {
                        Card card = system.find(id);
                        if (card == null) throw new IllegalArgumentException("Картку не знайдено");
                        card.readable = !card.readable;
                        System.out.println("Зчитування: " + (card.readable ? "працює" : "не працює"));
                    }
                }
            } catch (IllegalArgumentException | ArithmeticException | IOException | ClassNotFoundException e) {
                System.out.println("Помилка: " + e.getMessage());
            }
        }

    }
}
