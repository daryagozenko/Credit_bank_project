package app.gozenko.interfaces;

import app.gozenko.dto.LoanStatementRequestDto;
import jakarta.validation.Valid;

public interface PreScoringService {
    void preScoring(@Valid LoanStatementRequestDto request);
}
