package org.estatio.dom.lease;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioTransactionalObject;

//TODO: to complete
//@javax.jdo.annotations.PersistenceCapable(extensions={
//        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
//        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
//    })
//@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public class LeaseAssignment extends EstatioTransactionalObject {

    // {{ previousLease (property)
    private Lease previousLease;

    @MemberOrder(sequence = "1")
    public Lease getPreviousLease() {
        return previousLease;
    }

    public void setPreviousLease(final Lease previousLease) {
        this.previousLease = previousLease;
    }

    // }}

    // {{ NextLease (property)
    private Lease nextLease;

    @MemberOrder(sequence = "1")
    public Lease getNextLease() {
        return nextLease;
    }

    public void setNextLease(final Lease nextLease) {
        this.nextLease = nextLease;
    }

    // }}

    // {{ AssignmentDate (property)
    private LocalDate assignmentDate;

    @MemberOrder(sequence = "1")
    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(final LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    // }}

    // {{ AssignmentType (property)
    private LeaseAssignmentType assignmentType;

    @MemberOrder(sequence = "1")
    public LeaseAssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(final LeaseAssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    // }}

    public enum LeaseAssignmentType {
        NEW_TENANT, RENEWAL, TURNOVER
    }

}
