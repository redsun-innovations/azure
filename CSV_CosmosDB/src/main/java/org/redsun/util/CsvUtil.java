package org.redsun.util;

import org.redsun.model.Hierarchy;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvUtil {

    public static void writeToCsv(List<Hierarchy> hierarchies, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write CSV header
            writer.writeNext(new String[]{"displayName", "base36Id", "pk"});

            // Write data
            for (Hierarchy hierarchy : hierarchies) {
                writer.writeNext(new String[]{
                        hierarchy.getDisplayName(),
                        hierarchy.getBase36Id(),
                        hierarchy.getPk()
                });
            }

            System.out.println("CSV file created successfully at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
