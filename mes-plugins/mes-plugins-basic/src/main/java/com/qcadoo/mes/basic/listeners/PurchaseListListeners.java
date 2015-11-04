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
package com.qcadoo.mes.basic.listeners;

import com.qcadoo.mes.basic.constants.PurchaseFields;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.NumberService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.components.GridComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseListListeners {

    private static String L_GRID = "grid";

    @Autowired
    private NumberService numberService;

    @SuppressWarnings("unused") //it's used in purchaseList.xml view
    public void countAveragePrice(final ViewDefinitionState view, final ComponentState componentState, final String[] args) {

        GridComponent gridComponent = (GridComponent) view.getComponentByReference(L_GRID);
        List<Entity> purchases = gridComponent.getEntities();

        if (!purchases.isEmpty()) {
            BigDecimal priceSum = new BigDecimal("0");
            BigDecimal numberOfProducts = new BigDecimal("0");
            for (Entity p : purchases) {
                numberOfProducts = numberOfProducts.add(p.getDecimalField(PurchaseFields.QUANTITY), numberService.getMathContext());
                priceSum = priceSum.add(p.getDecimalField(PurchaseFields.PRICE), numberService.getMathContext());
            }
            BigDecimal averageCost = priceSum.divide(numberOfProducts, numberService.getMathContext());
            view.getComponentByReference(PurchaseFields.PURCHASE_AVERAGE_PRICE).setFieldValue(averageCost.toString());
        } else {
            view.getComponentByReference(PurchaseFields.PURCHASE_AVERAGE_PRICE).setFieldValue("0");
        }
    }
}