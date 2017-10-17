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

package com.nuxeo.studio.components.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.nuxeo.studio.components.common.bundle.BundleWalker;
import com.nuxeo.studio.components.common.bundle.ContributionsHolder;
import com.nuxeo.studio.components.common.bundle.Extension;
import com.nuxeo.studio.components.common.bundle.RegistrationInfo;
import com.nuxeo.studio.components.common.mapper.MappersManager;
import com.nuxeo.studio.components.common.mapper.descriptors.DocumentTypeDescriptor;
import com.nuxeo.studio.components.common.mapper.descriptors.SchemaBindingDescriptor;
import com.nuxeo.studio.components.common.mapper.impl.TypeServiceMapper;

public class TestWalker extends AbstractExtractorTest {

    @Test
    public void readComponent() throws URISyntaxException {
        RegistrationInfo ri = getRegistrationInfo("component-contrib.xml");

        assertEquals("org.nuxeo.ecm.platform.picture.coreTypes", ri.getName());
        assertEquals(3, ri.getExtensions().size());
        Extension extension = ri.getExtensions().get(0);

        assertEquals("schema", extension.getExtensionPoint());
        assertEquals("org.nuxeo.ecm.core.schema.TypeService", extension.getTargetComponent());
    }

    @Test
    public void testReadExtension() throws URISyntaxException {
        MappersManager manager = new MappersManager();
        manager.add(new TypeServiceMapper());

        List<Class<?>> descriptors = manager.getDescriptors("doctypes");
        assertEquals(1, descriptors.size());
        assertEquals(DocumentTypeDescriptor.class, descriptors.get(0));

        RegistrationInfo ri = getRegistrationInfo("component-contrib.xml");
        assertTrue(ri.getExtensions().stream().anyMatch(manager::accept));
    }

    @Test
    public void testContributionsHolder() throws URISyntaxException {
        RegistrationInfo ri = getRegistrationInfo("component-contrib.xml");

        ContributionsHolder holder = new ContributionsHolder();
        holder.load(ri);

        List<SchemaBindingDescriptor> contributions = holder.getContributions(SchemaBindingDescriptor.class);
        assertEquals(2, contributions.size());
    }

    @Test
    public void walkerTest() throws IOException {
        assertNotNull(walker.getManifest());

        List<Path> components = walker.getComponents().collect(Collectors.toList());
        assertEquals(3, components.size());

        RegistrationInfo ri = walker.read(components.get(0));
        assertNotNull(ri);
    }

    @Test
    public void jarFileWalkerTest() throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource("test-project-core-1.0-SNAPSHOT.jar");
        assertNotNull(resource);
        URI uri = URI.create("jar:file:" + resource.getFile());

        try (FileSystem jfs = FileSystems.newFileSystem(uri, new HashMap<>())) {
            BundleWalker walker = new BundleWalker(jfs.getPath("/"));
            assertEquals(3, walker.getComponents().count());

            RegistrationInfo ri = walker.getRegistrationInfos().findFirst().orElse(null);
            assertNotNull(ri);
            assertEquals("org.nuxeo.platform.audit.lifecycle.contrib", ri.getName());
        }
    }
}
