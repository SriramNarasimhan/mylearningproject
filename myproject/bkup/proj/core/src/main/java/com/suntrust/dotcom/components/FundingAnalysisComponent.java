package com.suntrust.dotcom.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.FundingAnalysisItemsBean;

public class FundingAnalysisComponent extends BaseUsePojo {

	List<FundingAnalysisItemsBean> beanListExpenses;
	List<FundingAnalysisItemsBean> beanListAids;
	List<FundingAnalysisItemsBean> beanListSavings;
	private final Logger log = LoggerFactory.getLogger(FundingAnalysisComponent.class);
			
	@Override
	public void activate() throws Exception {
		log.debug("inside activate");
		beanListExpenses= populateBeanList("expenses", FundingAnalysisItemsBean.class);
		beanListAids = populateBeanList("aids", FundingAnalysisItemsBean.class);
		beanListSavings = (populateBeanList("savings", FundingAnalysisItemsBean.class));
		log.debug("This is beanListExpenses "+beanListExpenses);
		log.debug("This is beanListAids "+beanListAids);
		log.debug("This is beanListSavings "+beanListSavings);
		for (FundingAnalysisItemsBean fundingAnalysisItemsBean : beanListAids) {
			log.info(fundingAnalysisItemsBean.getAidfieldvalue());
		}
		for (FundingAnalysisItemsBean fundingAnalysisItemsBean : beanListExpenses) {
			log.info(fundingAnalysisItemsBean.getExpensefieldvalue());
		}
		for (FundingAnalysisItemsBean fundingAnalysisItemsBean : beanListSavings) {
			log.info(fundingAnalysisItemsBean.getSavingfieldvalue());
		}
	}

	public List<FundingAnalysisItemsBean> getBeanListExpenses() {
		return beanListExpenses;
	}
	
	public List<FundingAnalysisItemsBean> getBeanListAids() {
		return beanListAids;
	}
	public List<FundingAnalysisItemsBean> getBeanListSavings() {
		return beanListSavings;
	}



}
