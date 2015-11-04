/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo MES
 * Version: 1.5
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
package com.qcadoo.mes.basic.validators;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.basic.constants.PurchaseFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.view.api.ComponentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseValidators {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public boolean validatesWith(final DataDefinition basicPurchaseDD, final Entity basicPurchase) {
        return checkValueOfQuantity(basicPurchaseDD, basicPurchase)
                && checkValueOfPrice(basicPurchaseDD, basicPurchase)
                && checkIfUniqueProductAndPrice(basicPurchaseDD, basicPurchase);
    }

    private boolean checkValueOfQuantity(final DataDefinition basicPurchaseDD, final Entity basicPurchase) {
        BigDecimal quantity = basicPurchase.getDecimalField(PurchaseFields.QUANTITY);

        if (quantity == null) {
            return true;
        }

        if ((quantity != null) && (quantity.compareTo(BigDecimal.ZERO) <= 0)) {
            basicPurchase.addError(basicPurchaseDD.getField(PurchaseFields.QUANTITY),
                    "basicPurchase.basicPurchase.quantity.error.lowerOrEqualZero");
        }

        if (!basicPurchase.getGlobalErrors().isEmpty() || !basicPurchase.getErrors().isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean checkValueOfPrice(final DataDefinition basicPurchaseDD, final Entity basicPurchase) {
        BigDecimal price = basicPurchase.getDecimalField(PurchaseFields.PRICE);

        if (price == null) {
            return true;
        }

        if ((price != null) && (price.compareTo(BigDecimal.ZERO) <= 0)) {
            basicPurchase.addError(basicPurchaseDD.getField(PurchaseFields.PRICE),
                    "basicPurchase.basicPurchase.price.error.lowerOrEqualZero");
        }

        if (!basicPurchase.getGlobalErrors().isEmpty() || !basicPurchase.getErrors().isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean checkIfUniqueProductAndPrice(final DataDefinition basicPurchaseDD, final Entity basicPurchase) {
        BigDecimal price = basicPurchase.getDecimalField(PurchaseFields.PRICE);
        Entity product = (Entity) basicPurchase.getField(PurchaseFields.PRODUCT);

        List<Entity> purchases = dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PURCHASE).find().list().getEntities();

        if (price == null || product == null) {
            return true;
        }

        if (purchases.isEmpty()) {
            return true;
        }

        for (Entity purchase : purchases) {
            Entity lProduct = (Entity) purchase.getField(PurchaseFields.PRODUCT);
            BigDecimal lPrice = purchase.getDecimalField(PurchaseFields.PRICE);

            if (basicPurchase.getId() == null || basicPurchase.getId().compareTo(purchase.getId()) != 0) {
                if (lProduct.equals(product) && lPrice.compareTo(price) == 0) {
                    basicPurchase.addError(basicPurchaseDD.getField(PurchaseFields.PRODUCT),
                            "basicPurchase.basicPurchase.product.error.notUnique");
                    basicPurchase.addError(basicPurchaseDD.getField(PurchaseFields.PRICE),
                            "basicPurchase.basicPurchase.price.error.notUnique");
                    basicPurchase.addGlobalError("basicPurchase.editOrAdd.priceAndProductNotUnique");
                }
            }
        }

        if (!basicPurchase.getGlobalErrors().isEmpty() || !basicPurchase.getErrors().isEmpty()) {
            return false;
        }

        return true;
    }



}
