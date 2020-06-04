package pe.gob.sunat.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Clase : FilterAbstract
 * Proyecto : Framework
 * Descripcion : 
 * Fecha : 26/09/2005
 * @author CGARRATT modifyBy JVALDEZ
 */
public abstract class FilterAbstract implements Filter {
  /**
   * 
   */
  protected final Log log = LogFactory.getLog(getClass());
  /**
   * 
   */
  private FilterConfig filterConfig;
  /**
   * 
   */
  private ServletContext servletContext;
  /**
   * 
   * @param fc
   */
  public void setFilterConfig(FilterConfig fc) {
    filterConfig = fc;		
  }
  /**
   * 
   * @return
   */
  public FilterConfig getFilterConfig() {
    return filterConfig;
  }

  /**
   * 
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    preProcesa(request, response, chain);
    procesa(request, response, chain);
    postProcesa(request, response, chain);
  }
  
  /**
   * 
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  public abstract void preProcesa(ServletRequest request,
      ServletResponse response, FilterChain chain) throws IOException, ServletException;
  
  /**
   * 
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  public abstract void postProcesa(ServletRequest request,
      ServletResponse response, FilterChain chain) throws IOException, ServletException;		
  /**
   * 
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  public abstract void procesa(ServletRequest request,
      ServletResponse response, FilterChain chain) throws IOException, ServletException;
  /**
   * 
   */
  public void init(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    this.servletContext = filterConfig.getServletContext();
  }
  /**
   * 
   */
  public void destroy() {		
    this.filterConfig = null;
  }
  /**
   * 
   * @return
   */
  public ServletContext getServletContext() {
    return servletContext;
  }
  /**
   * 
   * @param servletContext
   */
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }
}