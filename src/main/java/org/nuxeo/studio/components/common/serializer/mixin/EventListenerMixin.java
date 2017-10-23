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

import static org.nuxeo.studio.components.common.mapper.impl.EventMapper.SYSTEM_EVENTS;
import static org.nuxeo.studio.components.common.serializer.SerializerHelper.humanize;

import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nuxeo.studio.components.common.mapper.descriptors.EventListenerDescriptor;
import org.nuxeo.studio.components.common.serializer.JacksonConverter;

@JsonSerialize(using = EventListenerMixin.EventSerializer.class)
public abstract class EventListenerMixin {
    public static class EventSerializer extends JacksonConverter.StudioJacksonSerializer<EventListenerDescriptor> {

        @Override
        public void serialize(EventListenerDescriptor value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            // Export only not system's events
            String events = value.getEvents()
                                 .stream()
                                 .filter(e -> !SYSTEM_EVENTS.contains(e))
                                 .map(e -> String.format("\"%s\":\"%s\"", e, humanize(e)))
                                 .collect(Collectors.joining(","));

            gen.writeRaw(events);
        }
    }
}
