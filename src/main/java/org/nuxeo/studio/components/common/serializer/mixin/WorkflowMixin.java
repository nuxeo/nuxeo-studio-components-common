/*
 * (C) Copyright 2021 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Yannis JULIENNE
 */

package org.nuxeo.studio.components.common.serializer.mixin;

import java.io.IOException;

import org.nuxeo.studio.components.common.mapper.descriptors.WorkflowDescriptor;
import org.nuxeo.studio.components.common.serializer.JacksonConverter.StudioJacksonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = WorkflowMixin.WorkflowSerializer.class)
public abstract class WorkflowMixin {

    public static class WorkflowSerializer extends StudioJacksonSerializer<WorkflowDescriptor> {

        private static final long serialVersionUID = 1L;

        @Override
        public void serialize(WorkflowDescriptor value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(value.id);
        }
    }
}
