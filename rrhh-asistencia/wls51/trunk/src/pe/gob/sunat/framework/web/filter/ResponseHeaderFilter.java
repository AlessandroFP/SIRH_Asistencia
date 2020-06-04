package pe.gob.sunat.framework.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResponseHeaderFilter extends FilterAbstract{

	private static final Log log = LogFactory.getLog(ResponseHeaderFilter.class);
	
	public void preProcesa(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException{				
	}
	
	public void postProcesa(ServletRequest request, ServletResponse response, FilterChain chain) 
		throws IOException, ServletException{		
	}
	
	public void procesa(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {		
		try{		
			if (res instanceof HttpServletResponse) {
				HttpServletResponse response = (HttpServletResponse) res;
				log.debug("e : "+getFilterConfig().getInitParameterNames());
				// set the provided HTTP response parameters
				String headerName;
				for (Enumeration e = getFilterConfig().getInitParameterNames(); e.hasMoreElements();) {				
					headerName = (String) e.nextElement();
					log.debug("headerName : "+headerName);
					response.addHeader(headerName, getFilterConfig().getInitParameter(headerName));
				}	
			}
			chain.doFilter(req,res);
		}
		catch(Exception e){
			log.error("*** Error ***",e);
		}
	}

}
