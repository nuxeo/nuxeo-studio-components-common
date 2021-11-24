/*
 * (C) Copyright 2019 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.studio.components.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nuxeo.studio.components.common.bundle.ContributionsHolder;
import org.nuxeo.studio.components.common.mapper.descriptors.CSVResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.I18nResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.ImageResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.OperationDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.XSDResourceDescriptor;

public class TestContributionsLoader extends AbstractExtractorTest {
    public static final String JAR_FILE = "src/test/resources/test-project-core-1.0-SNAPSHOT.jar";

    protected ContributionsHolder holder;

    @Before
    public void before() {
        holder = new ContributionsHolder();
    }

    @Test
    public void testLoad() {
        opts.jarFile = JAR_FILE;
        new ContributionsLoader(holder, opts).load();
        List<OperationDescriptor> contributions = holder.getContributions(OperationDescriptor.class);
        assertThat(contributions).hasSize(1);
    }

    @Test
    public void loadFromJarFile() {
        new ContributionsLoader(holder, opts).loadFromJarFile(new File(JAR_FILE).toURI());
        List<OperationDescriptor> contributions = holder.getContributions(OperationDescriptor.class);
        assertThat(contributions).hasSize(1);
        List<ImageResourceDescriptor> imgResourceDescriptors = holder.getContributions(ImageResourceDescriptor.class);
        assertThat(imgResourceDescriptors).hasSize(1);
    }

    @Test
    public void loadFromDirectory() {
        new ContributionsLoader(holder, opts).loadFromDirectory("src/test/resources");

        List<OperationDescriptor> contributions = holder.getContributions(OperationDescriptor.class);
        assertThat(contributions).hasSize(1);

        List<ImageResourceDescriptor> imgResourceDescriptors = holder.getContributions(ImageResourceDescriptor.class);
        assertThat(imgResourceDescriptors).hasSize(4);

        List<CSVResourceDescriptor> csvResourceDescriptors = holder.getContributions(CSVResourceDescriptor.class);
        assertThat(csvResourceDescriptors).hasSize(4);

        List<XSDResourceDescriptor> xsdResourceDescriptors = holder.getContributions(XSDResourceDescriptor.class);
        assertThat(xsdResourceDescriptors).hasSize(4);

        List<I18nResourceDescriptor> i18nResourceDescriptors = holder.getContributions(I18nResourceDescriptor.class);
        assertThat(i18nResourceDescriptors).hasSize(4);
    }
}