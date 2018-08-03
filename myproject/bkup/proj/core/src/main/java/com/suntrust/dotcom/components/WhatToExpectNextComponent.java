package com.suntrust.dotcom.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.WhatToExpectNextStepsBean;
import com.suntrust.dotcom.utils.Utils;

public class WhatToExpectNextComponent extends BaseUsePojo {
	
	private List<WhatToExpectNextStepsBean> stepItems=null;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WhatToExpectNextComponent.class);
			
	@Override
	public void activate() throws Exception {
		
		LOGGER.info("Retrieving step List from component.");
		
		stepItems=populateBeanList("iItems", WhatToExpectNextStepsBean.class);
		stepItems.stream().forEach(
				step-> step.setModifiedURL(Utils.getModifyURL(step.getStepurl(),step.getUrlparam(),step.getAnchortag()))
						);
	}

	public List<WhatToExpectNextStepsBean> getStepItems() {
		return stepItems;
	}
	
}
