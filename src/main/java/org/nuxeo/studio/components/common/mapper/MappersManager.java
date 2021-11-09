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

package org.nuxeo.studio.components.common.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.nuxeo.studio.components.common.bundle.Extension;
import org.nuxeo.studio.components.common.mapper.xmap.XMap;
import org.nuxeo.studio.components.common.runtime.ExtractorContext;

/**
 * Mappers Manager holding all registered {@link ExtensionMapper} and can load an {@link Extension} to his Descriptor
 * class using {@link XMap} loader.
 */
public class MappersManager {
    private List<ExtensionMapper> mappers;

    public MappersManager() {
        mappers = new ArrayList<>();
    }

    public static MappersManager instance() {
        return new MappersManager();
    }

    public MappersManager add(ExtensionMapper transformer) {
        mappers.add(transformer);
        return this;
    }

    public boolean accept(Extension ext) {
        return mappers.stream().anyMatch(s -> s.accept(ext));
    }

    public boolean isSerializable(Class<?> descriptor, Object contribution) {
        ExtensionMapper mapper = mappers.stream() //
                                        .filter(s -> s.contains(descriptor))
                                        .findFirst()
                                        .orElseThrow(RuntimeException::new);

        return mapper.isEnabled(contribution) && !mapper.isPartial(contribution);
    }

    public List<Class<?>> getDescriptors(String name) {
        return mappers.stream() //
                      .map(m -> m.getDescriptor(name))
                      .filter(Objects::nonNull)
                      .findFirst()
                      .orElse(new ArrayList<>());
    }

    public String getDescriptorName(Class<?> klass) {
        return mappers.stream() //
                      .map(s -> s.descriptors)
                      .map(Map::entrySet)
                      .collect(ArrayList<Map.Entry<String, List<Class<?>>>>::new, ArrayList::addAll, ArrayList::addAll)
                      .stream()
                      .filter(s -> s.getValue().contains(klass))
                      .findFirst()
                      .map(Map.Entry::getKey)
                      .orElse(null);
    }

    public Object[] load(Extension ext) {
        List<Object> loaded = new ArrayList<>();
        mappers.stream() // we go through every mappers accepting the extension in case there are multiple mappers
                         // accepting the same extension (i.e directories and vocabularies)
               .filter(s -> s.accept(ext))
               .forEach(mapper -> {
                   loaded.addAll(Arrays.asList(mapper.loadAll(ExtractorContext.instance, ext)));
               });

        return loaded.toArray();
    }

    public String[] getRegisteredTargets() {
        return mappers.stream() //
                      .map(ExtensionMapper::getDescriptorNames)
                      .collect(ArrayList<String>::new, ArrayList::addAll, ArrayList::addAll)
                      .toArray(new String[0]);
    }
}
