/**
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.web.designer.workspace;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.bonitasoft.web.designer.config.DesignerInitializerException;
import org.bonitasoft.web.designer.migration.LiveRepositoryUpdate;
import org.bonitasoft.web.designer.model.widget.Widget;
import org.springframework.web.context.ServletContextAware;

/**
 * Context Listener initializing bonita page designer workspace
 *
 * @author Colin Puy
 */
@Named
public class WorkspaceInitializer implements ServletContextAware {

    @Inject
    private Workspace workspace;

    private List<LiveRepositoryUpdate> migrations;

    private ServletContext servletContext;

    /**
     * List cannot be injected in constructor with @Inject so we use setter and @Resource to inject them
     */
    @Resource(name = "liveRepositoriesUpdate")
    public void setMigrations(List<LiveRepositoryUpdate> migrations) {
        this.migrations = migrations;
    }

    @PostConstruct
    public void contextInitialized() {
        try {
            workspace.initialize();
            for (LiveRepositoryUpdate migration : migrations) {
                migration.start();
            }
        } catch (IOException e) {
            throw new DesignerInitializerException("Unable to initialize workspace", e);
        }
    }

    public synchronized void migrateWorkspace(){
        for (LiveRepositoryUpdate migration :  migrations.stream().sorted().collect(Collectors.toList())) {
            try {
                migration.migrate();
            } catch (IOException e) {
                throw new DesignerInitializerException("Unable to migrate workspace", e);
            }
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
