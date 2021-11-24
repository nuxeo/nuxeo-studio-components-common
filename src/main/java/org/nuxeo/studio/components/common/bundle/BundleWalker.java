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

package org.nuxeo.studio.components.common.bundle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.studio.components.common.mapper.descriptors.CSVResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.I18nResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.ImageResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.ResourceDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.XSDResourceDescriptor;

public class BundleWalker {

    private static Log log = LogFactory.getLog(BundleWalker.class);

    public static final String IMG_RESOURCES_PATH = "/web/nuxeo.war/img";

    public static final String CSV_RESOURCES_PATH = "/data/vocabularies";

    public static final String I18N_RESOURCES_PATH = "/data/i18n";

    public static final String XSD_RESOURCES_PATH = "/data/schemas";

    private Path basePath;

    public BundleWalker(String basePath) {
        this(new File(basePath));
    }

    public BundleWalker(File basePath) {
        setBasePath(basePath);
    }

    public BundleWalker(Path basePath) {
        this.basePath = basePath;
    }

    private Path findFile(String filePath) {
        try {
            return Files.walk(basePath).filter(s -> s.endsWith(filePath)).findFirst().orElse(null);
        } catch (IOException e) {
            log.debug(e);
            log.warn("Unable to find MANIFEST: " + filePath + ":" + e.getMessage());
            return null;
        }
    }

    public Stream<Path> getComponents() throws IOException {
        Path manifestPath = getManifest();
        if (manifestPath == null) {
            log.info(String.format("%s do no contains MANIFEST.MF file", basePath.toAbsolutePath().toString()));
            return Stream.empty();
        }

        Manifest manifest;
        try (InputStream fis = Files.newInputStream(manifestPath)) {
            manifest = new Manifest(fis);
        }

        String components = manifest.getMainAttributes().getValue("Nuxeo-Component");
        if (StringUtils.isBlank(components)) {
            return Stream.empty();
        }

        return Arrays.stream(components.split("[, \t\n\r\f]+"))
                     .filter(StringUtils::isNotBlank)
                     .map(this::findFile)
                     .filter(Objects::nonNull);
    }

    public Path getManifest() {
        return findFile("META-INF/MANIFEST.MF");
    }

    public Stream<RegistrationInfo> getRegistrationInfos() throws IOException {
        return getComponents().map(this::read).filter(Objects::nonNull);
    }

    public Stream<ResourceDescriptor> getResourceDescriptors(boolean isJarFile) throws IOException {
        List<ResourceDescriptor> descriptors = new ArrayList<>();

        // Load images resources
        Path imgResourcesPath = isJarFile ? getBasePath().resolve(IMG_RESOURCES_PATH)
                : new File(getBasePath() + IMG_RESOURCES_PATH).toPath();
        if (Files.exists(imgResourcesPath)) {
            descriptors.addAll(Files.walk(imgResourcesPath)
                                    .filter(Files::isRegularFile)
                                    .map(path -> new ImageResourceDescriptor(getResourceName(imgResourcesPath, path)))
                                    .collect(Collectors.toList()));
        }

        // Load csv resources
        Path csvResourcesPath = isJarFile ? getBasePath().resolve(CSV_RESOURCES_PATH)
                : new File(getBasePath() + CSV_RESOURCES_PATH).toPath();
        if (Files.exists(csvResourcesPath)) {
            descriptors.addAll(Files.walk(csvResourcesPath)
                                    .filter(Files::isRegularFile)
                                    .map(path -> new CSVResourceDescriptor(getResourceName(csvResourcesPath, path)))
                                    .collect(Collectors.toList()));
        }

        // Load i18n resources
        Path i18nResourcesPath = isJarFile ? getBasePath().resolve(I18N_RESOURCES_PATH)
                : new File(getBasePath() + I18N_RESOURCES_PATH).toPath();
        if (Files.exists(i18nResourcesPath)) {
            descriptors.addAll(Files.walk(i18nResourcesPath)
                                    .filter(Files::isRegularFile)
                                    .map(path -> new I18nResourceDescriptor(getResourceName(i18nResourcesPath, path)))
                                    .collect(Collectors.toList()));
        }

        // Load xsd resources
        Path xsdResourcesPath = isJarFile ? getBasePath().resolve(XSD_RESOURCES_PATH)
                : new File(getBasePath() + XSD_RESOURCES_PATH).toPath();
        if (Files.exists(xsdResourcesPath)) {
            descriptors.addAll(Files.walk(xsdResourcesPath)
                                    .filter(Files::isRegularFile)
                                    .map(path -> new XSDResourceDescriptor(getResourceName(xsdResourcesPath, path)))
                                    .collect(Collectors.toList()));
        }

        return descriptors.stream();
    }

    public RegistrationInfo read(Path component) {
        try (InputStream is = Files.newInputStream(component)) {
            return RegistrationInfo.read(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getBasePath() {
        return basePath;
    }

    public void setBasePath(File basePath) {
        if (basePath != null) {
            this.basePath = basePath.toPath();
        } else {
            this.basePath = null;
        }
    }

    private String getResourceName(Path resourcesDirPath, Path path) {
        // We create resource name by relativizing the resource path to its root container,
        // and normalizing separator.
        String name = resourcesDirPath.relativize(path).toString().replace("\\", "/");
        return name;
    }
}
