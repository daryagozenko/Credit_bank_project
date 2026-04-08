package app.gozenko.service.interfaces;

import app.gozenko.dto.CreditDto;
import app.gozenko.entity.Credit;

public interface CreditService {
    Credit createCredit(CreditDto creditDto);
}


