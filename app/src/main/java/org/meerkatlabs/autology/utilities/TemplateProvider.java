package org.meerkatlabs.autology.utilities;

import android.support.annotation.NonNull;

import org.meerkatlabs.autology.utilities.templates.BaseTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

/**
 * Provide a list of all of the templates that are defined in the application.  Initially this just
 * provides a structure of metadata for installing the template objects.
 */
public class TemplateProvider {

    private final Collection<BaseTemplate> templates = new TreeSet<>();

    public TemplateProvider() {
        templates.add(new BaseTemplate());
    }

    public void add(@NonNull BaseTemplate template) {
        templates.add(template);
    }

    public Collection<BaseTemplate> getTemplates() {
        return Collections.unmodifiableCollection(templates);
    }

}
