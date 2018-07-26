/**
 * 
 */
package com.first.myproject.services.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.first.myproject.services.CustomerService;
import com.first.myproject.vo.Customer;


/**
 * @author Welcome
 * 
 */
@Service
@Component(metatype = false, immediate = true)
public class CustomerServiceImpl implements CustomerService {

	/** Default log. */
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	private Session session;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.first.myproject.CustomerService#injestCustData(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public int injestCustData(String firstName, String lastName,
			String address, String desc) {
		LOG.info("firstName" + firstName);
		LOG.info("lastName" + lastName);
		LOG.info("address" + address);
		LOG.info("desc" + desc);
		try {

			// Invoke the adaptTo method to create a Session used to create a
			// QueryManager
			ResourceResolver resourceResolver = resolverFactory
					.getAdministrativeResourceResolver(null);
			session = resourceResolver.adaptTo(Session.class);

			// Create a node that represents the root node
			Node root = session.getRootNode();

			// Get the content node in the JCR
			Node content = root.getNode("content");

			// Determine if the content/customer node exists
			Node customerRoot = null;
			int custRec = doesCustExist(content);

			// -1 means that content/customer does not exist
			if (custRec == -1)
				// content/customer does not exist -- create it
				customerRoot = content.addNode("customer",
						"sling:OrderedFolder");
			else
				// content/customer does exist -- retrieve it
				customerRoot = content.getNode("customer");

			int custId = custRec + 1; // assign a new id to the customer node

			// Store content from the client JSP in the JCR
			Node custNode = customerRoot.addNode("customer" + firstName
					+ lastName + custId, "nt:unstructured");
			LOG.info("custNode: " + custNode.toString());
			// make sure name of node is unique
			custNode.setProperty("id", custId);
			LOG.info("Id" + custId);
			custNode.setProperty("firstName", firstName);
			LOG.info("firstName" + firstName);
			custNode.setProperty("lastName", lastName);
			LOG.info("lastName" + lastName);
			custNode.setProperty("address", address);
			LOG.info("address" + address);
			custNode.setProperty("desc", desc);
			LOG.info("desc" + desc);

			// Save the session changes and log out
			session.save();
			session.logout();
			return custId;
		}

		catch (Exception e) {
			LOG.error("RepositoryException: " + e);
		}
		return 0;
	}

	/*
	 * Determines if the content/customer node exists This method returns these
	 * values: -1 - if customer does not exist 0 - if content/customer node
	 * exists; however, contains no children number - the number of children
	 * that the content/customer node contains
	 */
	private int doesCustExist(Node content) {
		try {
			int childRecs = 0;
			if (content.hasNode("customer")) {
				java.lang.Iterable<Node> custNode = JcrUtils.getChildNodes(
						content, "customer");
				Iterator<Node> it = custNode.iterator();

				// only going to be 1 content/customer node if it exists
				if (it.hasNext()) {
					// Count the number of child nodes to customer
					Node customerRoot = content.getNode("customer");
					Iterable<Node> itCust = JcrUtils.getChildNodes(customerRoot);
					Iterator<Node> childNodeIt = itCust.iterator();

					// Count the number of customer child nodes
					while (childNodeIt.hasNext()) {
						childRecs++;
						childNodeIt.next();
					}
					return childRecs;
				}
			} else
				return -1; // content/customer does not exist
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// Convert Customer data retrieved from the AEM JCR
	// into an XML schema to pass back to client
	private Document toXml(List<Customer> custList) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			// Start building the XML to pass back to the AEM client
			Element root = doc.createElement("Customers");
			doc.appendChild(root);

			// Get the elements from the collection
			int custCount = custList.size();

			// Iterate through the collection to build up the DOM
			for (int index = 0; index < custCount; index++) {

				// Get the Customer object from the collection
				Customer myCust = (Customer) custList.get(index);

				Element Customer = doc.createElement("Customer");
				root.appendChild(Customer);

				// Add rest of data as child elements to customer
				// Set First Name
				Element first = doc.createElement("First");
				first.appendChild(doc.createTextNode(myCust.getFirst()));
				Customer.appendChild(first);

				// Set Last Name
				Element last = doc.createElement("Last");
				last.appendChild(doc.createTextNode(myCust.getLast()));
				Customer.appendChild(last);

				// Set Description
				Element desc = doc.createElement("Description");
				desc.appendChild(doc.createTextNode(myCust.getDescription()));
				Customer.appendChild(desc);

				// Set Address
				Element address = doc.createElement("Address");
				address.appendChild(doc.createTextNode(myCust.getAddress()));
				Customer.appendChild(address);
			}

			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String convertToString(Document xml) {
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xml);
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.first.myproject.CustomerService#getCustomerData(java.lang.String)
	 */
	public String getCustomerData(String filter) {
		Customer cust = null;

		List<Customer> custList = new ArrayList<Customer>();
		try {

			// Invoke the adaptTo method to create a Session used to create a
			// QueryManager
			ResourceResolver resourceResolver = resolverFactory
					.getAdministrativeResourceResolver(null);
			session = resourceResolver.adaptTo(Session.class);

			// Obtain the query manager for the session ...
			javax.jcr.query.QueryManager queryManager = session.getWorkspace()
					.getQueryManager();

			// Setup the quesry based on user input
			String sqlStatement = "";

			// Setup the query to get all customer records
			if (filter.equals("All Customers"))
				sqlStatement = "SELECT * FROM [nt:unstructured] WHERE CONTAINS(desc, 'Customer')";
			else if (filter.equals("Active Customer"))
				sqlStatement = "SELECT * FROM [nt:unstructured] WHERE CONTAINS(desc, 'Active')";
			else if (filter.equals("Past Customer"))
				sqlStatement = "SELECT * FROM [nt:unstructured] WHERE CONTAINS(desc, 'Past')";

			javax.jcr.query.Query query = queryManager.createQuery(
					sqlStatement, "JCR-SQL2");

			// Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();

			// Iterate over the nodes in the results ...
			javax.jcr.NodeIterator nodeIter = result.getNodes();

			while (nodeIter.hasNext()) {

				// For each node-- create a customer instance
				cust = new Customer();

				javax.jcr.Node node = nodeIter.nextNode();

				// Set all Customer object fields
				cust.setFirst(node.getProperty("firstName").getString());
				cust.setLast(node.getProperty("lastName").getString());
				cust.setAddress(node.getProperty("address").getString());
				cust.setDescription(node.getProperty("desc").getString());

				// Push Customer to the list
				custList.add(cust);
			}

			// Log out
			session.logout();
			return convertToString(toXml(custList));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
