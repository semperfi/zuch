/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zuch.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author HP 2000
 */
public class HeaderFilter implements Filter {

	@Override
	public void destroy() {
		
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
           
	      request.setCharacterEncoding("UTF-8");
	      response.setCharacterEncoding("UTF-8");
                                 
             // HttpServletResponse httpServletResponse = (HttpServletResponse) response;
               // Set standard HTTP/1.1 no-cache headers.
             // httpServletResponse.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		
	      chain.doFilter(request, response);
		
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}