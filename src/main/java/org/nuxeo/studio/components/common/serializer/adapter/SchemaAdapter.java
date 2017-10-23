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

import org.nuxeo.studio.components.common.mapper.descriptors.SchemaBindingDescriptor;
import org.nuxeo.studio.components.common.serializer.adapter.schema.Schema;
import org.nuxeo.studio.components.common.serializer.adapter.schema.SimpleSchemaReader;

public class SchemaAdapter implements SerializerAdapter<SchemaBindingDescriptor, Schema> {

    @Override
    public Schema adapt(SchemaBindingDescriptor descriptor) {
        Schema schema = new Schema(descriptor.name, descriptor.prefix);
        SimpleSchemaReader reader = new SimpleSchemaReader(descriptor.src);
        reader.load();

        schema.addFields(reader.getFields());
        return schema;
    }

}
