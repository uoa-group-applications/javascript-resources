package nz.ac.auckland.common.jsresource;

import nz.ac.auckland.common.config.ConfigKey;
import nz.ac.auckland.common.stereotypes.UniversityComponent;

/**
 * @author Kefeng Deng (deng@51any.com)
 */
@UniversityComponent
public class ResourceNamespace {

	@ConfigKey("lmz.namespace")
	protected String namespace = "UOA";

	public String getNamespace() {
		return namespace;
	}

}
