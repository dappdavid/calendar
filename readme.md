# Calendar Folder Generator

This Java program generates three types of calendar folders for a given year:

1. **Work Calendar** (`work-calendar-<year>`): Contains only workdays (weekdays excluding holidays).
2. **Time-Off Calendar** (`timeoff-calendar-<year>`): Contains only weekends and holidays.
3. **All-Days Calendar** (`calendar-<year>`): Contains all days of the year, no exclusions.

Each month will have its own folder, and each day will be a text file named like `01Jan-Thursday.txt`. Work and Time-Off calendars also generate a `report.txt` summarizing the number of working or free days and hours.

---

## What to Edit Before Running

1. Year: Change the YEAR constant if you want a calendar for a different year:
   ```java
   private static final int YEAR = 2026; // Change to desired year
   ```
2. Holidays: Modify the HOLIDAYS array to include the holidays you want:
   ```java
   private static final LocalDate[] HOLIDAYS = new LocalDate[]{
    LocalDate.of(YEAR, 1, 26), // January 26
    LocalDate.of(YEAR, 8, 15), // August 15
    LocalDate.of(YEAR, 10, 2), // October 2
    LocalDate.of(YEAR, 12, 25) // December 25
   };
   ```
## How to Run

1. Save the `CalendarFolderGenerator.java` file in a folder.
2. Open **Command Prompt** (Windows) or **Terminal** (Linux/Mac).
3. Navigate to the folder containing `CalendarFolderGenerator.java`.
4. Compile the Java class:

   ```sh
   javac CalendarFolderGenerator.java
    ```
5. Run the class
    ```sh
    java CalendarFolderGenerator
    ```
6. The program will create the calendar folders in the same directory:
   - `work-calendar-<year>`
   - `timeoff-calendar-<year>`
   - `calendar-<year>`


