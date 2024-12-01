package com.bakaneko.finance.controller;

import com.bakaneko.finance.config.YamlConfig;
import com.bakaneko.finance.config.YamlConfig.Income;
import com.bakaneko.finance.config.YamlConfig.Amount;
import com.bakaneko.finance.config.YamlConfig.Bracket;
import com.bakaneko.finance.config.YamlConfig.TaxData;
import com.bakaneko.finance.model.TaxReturnRequest;
import com.bakaneko.finance.model.TaxReturnResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxControllerTests {

	private final List<TaxData> tax = new ArrayList<>();

	@InjectMocks
	private TaxController controller;

	@Mock
	YamlConfig config;

	@BeforeEach
	public void setup() {
		TaxData tax1 = TaxData.builder().build();
		tax1.setYear("2024-25");
		tax1.setBrackets(
				List.of(
						createTaxBracket(0, 18200, 0, 0.0, 0),
						createTaxBracket(18201, 45000, 0, 0.16, 18200),
						createTaxBracket(45001, 135000, 4288, 0.30, 45000),
						createTaxBracket(135001, 190000, 31288, 0.37, 135000)
				)
		);
		tax1.setBaller(
				createTaxBracket(190001, null, 51638, 0.45, 190000)
		);
		tax.add(tax1);
	}

	@Test
	void test_income_inLowestBracket_returnNoTaxPaid() {
		when(config.getTaxData()).thenReturn(tax);
		TaxReturnRequest req = TaxReturnRequest.builder().year("2024-25").grossIncome(10000.0).taxPaid(0.0).build();

		ResponseEntity<TaxReturnResponse> res = controller.taxReturn(req);

		assertEquals(HttpStatus.OK, res.getStatusCode());
		assertEquals(0.0, Objects.requireNonNull(res.getBody()).getTotalTax(), 0.01);
	}

	@Test
	void test_income_inHighestBracket_returnsTaxAmount() {
		when(config.getTaxData()).thenReturn(tax);
		TaxReturnRequest req = TaxReturnRequest.builder().year("2024-25").grossIncome(200000.0).taxPaid(0.0).build();

		ResponseEntity<TaxReturnResponse> res = controller.taxReturn(req);

		assertEquals(HttpStatus.OK, res.getStatusCode());
		assertEquals(56138.0, Objects.requireNonNull(res.getBody()).getTotalTax(), 0.01);
		assertEquals(-56138.0, Objects.requireNonNull(res.getBody()).getTaxRefund(), 0.01);
	}

	@Test
	void test_income_inMiddleBracket_returnsTaxAmount() {
		when(config.getTaxData()).thenReturn(tax);
		TaxReturnRequest req = TaxReturnRequest.builder().year("2024-25").grossIncome(100000.0).taxPaid(0.0).build();

		ResponseEntity<TaxReturnResponse> res = controller.taxReturn(req);

		assertEquals(HttpStatus.OK, res.getStatusCode());
		assertEquals(20788.0, Objects.requireNonNull(res.getBody()).getTotalTax(), 0.01);
		assertEquals(-20788.0, Objects.requireNonNull(res.getBody()).getTaxRefund(), 0.01);
	}

	private Bracket createTaxBracket(Integer lower, Integer upper, Integer base, Double rate, Integer starting) {
		Income income = Income.builder()
				.lower(lower)
				.upper(upper)
				.build();
		Amount amount = Amount.builder()
				.base(base)
				.rate(rate)
				.starting(starting)
				.build();
		return Bracket.builder()
				.income(income)
				.amount(amount)
				.build();
	}
}
