package com.backbase.dbs.positivepay.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.dbs.positivepay.configuration.PositivePayConfiguration;
import com.backbase.dbs.positivepay.service.model.ArrangementDetails;
import java.util.Collections;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@Slf4j
public class ExtendedSubscriptionValidator extends PositivePaySubscription {

    private static final String KEY = "single-account";
    private static final String IDENTIFIER = "single-account-subscription";
    private static final String ACCOUNT_NUMBER = "1234509876";

    @Autowired
    public ExtendedSubscriptionValidator(PositivePayConfiguration configuration) {
        super(configuration);
    }


    /**
     * Post subscription validator for single account subscription. In case arrangement has this subscription, then it
     * allows to submit check only with {@link ExtendedSubscriptionValidator#ACCOUNT_NUMBER}.
     *
     * @param arrangementDetails arrangement details related with Positive Pay
     * @return set of subscription keys
     */
    @Override
    public Set<String> filterSubscription(ArrangementDetails arrangementDetails) {

        Set<String> keys = super.filterSubscription(arrangementDetails);

        //post custom validation
        validateAccountNumber(arrangementDetails);

        return keys;
    }

    private void validateAccountNumber(ArrangementDetails arrangementDetails) {

        boolean match = arrangementDetails.getIdentifiers().stream().anyMatch(IDENTIFIER::equals);
        if(!match) {
            return;
        }

        String accountNumber = findAccountNumber(arrangementDetails);
        if (isBlank(accountNumber) || !accountNumber.equals(ACCOUNT_NUMBER)) {
            throw new BadRequestException().withErrors(Collections.singletonList(
                new Error().withMessage(
                    "This account has single account subscription and "
                        + "account number doesn't match with configured account number.")
                    .withKey(String.format("positivepay.api.subscription.%s", KEY))));
        }
    }

    private String findAccountNumber(ArrangementDetails arrangementDetails) {
        if (!arrangementDetails.getBban().isBlank()) {
            return arrangementDetails.getBban();
        }
        if (!arrangementDetails.getIban().isBlank()) {
            return arrangementDetails.getIban();
        }
        if (!arrangementDetails.getNumber().isBlank()) {
            return arrangementDetails.getNumber();
        }
        if (!arrangementDetails.getProductNumber().isBlank()) {
            return arrangementDetails.getProductNumber();
        }
        return null;
    }
}
