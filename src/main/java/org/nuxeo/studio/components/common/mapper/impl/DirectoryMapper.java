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

package org.nuxeo.studio.components.common.mapper.impl;

import java.util.Arrays;
import java.util.List;

import org.nuxeo.studio.components.common.mapper.ExtensionMapper;
import org.nuxeo.studio.components.common.mapper.descriptors.DirectoryDescriptor;

public class DirectoryMapper extends ExtensionMapper {

    protected static final List<String> TARGETS = Arrays.asList("org.nuxeo.ecm.directory.GenericDirectory",
            "org.nuxeo.ecm.directory.sql.SQLDirectoryFactory");

    @Override
    public void registerDescriptors() {
        registerDescriptor("directories", DirectoryDescriptor.class);
    }

    @Override
    protected boolean accept(String target, String point) {
        return TARGETS.contains(target) && "directories".equals(point);
    }
}
