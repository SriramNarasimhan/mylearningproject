package com.suntrust.dotcom.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.MonthlyCalculatorItemsBean;

/**
 * MonthlyCalculatorComponent class is to fetch the values for the Monthly Budget calculator component
 *
 * @author Cognizant
 * @version 1.0
 * @since 16 NOV 2017
 *
 */
public class MonthlyCalculatorComponent extends BaseUsePojo {

	List<MonthlyCalculatorItemsBean> beanListMonthlyExpense;
	List<MonthlyCalculatorItemsBean> beanListMonthlyIncome;	
	private final Logger log = LoggerFactory.getLogger(MonthlyCalculatorComponent.class);
			
	@Override
	public void activate() throws Exception {
		log.debug("Inside MonthlyCalculatorComponent Activate method");
		beanListMonthlyExpense= populateBeanList("monthlyexpenses", MonthlyCalculatorItemsBean.class);
		beanListMonthlyIncome = populateBeanList("monthlyincome", MonthlyCalculatorItemsBean.class);		
		log.debug("This is beanListMonthlyExpense "+beanListMonthlyExpense);
		log.debug("This is beanListMonthlyIncome "+beanListMonthlyIncome);
		
		for (MonthlyCalculatorItemsBean monthlyCalculatorItemsBean : beanListMonthlyExpense) {
			log.info("Monthly Expense"+monthlyCalculatorItemsBean.getMonthlyexpensefield());
		}
		for (MonthlyCalculatorItemsBean monthlyCalculatorItemsBean : beanListMonthlyIncome) {
			log.info("Monthly Income"+monthlyCalculatorItemsBean.getMonthlyincomefield());
		}		
	}

	public List<MonthlyCalculatorItemsBean> getBeanListMonthlyExpense() {
		return beanListMonthlyExpense;
	}
	
	public List<MonthlyCalculatorItemsBean> getBeanListMonthlyIncome() {
		return beanListMonthlyIncome;
	}
	



}
