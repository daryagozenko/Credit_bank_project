package app.gozenko.service.interfaces;

import app.gozenko.dto.LoanStatementRequestDto;
import app.gozenko.dto.ScoringDataDto;
import jakarta.validation.Valid;

public interface PreScoringService {
    void preScoringLoan(@Valid LoanStatementRequestDto request);

    void preScoringScoreData(@Valid ScoringDataDto request);
}
