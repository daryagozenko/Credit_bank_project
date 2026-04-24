package app.gozenko.service;

import app.gozenko.dto.CreditDto;
import app.gozenko.entity.Credit;
import app.gozenko.enums.CreditStatus;
import app.gozenko.repository.CreditRepository;
import app.gozenko.service.interfaces.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;

    @Override
    public Credit createCredit(CreditDto creditDto) {
        log.info("Input: creditDto-${}", creditDto);
        log.debug("Credit dto push into fill stage");
        return creditRepository.save(fillCreditInfo(creditDto));
    }

    private Credit fillCreditInfo(CreditDto creditDto) {
        log.debug("Input: creditDto-${}", creditDto);
        log.info("Credit build");
        return Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .monthlyPayment(creditDto.getMonthlyPayment())
                .rate(creditDto.getRate())
                .psk(creditDto.getPsk())
                .paymentSchedule(creditDto.getPaymentSchedule())
                .isInsuranceEnabled(creditDto.getIsInsuranceEnabled())
                .isSalaryClient(creditDto.getIsSalaryClient())
                .creditStatus(CreditStatus.CALCULATED)
                .build();
    }

}
