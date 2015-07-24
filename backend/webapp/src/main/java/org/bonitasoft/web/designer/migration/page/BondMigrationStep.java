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

package org.bonitasoft.web.designer.migration.page;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.ImmutableMap;
import org.bonitasoft.web.designer.migration.MigrationStep;
import org.bonitasoft.web.designer.model.page.Component;
import org.bonitasoft.web.designer.model.page.Element;
import org.bonitasoft.web.designer.model.page.Page;
import org.bonitasoft.web.designer.model.page.PropertyValue;
import org.bonitasoft.web.designer.model.widget.BondType;
import org.bonitasoft.web.designer.model.widget.Property;
import org.bonitasoft.web.designer.model.widget.Widget;
import org.bonitasoft.web.designer.repository.WidgetRepository;
import org.bonitasoft.web.designer.visitor.ComponentVisitor;
import org.bonitasoft.web.designer.visitor.AnyContainerVisitor;

@Named
public class BondMigrationStep implements MigrationStep<Page> {

    private ComponentVisitor componentVisitor;
    private WidgetRepository widgetRepository;

    private Map<BondType, BondMigrationStrategy> migrationStrategies = ImmutableMap.<BondType, BondMigrationStrategy>builder()
            .put(BondType.CONSTANT, new ConstantBondMigrationStrategy())
            .put(BondType.INTERPOLATION, new InterpolationBondMigrationStrategy())
            .put(BondType.EXPRESSION, new ExpressionBondMigrationStrategy())
            .put(BondType.VARIABLE, new VariableBondMigrationStrategy())
            .build();

    @Inject
    public BondMigrationStep(ComponentVisitor componentVisitor, WidgetRepository widgetRepository) {
        this.componentVisitor = componentVisitor;
        this.widgetRepository = widgetRepository;
    }

    @Override
    public void migrate(Page page) {
        for (Component component :  page.accept(componentVisitor)) {
            Widget widget = widgetRepository.get(component.getId());
            for (Map.Entry<String, PropertyValue> entry : component.getPropertyValues().entrySet()) {
                Property property = widget.getProperty(entry.getKey());
                migrationStrategies
                        .get(property != null ? property.getBond() : BondType.EXPRESSION)
                        .migrate(property, entry.getValue());
            }
        }

        for(Element element: page.accept(new AnyContainerVisitor())) {
            for (Map.Entry<String, PropertyValue> entry : element.getPropertyValues().entrySet()) {
                migrationStrategies
                        .get(BondType.EXPRESSION)
                        .migrate(new Property(), entry.getValue());
            }
        }
    }
}
