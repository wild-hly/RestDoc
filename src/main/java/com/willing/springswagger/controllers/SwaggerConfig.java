package com.willing.springswagger.controllers;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SwaggerConfig {
    private List<String> _packages = new ArrayList<>();
    private String _fieldPrefix;

    private String _apiTitle;
    private String _apiDescription;
    private String _apiVersion;
}