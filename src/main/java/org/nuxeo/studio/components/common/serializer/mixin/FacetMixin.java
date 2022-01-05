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

package org.nuxeo.studio.components.common.serializer.mixin;

import java.io.IOException;
import java.util.Arrays;

import org.nuxeo.studio.components.common.mapper.descriptors.SchemaDescriptor;
import org.nuxeo.studio.components.common.serializer.JacksonConverter.StudioJacksonSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class FacetMixin {
    @JsonProperty(value = "id")
    String name;

    @JsonProperty(value = "label")
    @JsonInclude(Include.NON_NULL)
    String label;

    @JsonProperty(value = "description")
    @JsonInclude(Include.NON_NULL)
    String description;

    @JsonProperty(value = "deprecated")
    @JsonInclude(Include.NON_NULL)
    Boolean deprecated;

    @JsonSerialize(using = FacetSchemaSerializer.class)
    SchemaDescriptor[] schemas;

    public static class FacetSchemaSerializer extends StudioJacksonSerializer<SchemaDescriptor[]> {
        @Override
        public void serialize(SchemaDescriptor[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeStartArray();
            Arrays.stream(value).forEach(s -> {
                try {
                    gen.writeString(s.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            gen.writeEndArray();
        }
    }
}
