package org.estatio.dom.index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Extension;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    })*/
@Immutable
public class Index extends EstatioRefDataObject implements Comparable<Index> {

    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    
    @javax.jdo.annotations.Persistent(mappedBy = "index")
    private List<IndexBase> indexBases = new ArrayList<IndexBase>();

    @MemberOrder(name = "Bases", sequence = "3")
    public List<IndexBase> getIndexBases() {
        return indexBases;
    }

    public void setIndexBases(final List<IndexBase> indexBases) {
        this.indexBases = indexBases;
    }

    public void addToIndexBases(final IndexBase indexBase) {
        if (indexBase == null || getIndexBases().contains(indexBase)) {
            return;
        }
        indexBase.clearIndex();
        indexBase.setIndex(this);
        getIndexBases().add(indexBase);

    }

    public void removeFromIndexBases(final IndexBase indexBase) {
        if (indexBase == null || !getIndexBases().contains(indexBase)) {
            return;
        }
        indexBase.setIndex(null);
        getIndexBases().remove(indexBase);
    }

    @Hidden
    public BigDecimal getIndexValueForDate(LocalDate date) {
        if (date != null) {
            IndexValue indexValue = indices.findIndexValueForDate(this, date);
            return indexValue == null ? null : indexValue.getValue();
        }
        return null;
    }

    @Hidden
    public BigDecimal getRebaseFactorForDates(LocalDate baseIndexStartDate, LocalDate nextIndexStartDate) {
        IndexValue nextIndexValue = indices.findIndexValueForDate(this, nextIndexStartDate);
        // TODO: check efficiency.. seems to retrieve every single index value
        // for the last 15 years...
        if (nextIndexValue != null) {
            BigDecimal rebaseFactor = nextIndexValue.getIndexBase().getFactorForDate(baseIndexStartDate);
            return rebaseFactor;
        }
        return null;
    }

    @Hidden
    public void initialize(IndexationCalculator indexationCalculator, LocalDate baseIndexStartDate, LocalDate nextIndexStartDate) {
        indexationCalculator.setBaseIndexValue(getIndexValueForDate(baseIndexStartDate));
        indexationCalculator.setNextIndexValue(getIndexValueForDate(nextIndexStartDate));
        indexationCalculator.setRebaseFactor(getRebaseFactorForDates(baseIndexStartDate, nextIndexStartDate));
    }

    
    // {{ injected
    private Indices indices;

    public void injectIndices(Indices indices) {
        this.indices = indices;
    }
    // }}

    
    // {{ Comparable impl
    @Override
    @Hidden
    public int compareTo(Index o) {
        return o.getReference().compareTo(this.getReference());
    }
    // }}

}
