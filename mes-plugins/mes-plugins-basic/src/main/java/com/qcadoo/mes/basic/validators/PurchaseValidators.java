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

import com.qcadoo.mes.basic.constants.PurchaseFields;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PurchaseValidators {

    public boolean validatesWith(final DataDefinition basicPurchaseDD, final Entity basicPurchase) {
        return checkValueOfQuantity(basicPurchaseDD, basicPurchase) && checkValueOfPrice(basicPurchaseDD, basicPurchase);
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



}
