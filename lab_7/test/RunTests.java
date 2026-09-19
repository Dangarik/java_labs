import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class RunTests {
    public static void main(String[] args) {
        Class<?>[] suites = {
            workshop.task_1_1.AbbrivationBuilderTest.class,
            workshop.task_1_2.FactorialTest.class,
            workshop.task_2_1.DeveloperServiceTest.class,
            workshop.task_2_2.Exercise1SuppliersTest.class,
            workshop.task_2_2.Exercise2OrdersTest.class,
            workshop.task_3_1.BestPriceFinderTest.class,
            workshop.ExtraChecksTest.class
        };
        String[] labels = {"Task 1.1 - abbreviation", "Task 1.2 - factorial",
            "Task 2.1 - developer languages", "Task 2.2 - suppliers",
            "Task 2.2 - orders", "Task 3.1 - parallel prices", "Extra checks"};
        int total = 0, failures = 0;
        for (int i = 0; i < suites.length; i++) {
            System.out.println("\n" + labels[i]);
            JUnitCore core = new JUnitCore();
            core.addListener(new RunListener() {
                private boolean failed;
                @Override public void testStarted(Description description) { failed = false; }
                @Override public void testFailure(Failure failure) {
                    failed = true;
                    System.out.println("FAIL: " + failure);
                    System.out.println(failure.getTrace());
                }
                @Override public void testFinished(Description description) {
                    if (!failed) System.out.println("PASS: " + description.getMethodName());
                }
            });
            Result result = core.run(suites[i]);
            total += result.getRunCount();
            failures += result.getFailureCount();
            System.out.println("Tests: " + result.getRunCount() + ", failures: " + result.getFailureCount());
        }
        System.out.println("\nTOTAL: " + total + " tests, " + failures + " failures");
        if (failures != 0) System.exit(1);
    }
}
