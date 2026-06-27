package com.carsales.carsales.Service;

import com.carsales.carsales.Entity.CarsDetail;
import com.carsales.carsales.Repository.CarDetailRepository;
import com.opencsv.CSVReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CarDetailLoader implements CommandLineRunner {

    private final CarDetailRepository carDetailRepository;
    private static final int BATCH_SIZE = 100;

    public CarDetailLoader(CarDetailRepository carDetailRepository) {
        this.carDetailRepository = carDetailRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (carDetailRepository.count() == 0) {
            loadCSVData();
        } else {
            System.out.println("Data is Already Present. Skipping the import");
        }
    }

    private void loadCSVData() {
        System.out.println("Loading CSV data....");

        try {
            ClassPathResource loadedCSVFile = new ClassPathResource("carprices.csv");
            System.out.println("CSV file found: " + loadedCSVFile.getFilename());

            try (Reader reader = new InputStreamReader(loadedCSVFile.getInputStream());
                 CSVReader csvReader = new CSVReader(reader)) {

                System.out.println("CSV reader opened, processing rows...");

                String[] row;
                boolean isHeader = true;
                List<CarsDetail> batch = new ArrayList<>();
                int totalSaved = 0;
                int rowNumber = 0;

                while ((row = csvReader.readNext()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    rowNumber++;

                    if (row.length < 16) {
                        System.out.println("Skipping incomplete row " + rowNumber);
                        continue;
                    }

                    try {
                        CarsDetail carsDetail = new CarsDetail(
                                null,
                                parseInteger(row[0], "year", rowNumber),
                                row[1].trim(),
                                row[2].trim(),
                                row[3].trim(),
                                row[4].trim(),
                                row[5].trim(),
                                row[6].trim(),
                                row[7].trim(),
                                parseInteger(row[8], "carCondition", rowNumber),
                                parseInteger(row[9], "odometer", rowNumber),
                                row[10].trim(),
                                row[11].trim(),
                                row[12].trim(),
                                parseInteger(row[13], "mmr", rowNumber),
                                parseFloat(row[14], "sellingPrice", rowNumber)
                        );
                        batch.add(carsDetail);

                    } catch (Exception e) {
                        System.err.println("Error parsing row " + rowNumber + ": " + e.getMessage());
                    }

                    // Save every 500 rows
                    if (batch.size() >= 500) {
                        carDetailRepository.saveAll(batch);
                        totalSaved += batch.size();
                        batch.clear();
                        System.out.println("Saved " + totalSaved + " records so far...");
                    }
                }

                // Save remaining records
                if (!batch.isEmpty()) {
                    carDetailRepository.saveAll(batch);
                    totalSaved += batch.size();
                }

                System.out.println("CSV loading complete. Total saved: " + totalSaved);
            }

        } catch (Exception e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load CSV data", e);
        }
    }

    private Integer parseInteger(String value, String fieldName, int rowNumber) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer in row " + rowNumber +
                    " for field '" + fieldName + "': " + value);
            return null;
        }
    }

    private Float parseFloat(String value, String fieldName, int rowNumber) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid float in row " + rowNumber +
                    " for field '" + fieldName + "': " + value);
            return null;
        }
    }
}