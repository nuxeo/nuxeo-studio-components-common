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

package org.nuxeo.studio.components.common.serializer.adapter;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.studio.components.common.mapper.descriptors.VocabularyDescriptor;
import org.nuxeo.studio.components.common.serializer.adapter.vocabulary.Vocabulary;

public class VocabularyAdapter implements SerializerAdapter<VocabularyDescriptor, Vocabulary> {

    @Override
    public Vocabulary adapt(VocabularyDescriptor descriptor) {
        // adapt only vocabularies, not other directories
        if (StringUtils.equals(descriptor.extendz, "template-vocabulary")
                || StringUtils.equals(descriptor.extendz, "template-xvocabulary")) {
            String parent = descriptor.parentDirectory;
            String id = descriptor.name;
            boolean hierarchical = StringUtils.equals(parent, id);
            return new Vocabulary(parent, id, hierarchical);
        }
        return null;
    }

}
