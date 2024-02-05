package model;

import jakarta.persistence.*;

@Entity
public class RcCompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private RcCompany rcCompany;

    public RcCompany getRcCompany() {
        return rcCompany;
    }

    public void setRcCompany(RcCompany rcCompany) {
        this.rcCompany = rcCompany;
    }

}
