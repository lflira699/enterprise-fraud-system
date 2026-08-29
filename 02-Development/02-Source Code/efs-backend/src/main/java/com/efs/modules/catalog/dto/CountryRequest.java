package com.efs.modules.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CountryRequest {

    @NotBlank
    @Size(min = 2, max = 2)
    private String countryCode;

    @NotBlank
    @Size(min = 3, max = 3)
    private String alpha3Code;

    @Size(min = 3, max = 3)
    private String numericCode;

    @NotBlank
    @Size(max = 150)
    private String countryName;

    @NotBlank
    @Size(max = 20)
    private String status;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}