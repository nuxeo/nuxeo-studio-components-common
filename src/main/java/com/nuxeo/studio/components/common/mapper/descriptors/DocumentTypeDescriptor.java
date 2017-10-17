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

/**
 * Document Type Descriptor.
 * <p>
 * Can be used to delay document type registration when not all prerequisites are met (e.g. supertype was not yet
 * registered).
 * <p>
 * In this case the descriptor containing all the information needed to register the document is put in a queue waiting
 * for the prerequisites to be met.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@XObject("doctype")
public class DocumentTypeDescriptor {

    @XNode("@name")
    public String name;

    @XNodeList(value = "schema", type = SchemaDescriptor[].class, componentType = SchemaDescriptor.class)
    public SchemaDescriptor[] schemas;

    @XNode("@extends")
    public String superTypeName;

    @XNodeList(value = "facet@name", type = String[].class, componentType = String.class)
    public String[] facets;

    @XNode("@append")
    public boolean append = false;

    @XNodeList(value = "subtypes/type", type = String[].class, componentType = String.class)
    public String[] subtypes = new String[0];

    @XNodeList(value = "subtypes-forbidden/type", type = String[].class, componentType = String.class)
    public String[] forbiddenSubtypes = new String[0];

    @Override
    public String toString() {
        return "DocType: " + name;
    }
}
