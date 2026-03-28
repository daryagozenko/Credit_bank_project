package app.gozenko.service.interfaces;

import app.gozenko.dto.FinishRegistrationRequestDto;
import app.gozenko.dto.ScoringDataDto;
import app.gozenko.entity.Statement;

public interface ScoringDataService {
    ScoringDataDto createScoringData(FinishRegistrationRequestDto finishRegistration, Statement statement);
}
