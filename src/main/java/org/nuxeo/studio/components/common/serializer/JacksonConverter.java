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

package org.nuxeo.studio.components.common.serializer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.studio.components.common.ExtractorOptions;
import org.nuxeo.studio.components.common.mapper.descriptors.DocumentTypeDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.EventListenerDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.FacetDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.LifeCycleDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.PermissionDescriptor;
import org.nuxeo.studio.components.common.serializer.adapter.DefaultAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.OperationAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.OperationChainAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.OperationScriptingAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.SchemaAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.SerializerAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.automation.OperationDocumentation;
import org.nuxeo.studio.components.common.serializer.adapter.schema.Schema;
import org.nuxeo.studio.components.common.serializer.mixin.DocTypeMixin;
import org.nuxeo.studio.components.common.serializer.mixin.EventListenerMixin;
import org.nuxeo.studio.components.common.serializer.mixin.FacetMixin;
import org.nuxeo.studio.components.common.serializer.mixin.LifeCycleMixin;
import org.nuxeo.studio.components.common.serializer.mixin.OperationDocumentationMixin;
import org.nuxeo.studio.components.common.serializer.mixin.PermissionMixin;
import org.nuxeo.studio.components.common.serializer.mixin.SchemaMixin;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JacksonConverter {

    private static final Log log = LogFactory.getLog(JacksonConverter.class);

    protected Map<Class<?>, Class<?>> mixins = new HashMap<>();

    protected Map<Class<?>, SerializerAdapter> adapters = new HashMap<>();

    protected SerializerAdapter defaultAdapter = new DefaultAdapter();

    protected ExtractorOptions options;

    private JacksonConverter(ExtractorOptions options) {
        this.options = options;

        // Adapters aim to adapt descriptor to a more specific object
        registerAdapter(OperationAdapter.class);
        registerAdapter(SchemaAdapter.class);
        registerAdapter(OperationChainAdapter.class);
        registerAdapter(OperationScriptingAdapter.class);

        // Mixins allow to define the way the serialization is done
        registerMixin(FacetDescriptor.class, FacetMixin.class);
        registerMixin(PermissionDescriptor.class, PermissionMixin.class);
        registerMixin(OperationDocumentation.class, OperationDocumentationMixin.class);
        registerMixin(LifeCycleDescriptor.class, LifeCycleMixin.class);
        registerMixin(EventListenerDescriptor.class, EventListenerMixin.class);
        registerMixin(Schema.class, SchemaMixin.class);
        registerMixin(DocumentTypeDescriptor.class, DocTypeMixin.class);
    }

    public static JacksonConverter instance() {
        return new JacksonConverter(ExtractorOptions.DEFAULT);
    }

    public static JacksonConverter instance(ExtractorOptions options) {
        return new JacksonConverter(options);
    }

    protected void registerMixin(Class<?> target, Class<?> mixin) {
        mixins.put(target, mixin);
    }

    protected void registerAdapter(Class<? extends SerializerAdapter> serializer) {
        try {
            Class type = (Class) ((ParameterizedType) serializer.getGenericInterfaces()[0]).getActualTypeArguments()[0];
            adapters.put(type, serializer.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String serialize(Object target) {
        try {
            ObjectMapper om = new ObjectMapper();
            Object targetAdapted = adapters.getOrDefault(target.getClass(), defaultAdapter).adapt(target);

            // Mainly occurred with {@code SchemaAdapter#adapt} when schema file
            // is
            // missing.
            if (targetAdapted == null) {
                log.warn("Unable to adapt: \"" + target + "\" (" + target.getClass() + ")");
                return null;
            }

            log.info("Serialize: " + targetAdapted.toString().trim());
            om.addMixIn(targetAdapted.getClass(), mixins.getOrDefault(targetAdapted.getClass(), Object.class));
            return om.writeValueAsString(targetAdapted);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void newGlobalStudioObject(OutputStream os, Map<String, String> serialized) {
        JsonFactory factory = new JsonFactory();

        if (options.isFailOnEmpty() && serialized.values().stream().allMatch(Objects::isNull)) {
            throw new RuntimeException("Nothing to export.");
        }

        try (JsonGenerator gen = factory.createGenerator(os, JsonEncoding.UTF8)) {
            gen.writeStartObject();
            for (String k : serialized.keySet()) {
                // XXX NXS-4051: Skip empty object, to prevent from overriding existing data from now. To remove when
                // Studio will handle registries merge
                String value = serialized.get(k);
                if (StringUtils.isBlank(value)) {
                    continue;
                }

                gen.writeFieldName(k);
                gen.writeRawValue(value);
            }
            gen.writeEndObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static abstract class StudioJacksonSerializer<T> extends StdSerializer<T> {
        /**
         * An empty constructor is required by Jackson
         */
        public StudioJacksonSerializer() {
            this(null);
        }

        protected StudioJacksonSerializer(Class<T> t) {
            super(t);
        }
    }
}
