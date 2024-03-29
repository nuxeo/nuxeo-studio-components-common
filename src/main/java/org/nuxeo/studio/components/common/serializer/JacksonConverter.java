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
 *     Florian BEMATOL
 */

package org.nuxeo.studio.components.common.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.studio.components.common.ExtractorOptions;
import org.nuxeo.studio.components.common.mapper.descriptors.DirectoryDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.DocTemplateDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.DocumentTypeDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.EventHandlerDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.EventListenerDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.FacetDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.LifeCycleDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.LoginScreenDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.MailTemplateDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.OpRestBindingDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.PageProviderDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.PermissionDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.SearchFormDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.StructureTemplateDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.WorkflowDescriptor;
import org.nuxeo.studio.components.common.serializer.adapter.DefaultAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.DirectoryAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.OperationAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.OperationChainAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.OperationScriptingAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.SchemaAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.SerializerAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.VocabularyAdapter;
import org.nuxeo.studio.components.common.serializer.adapter.automation.OperationDocumentation;
import org.nuxeo.studio.components.common.serializer.adapter.schema.Schema;
import org.nuxeo.studio.components.common.serializer.mixin.DirectoryMixin;
import org.nuxeo.studio.components.common.serializer.mixin.DocTemplateMixin;
import org.nuxeo.studio.components.common.serializer.mixin.DocTypeMixin;
import org.nuxeo.studio.components.common.serializer.mixin.EventHandlerMixin;
import org.nuxeo.studio.components.common.serializer.mixin.EventListenerMixin;
import org.nuxeo.studio.components.common.serializer.mixin.FacetMixin;
import org.nuxeo.studio.components.common.serializer.mixin.LifeCycleMixin;
import org.nuxeo.studio.components.common.serializer.mixin.LoginScreenMixin;
import org.nuxeo.studio.components.common.serializer.mixin.MailTemplateMixin;
import org.nuxeo.studio.components.common.serializer.mixin.OpRestBindingMixin;
import org.nuxeo.studio.components.common.serializer.mixin.OperationDocumentationMixin;
import org.nuxeo.studio.components.common.serializer.mixin.PageProviderMixin;
import org.nuxeo.studio.components.common.serializer.mixin.PermissionMixin;
import org.nuxeo.studio.components.common.serializer.mixin.SchemaMixin;
import org.nuxeo.studio.components.common.serializer.mixin.SearchFormMixin;
import org.nuxeo.studio.components.common.serializer.mixin.StructureTemplateMixin;
import org.nuxeo.studio.components.common.serializer.mixin.WorkflowMixin;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
        registerAdapter(VocabularyAdapter.class);
        registerAdapter(DirectoryAdapter.class);

        // Mixins allow to define the way the serialization is done
        registerMixin(FacetDescriptor.class, FacetMixin.class);
        registerMixin(PermissionDescriptor.class, PermissionMixin.class);
        registerMixin(OperationDocumentation.class, OperationDocumentationMixin.class);
        registerMixin(LifeCycleDescriptor.class, LifeCycleMixin.class);
        registerMixin(EventListenerDescriptor.class, EventListenerMixin.class);
        registerMixin(Schema.class, SchemaMixin.class);
        registerMixin(DocumentTypeDescriptor.class, DocTypeMixin.class);
        registerMixin(MailTemplateDescriptor.class, MailTemplateMixin.class);
        registerMixin(DocTemplateDescriptor.class, DocTemplateMixin.class);
        registerMixin(WorkflowDescriptor.class, WorkflowMixin.class);
        registerMixin(PageProviderDescriptor.class, PageProviderMixin.class);
        registerMixin(DirectoryDescriptor.class, DirectoryMixin.class);
        registerMixin(OpRestBindingDescriptor.class, OpRestBindingMixin.class);
        registerMixin(LoginScreenDescriptor.class, LoginScreenMixin.class);
        registerMixin(SearchFormDescriptor.class, SearchFormMixin.class);
        registerMixin(EventHandlerDescriptor.class, EventHandlerMixin.class);
        registerMixin(StructureTemplateDescriptor.class, StructureTemplateMixin.class);
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

    public String serialize(Object target, boolean simulateWrapInObject) {
        try {
            ObjectMapper om = new ObjectMapper();
            Object targetAdapted = adapters.getOrDefault(target.getClass(), defaultAdapter).adapt(target);


            if (targetAdapted == null) {
                // Mainly occurred when adapter filter out the target, just ignoring
                return null;
            }

            log.info("Serialize: " + targetAdapted.toString().trim());
            om.addMixIn(targetAdapted.getClass(), mixins.getOrDefault(targetAdapted.getClass(), Object.class));
            StringWriter sw = new StringWriter();
            JsonGenerator gen = om.getFactory().createGenerator(sw);
            if (simulateWrapInObject) {
                gen.writeStartObject();
            }
            om.writeValue(gen, targetAdapted);
            String result = sw.toString();
            if (simulateWrapInObject) {
                result = StringUtils.removeStart(result, "{");
            }
            return result;
        } catch (IOException e) {
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
