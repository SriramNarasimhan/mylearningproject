package com.suntrust.dotcom.services;

import java.util.List;
import java.util.Map;

/**
 * Email Service Interface
 * 
 * @author Anomitra (uiam82)
 *
 */
public interface EmailService
{
	/**
	 * Method to be implemented to send email to TOO and CC list
	 * 
	 * @param paramString
	 * @param paramMap
	 * @param paramVarArgsTo
	 * @param paramVarArgsCC
	 */
  public void sendEmail(String paramString, Map<String, String> paramMap, List<String> paramVarArgsTo, List<String> paramVarArgsCC);
  
  /**
   * Method to be implemented to send email to TOO list
   * 
   * @param paramString
   * @param paramMap
   * @param paramVarArgsTo
   */
  public void sendEmail(String paramString, Map<String, String> paramMap, List<String> paramVarArgsTo);
}
