/*
 * (C) Copyright 2022 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Florian BEMATOL
 */

package org.nuxeo.studio.components.common.serializer.mixin;

import java.io.IOException;

import org.nuxeo.studio.components.common.mapper.descriptors.LoginScreenDescriptor;
import org.nuxeo.studio.components.common.serializer.JacksonConverter.StudioJacksonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = LoginScreenMixin.LoginScreenSerializer.class)
public abstract class LoginScreenMixin {

    public static class LoginScreenSerializer extends StudioJacksonSerializer<LoginScreenDescriptor> {

        private static final long serialVersionUID = 1L;

        @Override
        public void serialize(LoginScreenDescriptor value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeBooleanField("isPresent", true);
        }
    }
}
