package com.mrp.ma.controller;

import com.mrp.ma.model.ForecastingResponse;
import com.mrp.ma.service.SalesForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SalesForecastController {

    private final SalesForecastService salesForecastService;

    @PostMapping("/upload")
    public ForecastingResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "period", defaultValue = "4") Integer windowSize) {
        return salesForecastService.processFile(file, windowSize);
    }
}