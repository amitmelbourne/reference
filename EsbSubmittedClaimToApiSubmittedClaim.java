package com.medibank.digital.integration.claim.services.business.mapping;

import au.com.medibank.ws.esb.claimsubmit.*;
import com.medibank.digital.api.model.*;
import com.medibank.digital.integration.common.utils.DateTransformer;

import java.util.ArrayList;
import java.util.List;

public final class EsbSubmittedClaimToApiSubmittedClaim {
    private EsbSubmittedClaimToApiSubmittedClaim() {}

    public static ClaimResponse map(SubmitResType esbResponse) {
        ClaimResponse apiClaim = new ClaimResponse();
        ClaimResType esbClaim = esbResponse.getClaim();
        
        apiClaim.setId(esbClaim.getClaimID());
        apiClaim.setReferenceNumber(esbClaim.getClaimRefID());
        apiClaim.setStatus(esbResponse.getClaimStatus());

        apiClaim.setSubmissionDate(DateTransformer.xmlGregorianCalendarToString(esbClaim.getAssessTs()));

        if (esbClaim.getPayment() != null) {
            apiClaim.setPayToWho(esbClaim.getPayment().getPayToWho());
        }

        if (esbClaim.getClaimAmt() != null) {
            apiClaim.setTotalBonusPaidAmount(
                    esbToApiAmount(esbClaim.getClaimAmt().getTotalBonusPaidAmt()));
            apiClaim.setTotalBenefitPaidAmount(
                    esbToApiAmount(esbClaim.getClaimAmt().getTotalBenefitPaidAmt()));
            apiClaim.setTotalPaidAmount(
                    esbToApiAmount(esbClaim.getClaimAmt().getTotalPaidAmt()));
            apiClaim.setTotalRewardPaidAmt(
                    esbToApiAmount(esbClaim.getClaimAmt().getTotalRewardPaidAmt()));
        }


        apiClaim.setItems(mapEsbClaimItemsToApiClaimItems(esbClaim.getClaimItemLine()));

        return apiClaim;
    }

    private static List<ClaimResponseItem> mapEsbClaimItemsToApiClaimItems(List<ItemResType> esbItems) {
        List<ClaimResponseItem> apiItems = new ArrayList<>();
        if (esbItems == null) {
            return apiItems;
        }

        for(ItemResType esbItem : esbItems) {
            ClaimResponseItem apiItem = new ClaimResponseItem();
            apiItems.add(apiItem);

            apiItem.setId(Integer.toString(esbItem.getItemLineID()));
            apiItem.setCode(esbItem.getItemCd());
            apiItem.setDateOfService(DateTransformer.xmlGregorianCalendarToString(esbItem.getServiceTs()));
            apiItem.setStatus(esbItem.getItemStatus());
            if (esbItem.getClaimItemLineAmt() != null) {
                apiItem.setBenefitPaidAmount(
                        esbToApiAmount(esbItem.getClaimItemLineAmt().getBenefitPaidAmt()));
                apiItem.setBonusPaidAmount(
                        esbToApiAmount(esbItem.getClaimItemLineAmt().getBonusPaidAmt()));
                apiItem.setRewardPaidAmt(esbToApiAmount(esbItem.getClaimItemLineAmt().getRewardPaidAmt()));
            }

            apiItem.setAssessments(mapEsbClaimAssessmentsToApiAssessments(esbItem.getAssessment()));
        }

        return apiItems;
    }

    private static List<ClaimResponseItemAssessment> mapEsbClaimAssessmentsToApiAssessments(List<AssessmentType> esbAssessments) {
        List<ClaimResponseItemAssessment> apiAssessments = new ArrayList<>();
        if (esbAssessments == null) {
            return apiAssessments;
        }

        for(AssessmentType esbAssessment : esbAssessments) {
            ClaimResponseItemAssessment apiAssessment = new ClaimResponseItemAssessment();
            apiAssessments.add(apiAssessment);

            apiAssessment.setCode(esbAssessment.getAssessmentCd());
            apiAssessment.setDescription(esbAssessment.getAssessmentDesc());
        }

        return apiAssessments;
    }

    private static double esbToApiAmount(AmtType esbAmount) {
        if (esbAmount == null || esbAmount.getAmt() == null) {
            return 0D;
        }
        return esbAmount.getAmt().doubleValue();
    }
}