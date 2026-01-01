import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class Calendar {

    private static int YEAR = 2026;

    // Holidays
    private static final LocalDate[] HOLIDAYS = new LocalDate[]{
            LocalDate.of(YEAR, 1, 1),
            LocalDate.of(YEAR, 1, 26),
            LocalDate.of(YEAR, 8, 15),
            LocalDate.of(YEAR, 10, 2),
            LocalDate.of(YEAR, 12, 25)
    };

    public static final String WORK = "work";
    public static final String TIMEOFF = "timeoff";
    public static final String ALL = "all";

    public static void main(String[] args) {

        if (args.length > 0) {
            try {
                YEAR = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid year argument, using default year " + YEAR);
            }
        }

        // Base directories
        Path WORK_CALENDAR_DIR = Paths.get("work-calendar-" + YEAR);
        Path TIMEOFF_CALENDAR_DIR = Paths.get("timeoff-calendar-" + YEAR);
        Path ALL_CALENDAR_DIR = Paths.get("calendar-" + YEAR);

        System.out.println("Generating work calendar...");
        generateCalendar(WORK_CALENDAR_DIR, WORK);

        System.out.println("Generating time-off calendar...");
        generateCalendar(TIMEOFF_CALENDAR_DIR, TIMEOFF);

        System.out.println("Generating all-days calendar...");
        generateCalendar(ALL_CALENDAR_DIR, ALL);

        System.out.println("All calendars for " + YEAR + " created successfully.");
    }

    /**
     * Generate a calendar in the given base directory.
     *
     * @param baseDir    Path to calendar base directory
     * @param calendarType "work", "timeoff", or "all"
     */
    private static void generateCalendar(Path baseDir, String calendarType) {
        int totalWeekendDays = 0;

        boolean workDaysOnly = WORK.equals(calendarType);
        boolean timeOffOnly = TIMEOFF.equals(calendarType);
        boolean allDays = ALL.equals(calendarType);

        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(YEAR, month);
            Path monthFolder = createMonthFolder(baseDir, yearMonth, month);

            for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                LocalDate date = LocalDate.of(YEAR, month, day);
                DayOfWeek dayOfWeek = date.getDayOfWeek();

                if (isWeekend(dayOfWeek)) totalWeekendDays++;

                boolean isWeekendOrHoliday = isWeekendOrHoliday(dayOfWeek, date);

                boolean include = allDays || (workDaysOnly && !isWeekendOrHoliday) || (timeOffOnly && isWeekendOrHoliday);
                if (include) {
                    createDayFile(date, monthFolder, dayOfWeek);
                }
            }
        }

        if (!allDays) { // Only work and timeoff calendars need reports
            generateReport(baseDir, totalWeekendDays, calendarType);
        }
    }

    // --- Helper Methods ---

    private static Path createMonthFolder(Path baseDir, YearMonth yearMonth, int month) {
        String folderName = String.format("Month%02d-%s%d",
                month,
                yearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                YEAR
        );
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
            if (holiday.equals(date)) return true;
        }
        return false;
    }

    private static boolean isWeekendOrHoliday(DayOfWeek dayOfWeek, LocalDate date) {
        return isWeekend(dayOfWeek) || isHoliday(date);
    }

    private static void createDayFile(LocalDate date, Path monthFolder, DayOfWeek dayOfWeek) {
        String fileName = String.format("%02d%s-%s.txt",
                date.getDayOfMonth(),
                date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        );
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

    private static void generateReport(Path baseDir, int totalWeekendDays, String calendarType) {
        int effectiveHolidayCount = 0;
        for (LocalDate holiday : HOLIDAYS) {
            if (!isWeekend(holiday.getDayOfWeek())) effectiveHolidayCount++;
        }

        int freeDays = totalWeekendDays + effectiveHolidayCount;
        Path reportPath = baseDir.resolve("report.txt");

        String content;
        if (WORK.equals(calendarType)) {
            int workingDays = isLeapYear(YEAR) ? 366 - freeDays : 365 - freeDays;
            int workingHours = workingDays * 9;
            content = String.format("Year %d work report%n%nStats:%n- Number of working days: %d%n- Number of working hours: %d%n",
                    YEAR, workingDays, workingHours);
        } else { // TIMEOFF
            int freeHours = freeDays * 12;
            content = String.format("Year %d timeoff report%n%nStats:%n- Number of free days: %d%n- Number of free hours: %d%n",
                    YEAR, freeDays, freeHours);
        }

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
