package com.bakaneko.finance.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaxReturnResponse {

    private Double totalTax;
    private Double taxRefund;
}
