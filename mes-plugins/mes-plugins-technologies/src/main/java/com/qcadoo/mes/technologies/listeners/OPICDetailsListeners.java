/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.mes.technologies.listeners;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.qcadoo.commons.functional.Either;
import com.qcadoo.mes.basic.constants.ProductFields;
import com.qcadoo.mes.basic.constants.UnitConversionItemFieldsB;
import com.qcadoo.mes.technologies.constants.OperationProductInComponentFields;
import com.qcadoo.model.api.BigDecimalUtils;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.units.PossibleUnitConversions;
import com.qcadoo.model.api.units.UnitConversionService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.FieldComponent;
import com.qcadoo.view.api.components.FormComponent;

@Service
public class OPICDetailsListeners {

    @Autowired
    private UnitConversionService unitConversionService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void calculateQuantity(final ViewDefinitionState view, final ComponentState componentState, final String[] args) {

        FormComponent form = (FormComponent) view.getComponentByReference("form");
        Entity opic = form.getPersistedEntityWithIncludedFormValues();

        String givenUnit = opic.getStringField(OperationProductInComponentFields.GIVEN_UNIT);
        Entity product = opic.getBelongsToField(OperationProductInComponentFields.PRODUCT);

        FieldComponent givenQuantityField = (FieldComponent) view
                .getComponentByReference(OperationProductInComponentFields.GIVEN_QUANTITY);
        if (product == null || givenUnit == null || givenUnit.isEmpty() || givenQuantityField.getFieldValue() == null) {
            return;
        }

        Either<Exception, Optional<BigDecimal>> maybeQuantity = BigDecimalUtils.tryParse(
                (String) givenQuantityField.getFieldValue(), view.getLocale());
        if (maybeQuantity.isRight()) {
            if (maybeQuantity.getRight().isPresent()) {
                BigDecimal givenQuantity = maybeQuantity.getRight().get();
                String baseUnit = product.getStringField(ProductFields.UNIT);
                if (baseUnit.equals(givenUnit)) {
                    opic.setField(OperationProductInComponentFields.QUANTITY, givenQuantity);
                } else {
                    PossibleUnitConversions unitConversions = unitConversionService.getPossibleConversions(givenUnit,
                            searchCriteriaBuilder -> searchCriteriaBuilder.add(SearchRestrictions.belongsTo(
                                    UnitConversionItemFieldsB.PRODUCT, product)));
                    if (unitConversions.isDefinedFor(baseUnit)) {
                        BigDecimal convertedQuantity = unitConversions.convertTo(givenQuantity, baseUnit);
                        opic.setField(OperationProductInComponentFields.QUANTITY, convertedQuantity);
                    } else {
                        opic.addError(opic.getDataDefinition().getField(OperationProductInComponentFields.GIVEN_QUANTITY),
                                "technologies.operationProductInComponent.validate.error.missingUnitConversion");
                        opic.setField(OperationProductInComponentFields.QUANTITY, null);
                    }
                }

            } else {
                opic.setField(OperationProductInComponentFields.QUANTITY, null);
            }
        } else {
            opic.setField(OperationProductInComponentFields.QUANTITY, null);
        }
        form.setEntity(opic);
    }
}
