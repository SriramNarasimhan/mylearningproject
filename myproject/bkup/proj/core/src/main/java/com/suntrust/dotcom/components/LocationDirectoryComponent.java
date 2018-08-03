/**
 * 
 */
package com.suntrust.dotcom.components;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.suntrust.dotcom.utils.GenericEnum;

/**
 * @author UGRK104
 *
 */
public class LocationDirectoryComponent extends WCMUsePojo {
	
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationDirectoryComponent.class);
	
	/**	jcr property name for style field * */
	private static final String STYLE_IDENTIFIER = "styleFrom";
	
	/** instance variable to hold child page collection in Map * */
	private Map<String, List<Page>> childPageMapCollection = null;
	
	/** instance variable to hold search results in list * */
	private List<Page> searchResultList = null;
	
	/** instance variable to hold alphabet vaules in Map * */
	private Map<String, String> alpabetsMap = null;
	
	/** instance variable to hold search result text value * */
	private String searchResultText;
	
	/** instance variable to hold jcr property "styleFrom" value * */
	private String styleFrom;

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	public void activate(){
		

		LOGGER.info("INVOKED LOCATION DIRECTORY COMPONENT ACTIVATE METHOD");
		
		try
		{
			this.setAlpabetsMap();
			List<Page> childPages = null;
			Page currentPage = getCurrentPage();

			if (currentPage != null) 
			{
				
				Node jcrNode = getResource().adaptTo(Node.class);
				
				Property styleProperty = jcrNode.getProperty(STYLE_IDENTIFIER);
				
				//Get the styleFrom property value from the dialog.
				String style = (styleProperty != null && !styleProperty.getString().trim().isEmpty()) ? styleProperty.getString().trim() : STYLE_IDENTIFIER;
				
				Locale pageLanguageLocale = currentPage.getLanguage(false);
				
				this.styleFrom = Character.toString(style.charAt(0)).toUpperCase(pageLanguageLocale) + style.substring(1);
				
				//Get the just next low level of child pages of the current page in list collection.
				childPages = this.getChildPagesList(currentPage,getRequest());	
				
				//Start fix for Defect ID : 199399
				
				if(style.equals("city"))
				{
					Pattern pattern = Pattern.compile(GenericEnum.US_ZIP_CODE_REGEX.getValue());
					
					for (Iterator<Page> iterator = childPages.iterator(); iterator.hasNext(); ) 
					{					
						Page childPage = iterator.next();
						
						if(childPage != null)
						{
							Matcher matcher = pattern.matcher(childPage.getName().trim());
							if (matcher.matches()) 
							{
								iterator.remove();
							}
						}
						
					}				
	
				}
				
				//End fix for Defect ID : 199399
				
				//Get the just next low level of child pages of the current page in Sorted Map collection.
				this.childPageMapCollection = this.getSortedchildPageMapCollection(childPages , pageLanguageLocale);
				
				//check for style "city"
				if(style.equals("city"))
				{
					this.childPageMapCollection = this.getSortedchildPageMapCollectionGrouping(this.childPageMapCollection);
				}
				
				//check for style "search"
				if(style.equals("search"))
				{				
					this.searchResultList = this.getBranchORATMList(this.childPageMapCollection);
				}
			}
		
		}
		catch (PathNotFoundException pathNotFoundException) {
			LOGGER.error(pathNotFoundException.getMessage(),pathNotFoundException);
		} catch (RepositoryException repositoryException) {
			LOGGER.error(repositoryException.getMessage(),repositoryException);
		} catch (Exception exception) {			
			LOGGER.error(exception.getMessage(),exception);
		}

	}




	/**
	 * @param map {@link Map}}
	 * @return serach result in list collection.
	 */
	private List<Page> getBranchORATMList(final Map<String, List<Page>> map)
	{
		List<Page> branchOrATMList = new ArrayList<Page>();
		
		Pattern pattern = Pattern.compile(GenericEnum.US_ZIP_CODE_REGEX.getValue());
		
		String cityPageName = GenericEnum.EMPTY_STRING.getValue();
		String statePageName = GenericEnum.EMPTY_STRING.getValue();
		
		if (map != null) {
			
			for (Map.Entry<String, List<Page>> e : map.entrySet()) {
				
				List<Page> zipCodePageList = e.getValue();
				
				for(Page zipCodePage : zipCodePageList)
				{
					
					if (zipCodePage != null) {
						
						Matcher matcher = pattern.matcher(zipCodePage.getName().trim());
						
						if (matcher.matches()) {
							
							cityPageName = zipCodePage.getParent().getTitle();
							statePageName = zipCodePage.getParent().getParent().getTitle();
							
							List<Page> branchOrATMPageList = this.getChildPagesList(zipCodePage,getRequest());
							branchOrATMList.addAll(branchOrATMPageList);
							
						}
						
					}
				}
			}
		}
		
		
		
		String text = "\"" + cityPageName + GenericEnum.COMMA_SYMBOL.getValue() + GenericEnum.EMPTY_SPACE_STRING.getValue() + statePageName + "\"";
					
		MessageFormat mf = new MessageFormat(GenericEnum.SEARCH_RESULT_TEXT.getValue().trim());
		
		this.searchResultText = mf.format(new Object[] { Integer.toString(branchOrATMList.size()) , text});		
		
		return branchOrATMList;
	}



	/**
	 * @param map {@link Map}
	 * @return sorted map collection
	 * 
	 * Grouping Logic - 
	 * 
	 * Step 1 - Check for Cities count if its Minimum 5 cities then it will display separately under corresponding .
	 * 
	 * Step 2 - If cities count corresponding to that alphabet is less than 5 then it will try to group with predecessor or successor. 
	 * 			If predecessor cities count exceed 15  count after grouping  then it will check successor cities count after grouping. 
	 * 			If the count is not more than 15 then only it will group respectively.  
	 * 			In short predecessor or successor items count should not exceed 15 count after grouping otherwise it will not perform grouping and display separately.
	 */
	public Map<String,List<Page>> getSortedchildPageMapCollectionGrouping(final Map<String, List<Page>> map) {
		
		NavigableMap<String,List<Page>> navigableMap = (NavigableMap<String, List<Page>>) map;
		
		Boolean boolean1 = true;
		
		while(boolean1)
		{
			
			for (Map.Entry<String, List<Page>> e : navigableMap.entrySet()) {
				
				List<Page> list = e.getValue();
				int currentValue =list.size();
				
				// Step 1 Logic
				if (currentValue > 5) {
					
					LOGGER.debug(e.getKey() + " : value is greater than 5");
					
					Map.Entry<String, List<Page>> next = navigableMap.higherEntry(e.getKey()); // next	
					if(next == null)
					{
						boolean1 = false;
					}
					
					continue;
				}
				
				// Step 2 Logic
				else {
					
					LOGGER.debug(e.getKey() + " : value is lesser than 5");
					
					Map.Entry<String, List<Page>> prev = navigableMap.lowerEntry(e.getKey());  // previous
					Map.Entry<String, List<Page>> next = navigableMap.higherEntry(e.getKey()); // next	
				    
					// Lookup up previous map entry (Alphabet Key) in order to merge.
				    if (prev != null && (e.getValue().size() + prev.getValue().size()) < 15) {
						// merge and update map than recursive call
				    	
				    	String mergedKey = prev.getKey().substring(0 , 1) + GenericEnum.DASH_SYMBOL.getValue() + e.getKey().substring(0 , 1);
				    	
				    	
				    	List<Page> mergedValue = new ArrayList<Page>();
				    	
				    	mergedValue.addAll(prev.getValue());
				    	mergedValue.addAll(e.getValue());	
				    	
				    	// update alphabetsMap
						this.updateAlpabetsMap(prev.getKey() , mergedKey);
						this.updateAlpabetsMap(e.getKey() , mergedKey);
				    	
				    	navigableMap.remove(prev.getKey());// remove previous key
				    	navigableMap.remove(e.getKey());// remove current key
				    	
				    	navigableMap.put(mergedKey, mergedValue); // update the map with mergedKey-value.
				    	
				    	boolean1 = true;
				    	
				    	break;
					}
				    
				    
				 // Lookup up next map entry (Alphabet Key) in order to merge.
				    if (next != null && (e.getValue().size() + next.getValue().size()) < 15) {
				    	// merge and update map than recursive call
				    	
				    	String mergedKey = e.getKey().substring(0 , 1) + GenericEnum.DASH_SYMBOL.getValue() + next.getKey().substring(0 , 1);
				    	
						List<Page> mergedValue = new ArrayList<Page>();
						
						mergedValue.addAll(e.getValue());
						mergedValue.addAll(next.getValue());	
						
						// update alphabetsMap
						this.updateAlpabetsMap(next.getKey() , mergedKey);
						this.updateAlpabetsMap(e.getKey() , mergedKey);
				    	
				    	navigableMap.remove(next.getKey());// remove previous key
				    	navigableMap.remove(e.getKey());// remove current key
				    	
				    	navigableMap.put(mergedKey, mergedValue); // update the map with mergedKey-value.
				    	
				    	boolean1 = true;
				    	
				    	break;
					}
				    
				    if (next == null) {
						boolean1 = false;
					}
				    
				    continue;
				}
				
			}
		}
		
		if(LOGGER.isDebugEnabled())
		{
			for (Map.Entry<String, List<Page>> e : navigableMap.entrySet()) {
				LOGGER.debug(e.getKey() + " : " + e.getValue().size());
			}
		}
		
		return navigableMap;
		
	}
	
	
	
	/**
	 * @param pages {@link List}
	 * @return convert the page list collection into Sorted Map collection.
	 */
	public Map<String, List<Page>> getSortedchildPageMapCollection(final List<Page> pages , final Locale pageLanguageLocale) {
		
		Map<String, List<Page>> unSortedchildPageMapCollection = new HashMap<String, List<Page>>();

		String firstCahrPlaceholder = null;
		List<Page> list = null;

		for (Page childPage : pages) {

			String firstChar = childPage.getName().trim().substring(0, 1);
			
			firstChar = firstChar.toUpperCase(pageLanguageLocale);

			if (firstCahrPlaceholder == null) {
				
				list = new ArrayList<Page>();
				
				firstCahrPlaceholder = firstChar;
				list.add(childPage);
			}
			
			else if (firstCahrPlaceholder.equalsIgnoreCase(firstChar)) {
				list.add(childPage);
			}

			else {
				
				// if the current firstChar is already available in map as a key than update its value.
				if(unSortedchildPageMapCollection.get(firstChar) != null)
				{
					List<Page> list2 = unSortedchildPageMapCollection.get(firstChar);
					list2.add(childPage);
				}
				
				else
				{
					unSortedchildPageMapCollection.put(firstCahrPlaceholder, list);

					firstCahrPlaceholder = firstChar;
					list = new ArrayList<Page>();
					
					list.add(childPage);
				}
			}
		}
		
		unSortedchildPageMapCollection.put(firstCahrPlaceholder, list);
		firstCahrPlaceholder = null;

		return new TreeMap<String, List<Page>>(unSortedchildPageMapCollection);

	}
	
	
	/**
	 * @param page {@link Page}
	 * @param slingHttpServletRequest {@link SlingHttpServletRequest}
	 * @return list collection of child pages of the page passed as argument in the method.
	 */
	public List<Page> getChildPagesList(Page page,SlingHttpServletRequest slingHttpServletRequest) {
		List<Page> childPages = new ArrayList<Page>();
		Page childPage = null;
		try {
			if (page != null) {
				Iterator<Page> pageItr = page.listChildren();
				while (pageItr.hasNext()) {
					childPage = pageItr.next();
					childPages.add(childPage);
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error while getting child pages :: ", e);
		}
		return childPages;
	}
	
	
	/**
	 * Purpose - create a map of alphabets.
	 */
	private void setAlpabetsMap() {
		
		Map<String, String> map = new TreeMap<String, String>();
		
		for (char ch = 'A'; ch <= 'Z'; ++ch) 
		{
			map.put(String.valueOf(ch) , String.valueOf(ch)); 
		}
		
		this.alpabetsMap = map;
	}
	
	/**
	 * @param key {@link String}
	 * @param value {@link String}
	 * 
	 * Purpose - update the alpabetsMap.
	 */
	private void updateAlpabetsMap(String key , String value) {
		
		String firstChar = value.substring(0 , 1);
		
		if(this.alpabetsMap.get(key) != null)
		{
			this.alpabetsMap.put(key, value);
		}	
		
		for (Map.Entry<String, String> e : this.alpabetsMap.entrySet()) {
			
			if (firstChar.equals(e.getValue().subSequence(0, 1))) {
				e.setValue(value);
			}
		}

	}

	
	/**
	 * @return alphabet map collection.
	 */
	public Map<String, String> getAlpabetsMap() {
		return this.alpabetsMap;
	}
	
	/**
	 * @return child pages map collection.
	 */
	public Map<String, List<Page>> getChildPageMapCollection() {
		return this.childPageMapCollection;
	}
	
	
	/**
	 * @return search result list collection.
	 */
	public List<Page> getSearchResultList() {
		return this.searchResultList;
	}
	
	/**
	 * @return search result text.
	 */
	public String getSearchResultText() {
		
		return this.searchResultText;
	}
	
	/**
	 * @return modefied styleFrom value.
	 */
	public String getStyleFrom() {
		return this.styleFrom;
	}

	
}
