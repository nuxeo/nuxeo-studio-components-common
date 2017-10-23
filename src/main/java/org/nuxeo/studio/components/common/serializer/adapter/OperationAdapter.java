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

package org.nuxeo.studio.components.common.serializer.adapter;

import org.nuxeo.studio.components.common.mapper.descriptors.OperationDescriptor;
import org.nuxeo.studio.components.common.serializer.adapter.automation.OperationDocumentation;
import org.nuxeo.studio.components.common.serializer.adapter.automation.OperationReader;

public class OperationAdapter implements SerializerAdapter<OperationDescriptor, OperationDocumentation> {

    @Override
    public OperationDocumentation adapt(OperationDescriptor src) {
        OperationReader or = OperationReader.read(src.type);
        OperationDocumentation doc = new OperationDocumentation(or.getId());
        doc.aliases = or.getAliases();
        doc.category = or.getCategory();
        doc.label = or.getLabel();
        doc.requires = or.getRequires();
        doc.signature = or.getSplittedSignatures();
        doc.since = or.getSince();
        doc.description = or.getDescription();
        doc.params = or.getParameters().toArray(new OperationDocumentation.Param[0]);
        return doc;
    }
}
