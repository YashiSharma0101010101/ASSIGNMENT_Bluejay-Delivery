package com.ASSIGNMENT;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Employee {
    public static void main(String[] args) throws CsvValidationException {
        
        String filePath = "C:/Users/yashi/Downloads/Assignment_Timecard.xlsx - Sheet1.csv";
                try {
                    // Create a CSVReader to read the CSV file
                    CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build();

                    // Initialize data structures to store employee information
                    Map<String, Integer> consecutiveDaysCount = new HashMap<>();
                    Map<String, Date> lastShiftEndTime = new HashMap<>();

                    String[] record;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a"); 

                    while ((record = csvReader.readNext()) != null) {
                        String name = record[0];
                        String position = record[1];
                        String startTimeStr = record[2];
                        String endTimeStr = record[3];

                        // Check if the date strings are not empty
                        if (!startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
                            Date startTime = dateFormat.parse(startTimeStr);
                            Date endTime = dateFormat.parse(endTimeStr);

                            // Check consecutive days
                            int consecutiveDays = consecutiveDaysCount.getOrDefault(name, 0);
                            Date lastEndTime = lastShiftEndTime.get(name);

                            if (lastEndTime != null && isNextDay(lastEndTime, startTime)) {
                                consecutiveDays++;
                            } else {
                                consecutiveDays = 1;
                            }

                            consecutiveDaysCount.put(name, consecutiveDays);

                            // Check less than 10 hours between shifts
                            if (lastEndTime != null && hoursBetween(lastEndTime, startTime) < 10 && hoursBetween(lastEndTime, startTime) > 1) {
                                System.out.println("Employee: " + name + ", Position: " + position + " - Less than 10 hours between shifts");
                            }

                            // Check more than 14 hours in a single shift
                            int shiftDuration = hoursBetween(startTime, endTime);
                            if (shiftDuration > 14) {
                                System.out.println("Employee: " + name + ", Position: " + position + " - Worked more than 14 hours in a single shift");
                            }

                            // Update last shift end time
                            lastShiftEndTime.put(name, endTime);
                        } else {
                            // Handle the case where date strings are empty
                            System.out.println("Empty date strings for Employee: " + name);
                        }
                    }

                    // Checking for consecutive days
                    for (Map.Entry<String, Integer> entry : consecutiveDaysCount.entrySet()) {
                        if (entry.getValue() >= 7) {
                            System.out.println("Employee: " + entry.getKey() + " - Worked for 7 consecutive days");
                        }
                    }

                    // Closing the CSV reader
                    csvReader.close();

                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }

            private static boolean isNextDay(Date d1, Date d2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return !dateFormat.format(d1).equals(dateFormat.format(d2));
            }

            private static int hoursBetween(Date date1, Date date2) {
                long ms = date2.getTime() - date1.getTime();
                return (int) (ms / (1000 * 60 * 60));
            }
        }
