package com.currencyconversion;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion currencyConversion(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, from, to);
		CurrencyConversion currencyConversion = responseEntity.getBody();
		if (currencyConversion == null) {
			throw new RuntimeException("Entity not found...");
		}
		System.out.println(currencyConversion.getConversionMultiple());
		return new CurrencyConversion(currencyConversion.getId(), from, to, currencyConversion.getConversionMultiple(),
				quantity, quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment());
	}
	
	@GetMapping("/currency-conversion/feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion currencyConversionFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		CurrencyConversion currencyConversion = proxy.findCurrencyExchange(from, to);
		if (currencyConversion == null) {
			throw new RuntimeException("Entity not found...");
		}
		System.out.println(currencyConversion.getConversionMultiple());
		return new CurrencyConversion(currencyConversion.getId(), from, to, currencyConversion.getConversionMultiple(),
				quantity, quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment());
	}
}
