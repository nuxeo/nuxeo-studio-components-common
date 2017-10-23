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

package org.nuxeo.studio.components.common.mapper.impl;

import org.nuxeo.studio.components.common.mapper.ExtensionMapper;
import org.nuxeo.studio.components.common.mapper.descriptors.DocumentTypeDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.FacetDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.SchemaBindingDescriptor;

import java.util.Arrays;
import java.util.List;

/**
 * Can contain: - doctype - schemas - facets
 */
public class TypeServiceMapper extends ExtensionMapper {

    protected static final List<String> extensionPoints = Arrays.asList("doctype", "schema", "configuration");

    @Override
    public void registerDescriptors() {
        registerDescriptor("facets", FacetDescriptor.class);
        registerDescriptor("doctypes", DocumentTypeDescriptor.class);
        registerDescriptor("schemas", SchemaBindingDescriptor.class);
    }

    @Override
    public boolean accept(String target, String point) {
        return "org.nuxeo.ecm.core.schema.TypeService".equals(target) && extensionPoints.contains(point);
    }

    @Override
    public boolean isPartial(Object contribution) {
        if (contribution instanceof DocumentTypeDescriptor) {
            return ((DocumentTypeDescriptor) contribution).append;
        }
        return false;
    }
}
