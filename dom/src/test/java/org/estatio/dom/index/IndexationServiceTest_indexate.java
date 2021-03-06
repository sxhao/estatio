/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.index;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.lease.LeaseTermForIndexableRent;

public class IndexationServiceTest_indexate {

    private IndexationService indexer;

    @Mock
    Index mockIndex;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {

        indexer = new IndexationService();
    }

    private Indexable getIndexableWith(LocalDate baseIndexStartDate, LocalDate nextIndexStartDate, double baseValue, double baseIndexValue, double nextIndexValue, double rebaseFactor, double levellingPercentage) {
        LeaseTermForIndexableRent indexable;
        indexable = new LeaseTermForIndexableRent();
        indexable.setIndex(mockIndex);
        indexable.setBaseIndexStartDate(baseIndexStartDate);
        indexable.setNextIndexStartDate(nextIndexStartDate);
        indexable.setBaseValue(BigDecimal.valueOf(baseValue));
        indexable.setBaseIndexValue(BigDecimal.valueOf(baseIndexValue));
        indexable.setNextIndexValue(BigDecimal.valueOf(nextIndexValue));
        indexable.setRebaseFactor(BigDecimal.valueOf(rebaseFactor));
        indexable.setLevellingPercentage(BigDecimal.valueOf(levellingPercentage));
        return indexable;
    }

    @Test
    public void happyCase() {
        final LeaseTermForIndexableRent indexable = (LeaseTermForIndexableRent) getIndexableWith(
                new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1), 250000.00, 122.2, 111.1, 1.234, 100);

        context.checking(new Expectations() {
            {
                oneOf(mockIndex).initialize(with(equal(indexable)));
            }
        });

        indexer.indexate(indexable);

        assertThat(indexable.getIndexationPercentage(), is(BigDecimal.valueOf(12.2)));
        assertThat(indexable.getIndexedValue(), is(BigDecimal.valueOf(280500).setScale(2)));
    }

    @Test
    public void roundingErrors() {
        final LeaseTermForIndexableRent indexable = (LeaseTermForIndexableRent) getIndexableWith(
                new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1), 250000.00, 110.0, 115.0, 1.000, 75);

        context.checking(new Expectations() {
            {
                oneOf(mockIndex).initialize(with(equal(indexable)));
            }
        });

        indexer.indexate(indexable);

        assertThat(indexable.getIndexationPercentage(), is(BigDecimal.valueOf(4.5)));
        assertThat(indexable.getIndexedValue(), is(BigDecimal.valueOf(258437.50).setScale(2)));
    }

    @Test
    public void notNegativeIndexation() {
        final LeaseTermForIndexableRent indexable = (LeaseTermForIndexableRent) getIndexableWith(
                new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1), 250000.00, 115.0, 110.0, 1.000, 75);

        context.checking(new Expectations() {
            {
                oneOf(mockIndex).initialize(with(equal(indexable)));
            }
        });

        indexer.indexate(indexable);

        assertThat(indexable.getIndexationPercentage(), is(BigDecimal.valueOf(-4.3)));
        assertThat(indexable.getIndexedValue(), is(new BigDecimal("250000.0")));
    }
}
