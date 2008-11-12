package com.atlassian.sal.crowd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.salproperty.SALProperty;
import com.atlassian.crowd.model.salproperty.SALPropertyDAO;

public class TestServlet extends HttpServlet
{
	private SALPropertyDAO salPropertyDAO;
	private PlatformTransactionManager transactionManager;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		final String key = request.getParameter("key");
		final String propertyName = request.getParameter("propertyName");
		String content = null;
		
		if ("put".equals(request.getParameter("action")) && key!=null && propertyName!=null)
		{
			final SALProperty salProperty = new SALProperty(key, propertyName, request.getParameter("stringValue"));
			doInTransaction(new TransactionCallback()
			{
				public Object doInTransaction(TransactionStatus status)
				{
					return salPropertyDAO.saveOrUpdate(salProperty);
				}
			});
		} else if ("delete".equals(request.getParameter("action")) && key!=null && propertyName!=null)
		{
			doInTransaction(new TransactionCallback()
			{
				public Object doInTransaction(TransactionStatus status)
				{
					salPropertyDAO.remove(key, propertyName);
					return null;
				}
			});
		} else if (key!=null && propertyName!=null)
		{
			try
			{
				final SALProperty property = salPropertyDAO.find(key, propertyName);
				content = property.getStringValue();
			} catch (final DataAccessException e)
			{
				e.printStackTrace();
			} catch (final ObjectNotFoundException e)
			{
				content = "Not found";
			}
		}
		
		if (content == null) content = "Ahoj";
		final String html = "<html><body>"+content+"</body></html>";
    	response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(html.getBytes("UTF-8").length);
        response.getWriter().print(html);
	}


	private Object doInTransaction(TransactionCallback transactionCallback)
	{
        final TransactionTemplate txTemplate = new TransactionTemplate(transactionManager, getTransactionDefinition());
        return txTemplate.execute(transactionCallback);
	}

    protected DefaultTransactionDefinition getTransactionDefinition()
    {
        final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SAALReadWriteTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setReadOnly(false);
        return def;
    }

	
	public SALPropertyDAO getSALPropertyDAO()
	{
		return salPropertyDAO;
	}

	public void setSALPropertyDAO(SALPropertyDAO salPropertyDAO)
	{
		this.salPropertyDAO = salPropertyDAO;
	}


	public PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}


	public void setTransactionManager(PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}
}
