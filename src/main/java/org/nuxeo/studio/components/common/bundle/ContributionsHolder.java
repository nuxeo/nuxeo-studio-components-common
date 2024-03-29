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

package org.nuxeo.studio.components.common.bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.nuxeo.studio.components.common.mapper.MappersManager;
import org.nuxeo.studio.components.common.mapper.descriptors.ResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.impl.AutomationMapper;
import org.nuxeo.studio.components.common.mapper.impl.CSVResourcesMapper;
import org.nuxeo.studio.components.common.mapper.impl.DirectoryMapper;
import org.nuxeo.studio.components.common.mapper.impl.DocTemplateMapper;
import org.nuxeo.studio.components.common.mapper.impl.EventMapper;
import org.nuxeo.studio.components.common.mapper.impl.I18nResourcesMapper;
import org.nuxeo.studio.components.common.mapper.impl.ImageResourcesMapper;
import org.nuxeo.studio.components.common.mapper.impl.LifeCycleMapper;
import org.nuxeo.studio.components.common.mapper.impl.LoginScreenMapper;
import org.nuxeo.studio.components.common.mapper.impl.MailTemplateMapper;
import org.nuxeo.studio.components.common.mapper.impl.OpRestBindingMapper;
import org.nuxeo.studio.components.common.mapper.impl.PageProviderMapper;
import org.nuxeo.studio.components.common.mapper.impl.PermissionsMapper;
import org.nuxeo.studio.components.common.mapper.impl.SearchFormMapper;
import org.nuxeo.studio.components.common.mapper.impl.StructureTemplateMapper;
import org.nuxeo.studio.components.common.mapper.impl.TypeServiceMapper;
import org.nuxeo.studio.components.common.mapper.impl.VocabularyMapper;
import org.nuxeo.studio.components.common.mapper.impl.WorkflowMapper;
import org.nuxeo.studio.components.common.mapper.impl.XSDResourcesMapper;

/**
 * Contributions Holder is aim to map and track all visited contributions.
 * <p>
 * This Holder is based on a {@link MappersManager} to map all known contributions to his Descriptor object.
 * </p>
 */
public class ContributionsHolder {
    /**
     * Contains a map of all contributions per contributions descriptor class.
     */
    protected final Map<String, List<Object>> contributions = new HashMap<>();

    protected final MappersManager manager;

    public ContributionsHolder() {
        this(MappersManager.instance()
                           .add(new TypeServiceMapper())
                           .add(new PermissionsMapper())
                           .add(new AutomationMapper())
                           .add(new LifeCycleMapper())
                           .add(new EventMapper())
                           .add(new VocabularyMapper())
                           .add(new WorkflowMapper())
                           .add(new MailTemplateMapper())
                           .add(new DocTemplateMapper())
                           .add(new PageProviderMapper())
                           .add(new DirectoryMapper())
                           .add(new OpRestBindingMapper())
                           .add(new ImageResourcesMapper())
                           .add(new CSVResourcesMapper())
                           .add(new I18nResourcesMapper())
                           .add(new XSDResourcesMapper())
                           .add(new LoginScreenMapper())
                           .add(new SearchFormMapper())
                           .add(new StructureTemplateMapper()));
    }

    public ContributionsHolder(MappersManager manager) {
        this.manager = manager;
    }

    public MappersManager getManager() {
        return manager;
    }

    public void load(RegistrationInfo ri) {
        ri.getExtensions().stream().map(manager::load).forEach(a -> Arrays.stream(a).forEach(c -> {
            List<Object> sortedContributions = contributions.computeIfAbsent(c.getClass().getName(),
                    s -> new ArrayList<>());
            sortedContributions.add(c);
        }));
    }

    public void load(ResourceDescriptor rd) {
        List<Object> sortedContributions = contributions.computeIfAbsent(rd.getClass().getName(),
                s -> new ArrayList<>());
        sortedContributions.add(rd);
    }

    /**
     * Get all contributions based on this descriptor; filtering the ones that target to be deleted or partial.
     *
     * @param descriptor gets contributions of the same class
     * @param <T> Class of the descriptor
     * @return a list of all contributions or an empty list.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getContributions(Class<T> descriptor) {
        List<T> list = new ArrayList<>();
        for (Object contribution : contributions.getOrDefault(descriptor.getName(), Collections.emptyList())) {
            if (manager.isSerializable(descriptor, contribution)) {
                list.add((T) contribution);
            }
        }
        return list;
    }

    /**
     * Remove all contributions based on this descriptor.
     *
     * @param <T> Class of the descriptor
     * @param descriptor
     * @return a list of removed contributions or an empty list.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> removeContributions(Class<T> descriptor) {
        return Optional.ofNullable((List<T>) contributions.remove(descriptor.getName()))
                       .orElse(Collections.emptyList());
    }

    /**
     * Remove all contributions based on this descriptor that satisfy the given predicate.
     *
     * @param <T> Class of the descriptor
     * @param descriptor
     * @param filter a predicate which returns true for elements to be removed
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> boolean removeContributions(Class<T> descriptor, Predicate<? super T> filter) {
        return ((Collection<T>) contributions.getOrDefault(descriptor.getName(), Collections.emptyList())).removeIf(
                filter);
    }
}
