package app.gozenko.service.interfaces;

import app.gozenko.dto.CreditDto;
import app.gozenko.dto.ScoringDataDto;

public interface ScoringService {
    CreditDto createScoringData(ScoringDataDto request);
}
