import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarFolderGenerator {

    private static final int YEAR = 2026;
    private static final Path BASE_DIRECTORY = Paths.get("calendar-" + YEAR);

    // Flags
    private static final boolean EXCLUDE_WEEKENDS = true;
    private static final boolean EXCLUDE_HOLIDAYS = true;

    // Holidays
    private static final LocalDate[] HOLIDAYS = new LocalDate[]{
            LocalDate.of(YEAR, 1, 26),   // 26 January
            LocalDate.of(YEAR, 8, 15),   // 15 August
            LocalDate.of(YEAR, 10, 2),   // 02 October
            LocalDate.of(YEAR, 12, 25)   // 25 December
    };

    public static void main(String[] args) {
        int totalWeekendDays = 0;

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(YEAR, month);
            Path monthFolder = createMonthFolder(yearMonth, month);

            int daysInMonth = yearMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(YEAR, month, day);
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                boolean isWeekend = isWeekend(dayOfWeek);
                if (isWeekend) totalWeekendDays++;

                boolean isHoliday = isHoliday(date);

                boolean skipFile = (EXCLUDE_WEEKENDS && isWeekend) || (EXCLUDE_HOLIDAYS && isHoliday);
                if (!skipFile) {
                    createDayFile(date, monthFolder, dayOfWeek);
                }
            }
        }

        if (EXCLUDE_WEEKENDS && EXCLUDE_HOLIDAYS) {
            generateReport(totalWeekendDays);
        }

        System.out.println("Calendar folders and files for " + YEAR + " created successfully.");
    }

    // --- Helper Methods ---

    private static Path createMonthFolder(YearMonth yearMonth, int month) {
        String monthNumber = String.format("%02d", month);
        String monthNameShort = yearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String folderName = "Month" + monthNumber + "-" + monthNameShort + YEAR;
        Path monthFolder = BASE_DIRECTORY.resolve(folderName);

        try {
            Files.createDirectories(monthFolder);
        } catch (IOException e) {
            System.err.println("Failed to create folder: " + monthFolder);
            e.printStackTrace();
        }
        return monthFolder;
    }

    private static boolean isWeekend(DayOfWeek dayOfWeek) {
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private static boolean isHoliday(LocalDate date) {
        for (LocalDate holiday : HOLIDAYS) {
            if (holiday.equals(date)) {
                return true;
            }
        }
        return false;
    }

    private static void createDayFile(LocalDate date, Path monthFolder, DayOfWeek dayOfWeek) {
        String monthNameShort = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String dayOfWeekName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String fileName = String.format("%02d%s-%s.txt", date.getDayOfMonth(), monthNameShort, dayOfWeekName);
        Path filePath = monthFolder.resolve(fileName);

        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            if (!Files.exists(filePath)) {
                System.err.println("Failed to create file: " + filePath);
                e.printStackTrace();
            }
        }
    }

    private static void generateReport(int totalWeekendDays) {
        // Count holidays that are not on weekends
        int effectiveHolidayCount = 0;
        for (LocalDate holiday : HOLIDAYS) {
            if (!isWeekend(holiday.getDayOfWeek())) {
                effectiveHolidayCount++;
            }
        }

        int freeDays = totalWeekendDays + effectiveHolidayCount;
        int workingDays = isLeapYear(YEAR) ? 366 - freeDays : 365 - freeDays;
        int workingHours = workingDays * 9;
        int freeHours = freeDays * 12;

        Path reportPath = BASE_DIRECTORY.resolve("report.txt");
        String reportContent = "Year " + YEAR + " report\n\n" +
                "Stats:\n" +
                "- Number of working days: " + workingDays + "\n" +
                "- Number of working hours: " + workingHours + "\n" +
                "- Number of free days: " + freeDays + "\n" +
                "- Number of free hours: " + freeHours + "\n";

        try {
            Files.writeString(reportPath, reportContent);
            System.out.println("Report generated at: " + reportPath);
        } catch (IOException e) {
            System.err.println("Failed to write report: " + reportPath);
            e.printStackTrace();
        }
    }

    private static boolean isLeapYear(int year) {
        return java.time.Year.isLeap(year);
    }
}
