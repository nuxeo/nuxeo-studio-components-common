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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.studio.components.common.bundle.Extension;
import org.nuxeo.studio.components.common.mapper.xmap.Context;
import org.nuxeo.studio.components.common.mapper.xmap.XMap;

public abstract class ExtensionMapper {

    protected Map<String, List<Class<?>>> descriptors = new HashMap<>();

    protected List<Class<?>> bareDescriptors = new ArrayList<>();

    public ExtensionMapper() {
        registerDescriptors();
    }

    public abstract void registerDescriptors();

    protected abstract boolean accept(String target, String point);

    /**
     * Associated a Descriptor to a Studio registry
     * 
     * @param name
     * @param descriptor
     */
    public void registerDescriptor(String name, Class<?> descriptor) {
        descriptors.computeIfAbsent(name, s -> new ArrayList<>()).add(descriptor);
    }

    /**
     * Register descriptor that are not handled by Studio registries
     * 
     * @param descriptor
     */
    public void registerBareDescriptor(Class<?> descriptor) {
        bareDescriptors.add(descriptor);
    }

    public boolean accept(Extension ext) {
        return accept(ext.getTargetComponent(), ext.getExtensionPoint());
    }

    /**
     * Method used to be overriden in order to detect a disabled contribution
     *
     * @param contribution Object to check
     * @return false if the contribution is removing another one, true otherwise.
     */
    public boolean isEnabled(Object contribution) {
        return true;
    }

    /**
     * Method used to be overriden in order to detect a partial contribution
     *
     * @param contribution Object to check
     * @return true if the contribution is partial, false otherwise.
     */
    public boolean isPartial(Object contribution) {
        return false;
    }

    public Object[] loadAll(Context ctx, Extension extension) {
        XMap xmap = new XMap();
        getDescriptors().forEach(xmap::register);
        try {
            return xmap.loadAll(ctx, extension.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Class<?>> getDescriptors() {
        List<Class<?>> completeList = new ArrayList<>(bareDescriptors);
        descriptors.values().forEach(completeList::addAll);
        return completeList;
    }

    public List<Class<?>> getDescriptor(String name) {
        return descriptors.getOrDefault(name, null);
    }

    public Set<String> getDescriptorNames() {
        return descriptors.keySet();
    }

    public boolean contains(Class<?> descriptor) {
        return descriptors.values().stream().anyMatch(list -> list.contains(descriptor));
    }
}
