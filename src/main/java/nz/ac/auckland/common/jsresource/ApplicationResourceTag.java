package nz.ac.auckland.common.jsresource;

import nz.ac.auckland.lmz.common.AppVersion;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Writer;

/**
 * @author: Richard Vowles - https://plus.google.com/+RichardVowles
 */
public class ApplicationResourceTag extends SimpleTagSupport {
	@Inject
	AppVersion version;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();

		JspWriter out = pageContext.getOut();

		ServletContext servletContext = pageContext.getServletContext();
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, servletContext);

		String contextPath = servletContext.getContextPath();

		out.print(String.format("<script src='%s/app-resources/%s/global.js'></script>", contextPath, version.getVersion()));
		out.print(String.format("<script src='%s/app-resources/%s/session.js'></script>", contextPath, version.getVersion()));
	}
}
