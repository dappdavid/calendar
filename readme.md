# Calendar

This Java program generates three types of calendars for a given year:

1. **Work Calendar** (`work-calendar-<year>`): Contains only workdays i.e weekdays excluding holidays.
2. **Time-Off Calendar** (`timeoff-calendar-<year>`): Contains only weekends and holidays.
3. **All-Days Calendar** (`calendar-<year>`): Contains all days of the year, no exclusions.

Each month will have its own folder, and each day will be a text file named like `01Jan-Thursday.txt`. Work and Time-Off calendars also generate a `report.txt` summarizing the number of working or free days and hours.

---

## What to Edit Before Running

- **Holidays**: In `Calendar.java`, modify the HOLIDAYS array to include the holidays you want:
   ```java
   private static final LocalDate[] HOLIDAYS = new LocalDate[]{
    LocalDate.of(YEAR, 1, 26), // January 26
    LocalDate.of(YEAR, 8, 15), // August 15
    LocalDate.of(YEAR, 10, 2), // October 2
    LocalDate.of(YEAR, 12, 25) // December 25
   };
   ```
## How to Run

1. After editing the above points, save the `Calendar.java` file in a folder.
2. Open **Command Prompt** (Windows) or **Terminal** (Linux/Mac).
3. Navigate to the folder containing `Calendar.java`.
4. Compile the Java class:

   ```sh
   javac Calendar.java
    ```
5. Run the class with the **desired year** as an argument (if no year is provided, it defaults to 2026):
    ```sh
    java Calendar 2026
    ```
6. The program will create the calendar folders in the same directory:
   - `work-calendar-2026`
   - `timeoff-calendar-2026`
   - `calendar-2026`


