package com.mrp.ma.controller;

import com.mrp.ma.model.ForecastingResponse;
import com.mrp.ma.service.SalesForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalesForecastController {

    private final SalesForecastService salesForecastService;

    @PostMapping("/upload")
    public ForecastingResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("period") Integer windowSize) {
        return salesForecastService.processFile(file, windowSize);
    }
}