package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.suntrust.dotcom.utils.GenericUtil;

public class InternalNavigationComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InternalNavigationComponent.class);

	List<Page> pages = new ArrayList<Page>();
	List<Page> siblings = new ArrayList<Page>();
	//Iterator<Page> childrenIterator = (Iterator<Page>) new ArrayList<Page>();
	List<Page> children = new ArrayList<Page>();
	//List<Page> grandChildren = new ArrayList<Page>();
	List<Page> outputChildren = new ArrayList<Page>();
	List<String> pageLink = new ArrayList<String>();
	List<String> siblingLink = new ArrayList<String>();
	String providedPath = "test";
	String headerTitle;
	String headerLink;
	Page providedPage;
	Page headerPage;
	Page childPage;
	String currentPagePath = "";

	
	public String getHeaderPath() {
		return headerTitle;
	}

	@Override
	public void activate() throws Exception {

		// This is a test comment;

		LOGGER.info("##### INVOKED ACTIVATE from InternalNavigationComponent");
		currentPagePath = getCurrentPage().getPath();

		if (getProperties().get("sectionroot")!=null)
		{
			providedPath = getProperties().get("sectionroot").toString();
			providedPage = getPageManager().getPage(providedPath);
			//get the page for wrapper logic
			String wrapperUrl = (String) getRequest().getAttribute("internalNavPage");
			String template = getCurrentPage().getTemplate().getName();
			if(wrapperUrl != null && template.contains("wrapper"))
			{
				currentPagePath = wrapperUrl;
			}
			getChildPages(providedPage);
			headerPage = providedPage;
		}
		else
		{
			getChildPages(getCurrentPage());
/*			headerTitle=getCurrentPage().getParent(2).getTitle();
			headerLink = getCurrentPage().getParent(2).getPath() + ".html";*/
			headerPage = getCurrentPage().getParent(1);
		}
		
		
		//siblings=getSiblings();
		
		//System.out.println("component node properties : " + providedPath);


	}

	public Page getHeaderPage() {
		return headerPage;
	}

	public List<Page> getPages() {
		return pages;
	}
	

	
	public String getHeaderLink() {
		return headerLink;
	}

	public List<Page> getSiblings() {
		
		return siblings;
	}


	public String getCurrentPagePath()
	{
		return currentPagePath;
	}

	public List<Page> getChildren() {
		return children;
	}

	public void getChildPages(Page page) {
		GenericUtil genericUtil = new GenericUtil();

	
		pages = genericUtil.getChildPagesList(page, getRequest());
		
/*		for (Page item : pages) {
		    if (item.hasChild(getCurrentPage().getName()))
		    {
		    	childrenIterator = item.listChildren();
		    	int index=0;
		    	while(childrenIterator.hasNext())
		    	{
		    		children.add(index, childrenIterator.next());
		    		index++;
		    	}
		    }
		}*/
		
		
		if (getProperties().get("sectionroot")!=null)
		{
			siblings = genericUtil.getChildPagesList(providedPage,getRequest());
		}
		else
		{
			siblings = genericUtil.getChildPagesList(getCurrentPage().getParent(),getRequest());
		}
		boolean iaminleve2 = false;
		
		for(Page sibling : siblings)
		{
			//System.out.println("CURRENT PATH.getPath()<<<<<<<<<<<<   " + getCurrentPage().getPath());
			if(sibling.getPath().equals(currentPagePath))
			{
				iaminleve2 = true;
				children = genericUtil.getChildPagesList(sibling,getRequest());
				//System.out.println("sibling.getPath()<<<<<<<<<<<<   " + sibling.getPath());
				outputChildren=children;
				//System.out.println("sibling.list size()<<<<<<<<<<<<   " +outputChildren.size());
				/*for(Page child:children)
				{
					//System.out.println("Child <<<<<<<<<<< " + child.getName());
					
					if ((child.getParent().getPath()==getCurrentPage().getPath()) || (child.getPath()==getCurrentPage().getPath()))
					{
						outputChildren=children;
						
						//System.out.println("Child <<<<<<<<<<< " + child.getName());
					}
					
				}*/
				
			}
			
		}
		if(!iaminleve2)
		{
			//check if i am in level 3
			for(Page sibling : siblings)
			{

					children = genericUtil.getChildPagesList(sibling,getRequest());
					for(Page child:children)
					{
						//System.out.println("Child <<<<<<<<<<< " + child.getName());
						
						if ((child.getParent().getPath().equals(currentPagePath)) || (child.getPath().equals(currentPagePath)))
						{
							outputChildren=children;
							return;
							//System.out.println("Child <<<<<<<<<<< " + child.getName());
						}
						
					}
					
				
			}
		}
		
	}

	public List<Page> getOutputChildren() {
		return outputChildren;
	}

/*	public List<Page> getGrandChildren() {
		return grandChildren;
	}*/
	


}