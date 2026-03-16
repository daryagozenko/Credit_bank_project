package app.gozenko.service;

import app.gozenko.dto.LoanOfferDto;
import app.gozenko.service.interfaces.LoanOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanOfferServiceImpl implements LoanOfferService {

    private final CheckValueService checkValueService;

    @Override
    public List<LoanOfferDto> createLoanOffers(BigDecimal amount, Integer term){
        List<LoanOfferDto> result = new ArrayList<>();

        log.debug("Transferred: amount-{}, term-{}",amount,term);

        result.add(checkValueService.salaryAndInsuranceClient(amount, term));
        result.add(checkValueService.insuranceClient(amount, term));
        result.add(checkValueService.salaryClient(amount, term));
        result.add(checkValueService.noneSalaryAndInsuranceClient(amount,term));

        log.info("List of loan offers: {}",result);
        return result;
    }
}
