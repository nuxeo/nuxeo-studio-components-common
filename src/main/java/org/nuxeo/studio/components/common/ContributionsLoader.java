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

package org.nuxeo.studio.components.common;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.studio.components.common.bundle.BundleWalker;
import org.nuxeo.studio.components.common.bundle.ContributionsHolder;
import org.nuxeo.studio.components.common.runtime.ExtractorContext;

/**
 * Load contributions from several sources, depending of Mojo parameters
 */
public class ContributionsLoader {

    protected final ExtractorOptions opts;

    protected final ContributionsHolder holder;

    public ContributionsLoader(ContributionsHolder holder, ExtractorOptions opts) {
        this.opts = opts;
        this.holder = holder;
    }

    public void load() {
        if (StringUtils.isNotBlank(opts.getJarFile())) {
            // Based on external jarFile
            List<URI> files = Arrays.stream(opts.getJarFile().split(",")) //
                                    .filter(s -> s.endsWith(".jar"))
                                    .map(s -> new File(s).toURI())
                                    .collect(Collectors.toList());
            ExtractorContext.instance.addExternalSources(files);

            files.forEach(this::loadFromJarFile);
        } else {
            // Based on Project (Standalone project, other project)
            opts.getSourcesDirectory().forEach(this::loadFromDirectory);
        }
    }

    protected void loadFromJarFile(URI jarFileURI) {
        File file = new File(jarFileURI);
        if (!file.exists()) {
            throw new RuntimeException("Unknown jar file: " + jarFileURI);
        }

        URI uri = URI.create("jar:" + jarFileURI);
        try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>())) {
            BundleWalker walker = new BundleWalker(fs.getPath("/"));
            loadFromWalker(walker, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadFromDirectory(String directory) {
        BundleWalker walker = new BundleWalker(directory);
        try {
            loadFromWalker(walker, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadFromWalker(BundleWalker walker, boolean isJarFile) throws IOException {
        // Load extensions
        walker.getRegistrationInfos().forEach(holder::load);

        // Load ressources
        walker.getResourceDescriptors(isJarFile).forEach(holder::load);
    }
}
