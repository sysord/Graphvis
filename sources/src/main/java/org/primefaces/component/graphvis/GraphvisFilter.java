package org.primefaces.component.graphvis;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;

/**
 * Filter for redirect request made by Cytoscape for get swf components
 *
 */
public class GraphvisFilter implements Filter{


	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest srequest = (HttpServletRequest) request;
        HttpServletResponse sresponse = (HttpServletResponse) response; 
               
        String url = HttpUtils.getRequestURL(srequest).toString();
        if(url.contains(".jsf.swf")){
            String redirectUrl = url.replace(".jsf.swf", ".jsf") + "?" + srequest.getQueryString();
        	sresponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        	sresponse.sendRedirect(redirectUrl);
        }else{
        	chain.doFilter(request, response);
        }
	}
	
	@Override
	public void destroy() {
	}


}
