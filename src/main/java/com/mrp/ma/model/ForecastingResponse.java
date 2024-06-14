package com.mrp.ma.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ForecastingResponse {

    private PerbandinganResponse perbandingan;
    private PerhitunganResponse perhitunganSma;
    private PerhitunganResponse perhitunganWma;
}
