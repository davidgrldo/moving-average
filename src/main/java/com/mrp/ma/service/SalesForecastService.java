package com.mrp.ma.service;

import com.mrp.ma.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SalesForecastService {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    public ForecastingResponse processFile(MultipartFile file, Integer windowSize) {
        List<Integer> years = new ArrayList<>();
        List<Integer> sales = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Tahun")) continue; // Skip header
                String[] values = line.split(";");
                if (values.length == 2) {
                    try {
                        int year = Integer.parseInt(values[0]);
                        int sale = Integer.parseInt(values[1]);
                        years.add(year);
                        sales.add(sale);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if data is read correctly
        if (years.isEmpty() || sales.isEmpty() || years.size() != sales.size()) {
            throw new IllegalArgumentException("CSV data is invalid or empty");
        }

        // Calculate SMA and WMA
        List<Double> sma = calculateSMA(sales, windowSize);
        List<Double> wma = calculateWMA(sales, windowSize);

        sma.add(0, null);
        wma.add(0, null);

        // Calculate MAE and MSE
        double[] smaErrors = calculateErrors(sales, sma, windowSize);
        double[] wmaErrors = calculateErrors(sales, wma, windowSize);

        PerbandinganResponse perbandinganResponse = new PerbandinganResponse(
                "SMA " + windowSize + " Tahun",
                formatDouble(sma.get(sma.size() - 1)),
                smaErrors[0],
                smaErrors[1],
                "WMA " + windowSize + " Tahun",
                formatDouble(wma.get(wma.size() - 1)),
                wmaErrors[0],
                wmaErrors[1]
        );

        List<PerhitunganResponse.DetailPerhitunganResponse> smaCalculations = prepareCalculationResponse(sales, sma, years);
        List<PerhitunganResponse.DetailPerhitunganResponse> wmaCalculations = prepareCalculationResponse(sales, wma, years);

        PerhitunganResponse smaData = new PerhitunganResponse(formatDouble(sma.get(sma.size() - 1)), smaErrors[2], smaErrors[3], smaCalculations);
        PerhitunganResponse wmaData = new PerhitunganResponse(formatDouble(wma.get(wma.size() - 1)), wmaErrors[2], wmaErrors[3], wmaCalculations);

        return new ForecastingResponse(perbandinganResponse, smaData, wmaData);
    }

    private List<Double> calculateSMA(List<Integer> sales, int windowSize) {
        List<Double> sma = new ArrayList<>();
        for (int i = 0; i < sales.size(); i++) {
            if (i < windowSize - 1) {
                sma.add(null);
            } else {
                double sum = 0;
                for (int j = i - windowSize + 1; j <= i; j++) {
                    sum += sales.get(j);
                }
                sma.add(sum / windowSize);
            }
        }
        return sma;
    }

    private List<Double> calculateWMA(List<Integer> sales, int windowSize) {
        List<Double> wma = new ArrayList<>();
        int weightSum = (windowSize * (windowSize + 1)) / 2;
        for (int i = 0; i < sales.size(); i++) {
            if (i < windowSize - 1) {
                wma.add(null);
            } else {
                double sum = 0;
                for (int j = 0; j < windowSize; j++) {
                    sum += sales.get(i - j) * (windowSize - j);
                }
                wma.add(sum / weightSum);
            }
        }
        return wma;
    }

    private double[] calculateErrors(List<Integer> sales, List<Double> forecast, int windowSize) {
        double mae = 0;
        double mse = 0;
        double sumAbsErrors = 0;
        double sumError2 = 0;
        int count = 0;
        for (int i = windowSize; i < sales.size(); i++) {
            if (forecast.get(i) != null) {
                double error = sales.get(i) - forecast.get(i);
                double absError = Math.abs(error);
                double error2 = error * error;

                mae += absError;
                mse += error2;

                sumError2 += error2;
                sumAbsErrors += absError;
                count++;
            }
        }
        mae /= count;
        mse /= count;
        return new double[]{
                formatDouble(mae),
                formatDouble(mse),
                formatDouble(sumAbsErrors),
                formatDouble(sumError2)
        };
    }

    private List<PerhitunganResponse.DetailPerhitunganResponse> prepareCalculationResponse(List<Integer> sales, List<Double> forecast, List<Integer> years) {
        List<PerhitunganResponse.DetailPerhitunganResponse> calculations = new ArrayList<>();
        double totalAbsoluteError = 0.0;

        for (int i = 0; i < sales.size(); i++) {
            double error = (forecast.get(i) != null) ? sales.get(i) - forecast.get(i) : 0;
            double absError = Math.abs(error);

            totalAbsoluteError += absError;

            calculations.add(
                    new PerhitunganResponse.DetailPerhitunganResponse(
                            years.get(i),
                            sales.get(i),
                            (forecast.get(i) != null) ? formatDouble(forecast.get(i)) : 0.00,
                            formatDouble(error),
                            formatDouble(absError),
                            formatDouble(error * error)

                    )
            );
        }
        log.info("Total Absolute Error: {}", totalAbsoluteError);

        return calculations;
    }

    private double formatDouble(double value) {
        return Double.parseDouble(df.format(value));
    }
}