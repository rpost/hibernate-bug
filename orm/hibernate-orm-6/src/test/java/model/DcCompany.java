package model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

@Entity
public class DcCompany extends AbstractDcCompany {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RcCompany rcCompany;

    public RcCompany getRcCompany() {
        return rcCompany;
    }

    public void setRcCompany(RcCompany rcCompany) {
        this.rcCompany = rcCompany;
    }

}
