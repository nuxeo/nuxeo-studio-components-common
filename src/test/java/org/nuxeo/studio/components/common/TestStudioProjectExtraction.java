package org.nuxeo.studio.components.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nuxeo.studio.components.common.bundle.ContributionsHolder;
import org.nuxeo.studio.components.common.mapper.descriptors.PageProviderDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.SchemaBindingDescriptor;
import org.nuxeo.studio.components.common.mapper.descriptors.WorkflowDescriptor;

public class TestStudioProjectExtraction extends AbstractExtractorTest {
    public static final String STUDIO_EXTENSTIONS_FILE = "studio-extensions.xml";

    protected ContributionsHolder holder;

    @Before
    public void before() {
        holder = new ContributionsHolder();
    }

    @Test
    public void testLoad() throws URISyntaxException {
        ContributionsHolder holder = loadComponent(STUDIO_EXTENSTIONS_FILE);
        List<SchemaBindingDescriptor> schemaContribs = holder.getContributions(SchemaBindingDescriptor.class);
        assertThat(schemaContribs).hasSize(13);

        List<WorkflowDescriptor> workflowContribs = holder.getContributions(WorkflowDescriptor.class);
        assertThat(workflowContribs).hasSize(3);

        List<PageProviderDescriptor> ppContribs = holder.getContributions(PageProviderDescriptor.class);
        assertThat(ppContribs).hasSize(1);
    }
}
