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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.nuxeo.studio.components.common.serializer.adapter.schema.ComplexField;
import org.nuxeo.studio.components.common.serializer.adapter.schema.Field;
import org.nuxeo.studio.components.common.serializer.adapter.schema.SimpleField;
import org.nuxeo.studio.components.common.serializer.adapter.schema.SimpleSchemaReader;

public class TestSchemaReader {

    protected SimpleSchemaReader readerFrom(String resource) {
        return new SimpleSchemaReader(TestHelper.getResource(resource));
    }

    protected Field findField(String name, Collection<Field> fields) {
        return fields.stream().filter(s -> name.equals(s.getName())).findFirst().orElse(null);
    }

    protected void assertSimpleField(Collection<Field> fields, String name, String type, boolean isArray,
            String directory) {
        Field field = findField(name, fields);
        assertThat(field).as(name).isNotNull();
        assertThat(field.isComplex()).as(name).isFalse();
        assertThat(field.isArray()).as(name).isEqualTo(isArray);
        assertThat(((SimpleField) field).getType()).as(name).isEqualTo(type);
        assertThat(((SimpleField) field).getDirectory()).as(name).isEqualTo(directory);
    }

    @Test
    public void shouldExtractComplexTypeWithSimpleType() {
        SimpleSchemaReader reader = readerFrom("schema/complextype-test.xsd");
        ComplexField custom = (ComplexField) reader.getTypeFields().get("custom");
        assertThat(custom).isNotNull();

        List<Field> fields = custom.getFields();
        assertThat(fields).hasSize(5);
        assertThat(fields).extracting("isArray").containsOnly(false);

        assertThat(findField("encoding", fields)).extracting("type").containsExactly("string");
        assertThat(findField("mime-type", fields)).extracting("type").containsExactly("string");
        assertThat(findField("name", fields)).extracting("type").containsExactly("string");
        assertThat(findField("length", fields)).extracting("type").containsExactly("long");
        assertThat(findField("digest", fields)).extracting("type").containsExactly("string");
    }

    @Test
    public void shouldExtractComplexTypeWithRestrictedType() {
        SimpleSchemaReader reader = readerFrom("schema/complextype-test.xsd");
        ComplexField custom = (ComplexField) reader.getTypeFields().get("withRestriction");
        assertThat(custom).isNotNull();

        List<Field> fields = custom.getFields();
        assertThat(fields).hasSize(1);
        assertThat(fields).extracting("isArray").containsOnly(false);

        assertThat(findField("coverage", custom.getFields())).extracting("type").containsExactly("string");
    }

    @Test
    public void shouldExtractComplexTypeWithSequencedType() {
        SimpleSchemaReader reader = readerFrom("schema/complextype-test.xsd");
        ComplexField custom = (ComplexField) reader.getTypeFields().get("withSequence");
        assertThat(custom).isNotNull();

        List<Field> fields = custom.getFields();
        assertThat(fields).hasSize(1);
        assertThat(fields).extracting("isArray").containsOnly(true);

        assertThat(findField("attachments", custom.getFields())).extracting("type").containsExactly("string");
    }

    @Test
    public void shouldResolveInternalType() {
        SimpleSchemaReader reader = readerFrom("schema/complextype-test.xsd");
        reader.load();

        Field elt = findField("customElement", reader.getFields());
        assertThat(elt).isNotNull();
        assertThat(elt.isComplex()).isTrue();
        assertThat(((ComplexField) elt).getFields()).hasSize(5);

        // No type attribute and no inline restriction: the field stays as before, here a list
        assertSimpleField(reader.getFields(), "ensureNotGettingChildComplexType", "string", true, null);
    }

    @Test
    public void shouldRegisterSimpleTypes() {
        SimpleSchemaReader reader = readerFrom("schema/simpletype-test.xsd");
        Field field = reader.getTypeFields().get("stringArray");
        assertThat(field).isNotNull();
        assertThat(field.isComplex()).isFalse();
        assertThat(field.isArray()).isTrue();
        Assertions.assertThat(((SimpleField) field).getType()).isEqualTo("string");

        field = reader.getTypeFields().get("subjectList");
        assertThat(field).isNotNull();
        assertThat(field.isComplex()).isFalse();
        assertThat(field.isArray()).isTrue();
        assertThat(((SimpleField) field).getType()).isEqualTo("string");

        field = reader.getTypeFields().get("clob");
        assertThat(field).isNotNull();
        assertThat(field.isComplex()).isFalse();
        assertThat(field.isArray()).isFalse();
        assertThat(((SimpleField) field).getType()).isEqualTo("string");
    }

    @Test
    public void shouldReadDirectoryOfTopLevelDirectoryFields() {
        SimpleSchemaReader reader = readerFrom("schema/directoryresolver-test.xsd");
        reader.load();

        List<Field> fields = reader.getFields();
        assertThat(fields).extracting("name")
                          .containsExactly("title", "tags", "codes", "itemTypeList", "accessibility", "keywords",
                                  "reviewer", "otherResolverWithDirectory", "classification", "noDirectory",
                                  "blankDirectory");
        assertSimpleField(fields, "accessibility", "string", false, "NXS7530SourceVocab");
        // A list Directory field stays a list
        assertSimpleField(fields, "keywords", "string", true, "MyVocab");
    }

    @Test
    public void shouldNotReadDirectoryOfOtherFields() {
        SimpleSchemaReader reader = readerFrom("schema/directoryresolver-test.xsd");
        reader.load();

        List<Field> fields = reader.getFields();
        assertSimpleField(fields, "title", "string", false, null);
        // Other list fields stay single-valued, as before
        assertSimpleField(fields, "tags", "string", false, null);
        assertSimpleField(fields, "codes", "string", false, null);
        assertSimpleField(fields, "itemTypeList", "string", false, null);
        // Other resolvers, even with a directory, and a directoryResolver with no directory or a blank one
        assertSimpleField(fields, "reviewer", "string", false, null);
        assertSimpleField(fields, "otherResolverWithDirectory", "string", false, null);
        assertSimpleField(fields, "noDirectory", "string", false, null);
        assertSimpleField(fields, "blankDirectory", "string", false, null);

        // Sub-fields of a complex field
        Field classification = findField("classification", fields);
        assertThat(classification).isNotNull();
        assertThat(classification.isComplex()).isTrue();
        assertThat(classification.isArray()).isFalse();
        List<Field> subFields = ((ComplexField) classification).getFields();
        assertThat(subFields).extracting("name").containsExactly("level", "topics");
        assertSimpleField(subFields, "level", "string", false, null);
        assertSimpleField(subFields, "topics", "string", false, null);
    }

    @Test
    public void shouldReadDirectoryResolverByNamespace() {
        SimpleSchemaReader reader = readerFrom("schema/directoryresolver-prefix-test.xsd");
        reader.load();

        List<Field> fields = reader.getFields();
        assertThat(fields).extracting("name")
                          .containsExactly("accessibility", "keywords", "notReference", "onlyResolverIsReference",
                                  "onlyDirectoryIsReference");
        // The external references namespace is bound to the prefix xref
        assertSimpleField(fields, "accessibility", "string", false, "NXS7530SourceVocab");
        assertSimpleField(fields, "keywords", "string", true, "MyVocab");
        // The prefix ref is bound to another namespace
        assertSimpleField(fields, "notReference", "string", false, null);
        assertSimpleField(fields, "onlyResolverIsReference", "string", false, null);
        assertSimpleField(fields, "onlyDirectoryIsReference", "string", false, null);
    }

    @Test
    public void shouldNotReadDirectoryOfFieldWithTypeAttributeOrOfNamedType() {
        SimpleSchemaReader reader = readerFrom("schema/dublincore.xsd");
        reader.load();

        List<Field> fields = reader.getFields();
        assertSimpleField(fields, "coverage", "string", false, "l10ncoverage");
        // nature has a type attribute, besides its inline directoryResolver
        assertSimpleField(fields, "nature", "string", false, null);
        assertSimpleField(fields, "lastContributor", "string", false, null);

        // The named list type subjectList has a directoryResolver
        Field subjectList = reader.getTypeFields().get("subjectList");
        assertThat(subjectList).isNotNull();
        assertThat(subjectList.isArray()).isTrue();
        assertThat(((SimpleField) subjectList).getDirectory()).isNull();
    }

    @Test
    public void shouldKeepDirectoryInCopy() {
        SimpleField field = new SimpleField("accessibility", "string", false, "NXS7530SourceVocab");

        SimpleField copy = (SimpleField) field.copy("keywords", true);
        assertThat(copy.getName()).isEqualTo("keywords");
        assertThat(copy.getType()).isEqualTo("string");
        assertThat(copy.isArray()).isTrue();
        assertThat(copy.getDirectory()).isEqualTo("NXS7530SourceVocab");

        assertThat(field.copy("accessibility", false)).isEqualTo(field);
        assertThat(field.copy("accessibility", false).hashCode()).isEqualTo(field.hashCode());
        assertThat(field).isNotEqualTo(new SimpleField("accessibility", "long", false, "NXS7530SourceVocab"));
        assertThat(field).isNotEqualTo(new SimpleField("accessibility", "string", false, "MyVocab"));
        assertThat(field).isNotEqualTo(new SimpleField("accessibility", "string", false));
        assertThat(new SimpleField("accessibility", "string", false)).isNotEqualTo(field);
        assertThat(new SimpleField("accessibility", "string", false)).isEqualTo(
                new SimpleField("accessibility", "string", false, null));
        assertThat(new SimpleField("accessibility", "string", false).hashCode()).isEqualTo(
                new SimpleField("accessibility", "string", false, null).hashCode());
    }
}
