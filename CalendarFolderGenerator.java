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

    // Base directories
    private static final Path WORK_CALENDAR_DIR = Paths.get("work-calendar-" + YEAR);
    private static final Path TIMEOFF_CALENDAR_DIR = Paths.get("timeoff-calendar-" + YEAR);

    // Holidays
    private static final LocalDate[] HOLIDAYS = new LocalDate[]{
            LocalDate.of(YEAR, 1, 26),   // 26 January
            LocalDate.of(YEAR, 8, 15),   // 15 August
            LocalDate.of(YEAR, 10, 2),   // 02 October
            LocalDate.of(YEAR, 12, 25)   // 25 December
    };
    public static final String WORK = "work";
    public static final String TIMEOFF = "timeoff";

    public static void main(String[] args) {
        System.out.println("Generating work calendar...");
        generateCalendar(WORK_CALENDAR_DIR, true, WORK);

        System.out.println("Generating time-off calendar...");
        generateCalendar(TIMEOFF_CALENDAR_DIR, false, TIMEOFF);

        System.out.println("All calendars for " + YEAR + " created successfully.");
    }

    /**
     * Generate a calendar in the given base directory.
     *
     * @param baseDir       Path to calendar base directory
     * @param workDaysOnly  if true, include only workdays; if false, include only weekends + holidays
     * @param reportType    "work" or "timeoff" (used for report title and metrics)
     */
    private static void generateCalendar(Path baseDir, boolean workDaysOnly, String reportType) {
        int totalWeekendDays = 0;

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(YEAR, month);
            Path monthFolder = createMonthFolder(baseDir, yearMonth, month);

            int daysInMonth = yearMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = LocalDate.of(YEAR, month, day);
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                boolean isWeekend = isWeekend(dayOfWeek);
                if (isWeekend) totalWeekendDays++;

                boolean isHoliday = isHoliday(date);

                boolean includeInCalendar = workDaysOnly ? !(isWeekend || isHoliday) : (isWeekend || isHoliday);

                if (includeInCalendar) {
                    createDayFile(date, monthFolder, dayOfWeek);
                }
            }
        }

        // Generate report for this calendar
        generateReport(baseDir, totalWeekendDays, reportType);
    }

    // --- Helper Methods ---

    private static Path createMonthFolder(Path baseDir, YearMonth yearMonth, int month) {
        String monthNumber = String.format("%02d", month);
        String monthNameShort = yearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String folderName = "Month" + monthNumber + "-" + monthNameShort + YEAR;
        Path monthFolder = baseDir.resolve(folderName);

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

    private static void generateReport(Path baseDir, int totalWeekendDays, String reportType) {
        // Count holidays that are not on weekends
        int effectiveHolidayCount = 0;
        for (LocalDate holiday : HOLIDAYS) {
            if (!isWeekend(holiday.getDayOfWeek())) {
                effectiveHolidayCount++;
            }
        }

        if (reportType.equals(WORK)) {
            int freeDays = totalWeekendDays + effectiveHolidayCount;
            int workingDays = isLeapYear(YEAR) ? 366 - freeDays : 365 - freeDays;
            int workingHours = workingDays * 9;

            Path reportPath = baseDir.resolve("report.txt");
            String reportContent = "Year " + YEAR + " work report\n\n" +
                    "Stats:\n" +
                    "- Number of working days: " + workingDays + "\n" +
                    "- Number of working hours: " + workingHours + "\n";

            writeReport(reportPath, reportContent);
        } else if (reportType.equals(TIMEOFF)) {
            int freeDays = totalWeekendDays + effectiveHolidayCount;
            int freeHours = freeDays * 12;

            Path reportPath = baseDir.resolve("report.txt");
            String reportContent = "Year " + YEAR + " timeoff report\n\n" +
                    "Stats:\n" +
                    "- Number of free days: " + freeDays + "\n" +
                    "- Number of free hours: " + freeHours + "\n";

            writeReport(reportPath, reportContent);
        }
    }

    private static void writeReport(Path reportPath, String content) {
        try {
            Files.writeString(reportPath, content);
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
