/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Arnaud Kervern
 */
package com.nuxeo.studio.components.common.mapper.descriptors;

import com.nuxeo.studio.components.common.mapper.xmap.annotation.XNode;
import com.nuxeo.studio.components.common.mapper.xmap.annotation.XNodeList;
import com.nuxeo.studio.components.common.mapper.xmap.annotation.XObject;
import com.nuxeo.studio.components.common.serializer.adapter.automation.OperationDocumentation;

@XObject("scriptedOperation")
public class OperationScriptingDescriptor {

    @XNode("@id")
    protected String id;

    @XNode("inputType")
    protected String inputType;

    @XNode("outputType")
    protected String outputType;

    @XNode("description")
    protected String description;

    @XNode("category")
    protected String category;

    @XNodeList(value = "aliases/alias", type = String[].class, componentType = String.class)
    protected String[] aliases;

    @XNodeList(value = "param", type = OperationDocumentation.Param[].class, componentType = OperationDocumentation.Param.class)
    protected OperationDocumentation.Param[] params = new OperationDocumentation.Param[0];

    @XNode("script")
    protected String source;

    public String[] getAliases() {
        return aliases;
    }

    public String getInputType() {
        return inputType;
    }

    public String getOutputType() {
        return outputType;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public OperationDocumentation.Param[] getParams() {
        return params;
    }

}
