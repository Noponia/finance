package com.bakaneko.finance.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaxReturnRequest {

    private Double grossIncome;
    private Double taxPaid;
    private String year;
}
