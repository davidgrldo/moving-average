package com.mrp.ma.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PerhitunganResponse {

    private double totalForecast;
    private double totalAbsolutError;
    private double totalError2;
    private List<DetailPerhitunganResponse> detail;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailPerhitunganResponse {

        private int tahun;
        private int sales;
        private Double forecasting;
        private double error;
        private double absoluteError;
        private double error2;
    }
}
