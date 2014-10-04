package nz.ac.auckland.common.jsresource;

import nz.ac.auckland.common.config.ConfigKey;
import nz.ac.auckland.common.stereotypes.UniversityComponent;
import org.eclipse.jetty.util.StringUtil;

/**
 * @author Kefeng Deng (deng@51any.com)
 */
@UniversityComponent
public class ResourceNamespace {

	public static final String DEFAULT_RESOURCE_NAMESAPCE = "UOA";

	/**
	 * The global namespace of javascript resources
	 */
	@ConfigKey("lmz.namespace")
	protected String namespace = DEFAULT_RESOURCE_NAMESAPCE;

	public String getNamespace() {
		if (StringUtil.isNotBlank(namespace)) {
			return namespace;
		}
		return DEFAULT_RESOURCE_NAMESAPCE;
	}

}
