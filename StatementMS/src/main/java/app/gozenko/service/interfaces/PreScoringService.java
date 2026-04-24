package app.gozenko.service.interfaces;

import app.gozenko.dto.LoanStatementRequestDto;
import jakarta.validation.Valid;

public interface PreScoringService {
    void preScoringLoan(@Valid LoanStatementRequestDto request);
}
