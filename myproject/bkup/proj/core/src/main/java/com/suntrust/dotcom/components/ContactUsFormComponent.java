package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.ContactUsFormComponentBean;



public class ContactUsFormComponent extends BaseUsePojo{

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsFormComponent.class);
	private List<ContactUsFormComponentBean> contactUsBean= null;
	private List<String> questionList=null;
	private Map<String,String> questionMailMap=null;
	@Override
	public void activate() throws Exception {

		LOGGER.info("activated ContactUsformComponent");
		
		contactUsBean=populateBeanList("iItems", ContactUsFormComponentBean.class);
		
		questionList=new ArrayList<String>();
		questionMailMap=new HashMap<>();
		
		contactUsBean.stream().forEach(beanItem -> {
			
			if(StringUtils.isNotBlank(beanItem.getQuestiontype())){
				questionList.add(beanItem.getQuestiontype());
				questionMailMap.put(beanItem.getQuestiontype(), beanItem.getEmailid());
			}
			});
		
	}
	
	public List<String> getQuestionList() {
		return questionList;
	}
	public Map<String, String> getQuestionMailMap() {
		return questionMailMap;
	}

}
