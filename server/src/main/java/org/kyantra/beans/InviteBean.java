package org.kyantra.beans;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "invites",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "unit_id"})})
public class InviteBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Expose
    Date sentDate;

    @Expose
    @Column(name="email")
    String email;

    @Expose
    @Column(name = "token", unique = true)
    String token;

    @Expose
    @ManyToOne
    @JoinColumn
    UserBean user;

    @Expose
    @ManyToOne
    @JoinColumn
    UnitBean unit;

    @Expose
    RoleEnum access;

    @Expose
    boolean accepted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public UnitBean getUnit() {
        return unit;
    }

    public void setUnit(UnitBean unit) {
        this.unit = unit;
    }

    public RoleEnum getAccess() {
        return access;
    }

    public void setAccess(RoleEnum access) {
        this.access = access;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
