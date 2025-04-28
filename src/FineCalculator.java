package src;

import java.sql.Date;
import java.time.temporal.ChronoUnit;

public class FineCalculator {
    private static final double FINE_PER_DAY = 1.0; // 1 currency unit per day late

    public static double calculateFine(Date dueDate, Date returnDate) {
        if (returnDate == null || dueDate == null) {
            return 0.0;
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate.toLocalDate(), returnDate.toLocalDate());
        if (daysLate > 0) {
            return daysLate * FINE_PER_DAY;
        }
        return 0.0;
    }
}
