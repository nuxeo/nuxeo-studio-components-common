package org.nuxeo.studio.components.common.mapper.descriptors;

import java.util.HashSet;
import java.util.Set;

import org.nuxeo.studio.components.common.mapper.xmap.annotation.XNode;
import org.nuxeo.studio.components.common.mapper.xmap.annotation.XNodeList;
import org.nuxeo.studio.components.common.mapper.xmap.annotation.XObject;

/**
 * Repository proxies configuration descriptor.
 */
@XObject("proxies")
public class ProxiesDescriptor {

    @XNode("@type")
    private String type;

    @XNodeList(value = "schema@name", type = HashSet.class, componentType = String.class)
    private Set<String> schemas = new HashSet<String>(0);

    /* empty constructor needed by XMap */
    public ProxiesDescriptor() {
    }

    public String getType() {
        return type == null ? "*" : type;
    }

    public Set<String> getSchemas() {
        return schemas;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(type=" + getType() + ", schemas=" + getSchemas() + ")";
    }

}
