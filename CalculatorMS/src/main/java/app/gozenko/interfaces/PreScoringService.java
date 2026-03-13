package app.gozenko.interfaces;

import app.gozenko.dto.LoanStatementRequestDto;

public interface PreScoringService {
    void preScoring(LoanStatementRequestDto request);
}
