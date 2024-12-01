package com.bakaneko.finance.controller;

import com.bakaneko.finance.config.YamlConfig;
import com.bakaneko.finance.config.YamlConfig.Bracket;
import com.bakaneko.finance.config.YamlConfig.TaxData;
import com.bakaneko.finance.model.TaxReturnRequest;
import com.bakaneko.finance.model.TaxReturnResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TaxController {

    private final YamlConfig config;

    @Autowired
    public TaxController(YamlConfig config) {
        this.config = config;
    }

    @PostMapping("/returns")
    public ResponseEntity<TaxReturnResponse> taxReturn(@RequestBody TaxReturnRequest req) {
        Optional<TaxData> taxData = config.getTaxData().stream()
                .filter(data -> data.getYear().equals(req.getYear()))
                .findFirst();

        if (taxData.isPresent()) {
            Bracket ballerBracket = taxData.get().getBaller();
            List<Bracket> brackets = taxData.get().getBrackets();

            if (req.getGrossIncome() >= ballerBracket.getIncome().getLower()) {
                Double taxOwed = calculateTaxTotal(req.getGrossIncome(), ballerBracket);
                return ResponseEntity.ok(createTaxReturnResponse(req, taxOwed));
            }

            for (Bracket bracket : brackets) {
                if (bracket.getIncome().getLower() <= req.getGrossIncome() &&
                        bracket.getIncome().getUpper() >= req.getGrossIncome()) {
                    Double taxOwed = calculateTaxTotal(req.getGrossIncome(), bracket);
                    return ResponseEntity.ok(createTaxReturnResponse(req, taxOwed));
                }
            }
        }

        return ResponseEntity.badRequest().build();
    }

    private Double calculateTaxTotal(Double grossIncome, Bracket bracket) {
        return (grossIncome - bracket.getAmount().getStarting()) * bracket.getAmount().getRate() + bracket.getAmount().getBase();
    }

    private TaxReturnResponse createTaxReturnResponse(TaxReturnRequest req, Double totalTax) {
        return TaxReturnResponse.builder()
                .totalTax(totalTax)
                .taxRefund(req.getTaxPaid() - totalTax)
                .build();
    }
}
