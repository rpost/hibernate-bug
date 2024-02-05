package model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DcCompanySeed extends AbstractDcCompany {

    @ManyToOne(optional = false)
    @JoinColumn(name = "invited_by", nullable = false)
    private RcCompanyUser invitedBy;

    public RcCompanyUser getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(RcCompanyUser invitedBy) {
        this.invitedBy = invitedBy;
    }

}
