package com.mrp.ma.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PerbandinganResponse {

    private String metodeSma;
    private double hasilPrediksiSma;
    private double maeSma;
    private double mseSma;
    private String metodeWma;
    private double hasilPrediksiWma;
    private double maeWma;
    private double mseWma;
}
