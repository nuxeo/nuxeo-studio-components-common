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
 *     Florian Bématol
 */
package org.nuxeo.studio.components.common.mapper.impl;

import org.nuxeo.studio.components.common.mapper.ExtensionMapper;
import org.nuxeo.studio.components.common.mapper.descriptors.I18nResourceDescriptor;

public class I18nResourcesMapper extends ExtensionMapper {
    @Override
    public void registerDescriptors() {
        registerDescriptor("i18ns", I18nResourceDescriptor.class);
    }

    @Override
    protected boolean accept(String target, String point) {
        return false;
    }
}