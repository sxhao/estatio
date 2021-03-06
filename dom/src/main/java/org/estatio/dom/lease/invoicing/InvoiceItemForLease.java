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
package org.estatio.dom.lease.invoicing;

import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceSource;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.valuetypes.LocalDateInterval;

/**
 * A lease-specific subclass of {@link InvoiceItem}, referring
 * {@link #getLeaseTerm() back} to the {@link LeaseTerm} that acts as the
 * <tt>InvoiceSource</tt> of this item's owning {@link Invoice}.
 */
@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseTermAndInterval", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm " +
                        "&& startDate == :startDate " +
                        "&& endDate == :endDate "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseTermAndIntervalAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm " +
                        "&& startDate == :startDate " +
                        "&& endDate == :endDate " +
                        "&& invoice.status == :invoiceStatus"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " +
                        "WHERE leaseTerm.leaseItem.lease == :lease " +
                        "&& invoice.status == :invoiceStatus"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " +
                        "WHERE leaseTerm.leaseItem == :leaseItem " +
                        "&& invoice.status == :invoiceStatus"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseTermAndInvoiceStatus", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.lease.invoicing.InvoiceItemForLease " +
                        "WHERE leaseTerm == :leaseTerm " +
                        "&& invoice.status == :invoiceStatus")
})
@Indices({
        @Index(name = "InvoiceItemForLease_LeaseTerm_StartDate_EndDate_DueDate_IDX",
                members = { "leaseTerm", "startDate", "endDate", "dueDate" }),
        @Index(name = "InvoiceItemForLease_LeaseTerm_StartDate_EndDate_IDX",
                members = { "leaseTerm", "startDate", "endDate" }),

})
public class InvoiceItemForLease extends InvoiceItem {

    @Override
    public InvoiceSource getSource() {
        return getLeaseTerm();
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    @Title(sequence = "1", append = ":")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    private LeaseTerm leaseTerm;

    @javax.jdo.annotations.Column(name = "leaseTermId", allowsNull = "true")
    @Disabled
    @Hidden
    //@Hidden(where = Where.REFERENCES_PARENT)
    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(final LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    // //////////////////////////////////////

    private FixedAsset fixedAsset;

    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    @Named("Unit")
    // for the moment, might be generalized (to the user) in the future
    @Disabled
    public FixedAsset getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(final FixedAsset fixedAsset) {
        this.fixedAsset = fixedAsset;
    }

    // //////////////////////////////////////

    private Boolean adjustment;

    @Optional
    public Boolean isAdjustment() {
        return adjustment;
    }

    public void setAdjustment(final Boolean adjustment) {
        this.adjustment = adjustment;
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval().overlap(getLeaseTerm().getEffectiveInterval());
    }

    // //////////////////////////////////////

    @Hidden
    public void remove() {
        // no safeguard, assuming being called with precaution
        setLeaseTerm(null);
        super.remove();
    }

    // //////////////////////////////////////

    protected AgreementTypes agreementTypes;

    public final void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    protected AgreementRoleTypes agreementRoleTypes;

    public final void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(final InvoiceItem other) {
        int compare = super.compareTo(other);
        if (compare != 0) {
            return compare;
        }
        if (other instanceof InvoiceItemForLease) {
            return ORDERING_BY_LEASE_TERM.compare(this, (InvoiceItemForLease) other);
        }
        return getClass().getName().compareTo(other.getClass().getName());
    }

    public final static Ordering<InvoiceItemForLease> ORDERING_BY_LEASE_TERM = new Ordering<InvoiceItemForLease>() {
        public int compare(final InvoiceItemForLease p, final InvoiceItemForLease q) {
            // unnecessary, but keeps findbugs happy...
            if (p == null && q == null) {
                return 0;
            }
            if (p == null) {
                return -1;
            }
            if (q == null) {
                return +1;
            }
            return Ordering.natural().nullsFirst().compare(p.getLeaseTerm(), q.getLeaseTerm());
        }
    };

}
