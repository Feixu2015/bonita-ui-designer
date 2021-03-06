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
package org.bonitasoft.web.designer.model.contract.builders;

import org.bonitasoft.web.designer.model.contract.Contract;
import org.bonitasoft.web.designer.model.contract.ContractInput;

import static org.bonitasoft.web.designer.model.contract.builders.ContractInputBuilder.*;

public class ContractBuilder {

    private Contract contract;

    private ContractBuilder(Contract contract) {
        this.contract = contract;
    }

    public static ContractBuilder aContract() {
        return new ContractBuilder(new Contract());
    }

    public ContractBuilder withInput(ContractInput... contractInput) {
        for (ContractInput input : contractInput) {
            contract.addInput(input);
        }
        return this;
    }

    public Contract build() {
        return contract;
    }

    public static Contract aSimpleContract() {
        return aContract().withInput(aContractInput("name").withDescription("employee name").build(),
                aBooleanContractInput("isValid"),
                aNodeContractInput("ticket").withInput(
                        aStringContractInput("title"),
                        aDateContractInput("creationDate"),
                        aLocalDateContractInput("creationLocalDate"),
                        aLocalDateTimeContractInput("creationLocalDateTime"),
                        aOffsetDateTimeContractInput("creationOffsetDateTime"),
                        aLongContractInput("updateTime")).build()).build();

    }

    public static Contract aContractWithMultipleInput() {
        return aContract().withInput(aMultipleStringContractInput("names")).build();
    }
}
