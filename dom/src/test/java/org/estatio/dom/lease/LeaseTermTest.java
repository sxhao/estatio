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
package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.hamcrest.Description;
import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.valuetypes.AbstractInterval.IntervalEnding;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

public class LeaseTermTest {

    private Lease lease;
    private LeaseTermForTesting term;
    private LeaseItem item;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    LeaseTerms mockLeaseTerms;

    @Mock
    private static ClockService mockClockService;

    private final LocalDate now = LocalDate.now();

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(now));
                allowing(mockLeaseTerms).newLeaseTerm(with(any(LeaseItem.class)), with(any(LeaseTerm.class)), with(any(LocalDate.class)));
                will(returnLeaseTerm());
            }
        });

        lease = new Lease();
        lease.setStartDate(new LocalDate(2012, 1, 1));

        item = new LeaseItem();
        item.setEndDate(new LocalDate(2013, 6, 30));
        lease.getItems().add(item);
        item.setLease(lease);
        
        item.injectLeaseTerms(mockLeaseTerms);
        item.injectClockService(mockClockService);

        term = new LeaseTermForTesting();
        
        item.getTerms().add(term);
        term.setLeaseItem(item);
        
        term.setStartDate(new LocalDate(2012, 1, 1));
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.injectClockService(mockClockService);
        term.injectLeaseTerms(mockLeaseTerms);
        term.initialize();
    }

    @Test
    public void createNext_ok() {
        final LeaseTermForTesting anotherTerm = new LeaseTermForTesting();
        item.getTerms().add(anotherTerm);
        anotherTerm.setLeaseItem(item);
        
        LeaseTermForTesting next = (LeaseTermForTesting) term.createNext(new LocalDate(2013, 1, 1));
        Assert.assertThat(this.term.getEndDate(), Is.is(new LocalDate(2012, 12, 31)));
        Assert.assertThat(next.getStartDate(), Is.is(new LocalDate(2013, 1, 1)));
        Assert.assertNull(next.getEndDate());
    }

    @Test
    public void update_ok() {
        LeaseTermForTesting nextTerm = new LeaseTermForTesting();
        nextTerm.modifyPrevious(term);
        nextTerm.modifyStartDate(new LocalDate(2013, 1, 1));
        // term.update();
        assertThat(term.getEndDate(), Is.is(new LocalDate(2012, 12, 31)));
    }

    // TODO: We moved the retrieval to the repository so this is broken. 
    @Ignore
    @Test
    public void invoicedValueFor_ok() throws Exception {
        LocalDateInterval interval = new LocalDateInterval(new LocalDate(2012, 1, 1), new LocalDate(2012,4,1), IntervalEnding.EXCLUDING_END_DATE);
        LeaseTermForTesting term = new LeaseTermForTesting();
        Invoice invoice = new Invoice();
        invoice.setStatus(InvoiceStatus.APPROVED);
        InvoiceItemForLease item1 = new InvoiceItemForLease();
        
        invoice.getItems().add(item1);
        item1.setInvoice(invoice);
        
        item1.setLeaseTerm(term);
        item1.setStartDate(interval.startDate());
        item1.setNetAmount(BigDecimal.valueOf(1234.45));
        
        InvoiceItemForLease item2 = new InvoiceItemForLease();
        invoice.getItems().add(item2);
        item2.setInvoice(invoice);
        
        item2.setNetAmount(BigDecimal.valueOf(1234.45));
        item2.setLeaseTerm(term);
        item2.setStartDate(interval.startDate());

    }

    @Test
    public void testEffectiveInterval() throws Exception {
        term.align();
        assertThat(term.getEffectiveInterval().endDate(), Is.is(new LocalDate(2013, 6, 30)));
        lease.setTenancyEndDate(new LocalDate(2012, 3, 31));
        assertThat(term.getEffectiveInterval().endDate(), Is.is(new LocalDate(2012, 3, 31)));
    }

    @Test
    public void testEI() throws Exception {
        assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, null, "2011-02-01", null, "2011-01-01", "2011-12-31").toString(), is("2011-02-01/2012-01-01") );
        assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, "2012-06-30", "2011-02-01", null, "2012-01-01", "2012-12-31").toString(), is("2012-01-01/2012-07-01") );
        assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, null, "2011-02-01", null, "2011-01-01", null).toString(), is("2011-02-01/----------") );
        assertNull(effectiveIntervalWith("2011-01-01", "2012-12-31", null, "2013-12-31", "2011-02-01", null, "2014-01-01", null));
           }
    
    private LocalDateInterval effectiveIntervalWith(
            String leaseStartDate,
            String leaseEndDate,
            String leaseTenancyStartDate,
            String leaseTenacyEndDate,
            String itemStartDate,
            String itemEndDate,
            String termStartDate, String termEndDate
            ){        

        Lease lease = new Lease();
        lease.setStartDate(parseDate(leaseStartDate));
        lease.setEndDate(parseDate(leaseEndDate));
        lease.setTenancyEndDate(parseDate(leaseTenacyEndDate));

        LeaseItem item = new LeaseItem();
        item.setStartDate(parseDate(itemStartDate));
        item.setEndDate(parseDate(itemEndDate));
        lease.getItems().add(item);
        item.setLease(lease);
        
        item.injectLeaseTerms(mockLeaseTerms);
        item.injectClockService(mockClockService);

        LeaseTerm term = new LeaseTermForTesting();
        
        item.getTerms().add(term);
        term.setLeaseItem(item);
        
        term.setStartDate(parseDate(termStartDate));
        term.setEndDate(parseDate(termEndDate));
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.injectClockService(mockClockService);
        term.initialize();
        
        return term.getEffectiveInterval();
        
    }
    
    private LocalDate parseDate(String input) {
        if (input == null)
            return null;
        return LocalDate.parse(input);
        
    }
    
    
    
    // //////////////////////////////////////

    public static Action returnLeaseTerm() {
        return new Action() {
            @Override
            public Object invoke(Invocation invocation) throws Throwable {
                LeaseItem leaseItem = (LeaseItem) invocation.getParameter(0);
                LeaseTerm leaseTerm = (LeaseTerm) invocation.getParameter(1);
                LeaseTermForTesting ltt = new LeaseTermForTesting();
                leaseItem.getTerms().add(ltt);
                ltt.setLeaseItem(leaseItem);
                ltt.modifyPrevious(leaseTerm);
                ltt.initialize();
                ltt.injectClockService(mockClockService);
                return ltt;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("new Lease Term under item and with previous term");
            }
        };
    }

}
