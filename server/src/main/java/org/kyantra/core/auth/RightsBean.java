package org.kyantra.core.auth;

import com.google.gson.annotations.Expose;
import org.kyantra.common.RoleEnum;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.user.UserBean;

import javax.persistence.*;

/**
 * A right represents authorization of a user.
 * It is of the form unit_id:role.
 * Roles are currently static and only read only and admin (full privileges).
 */
@Entity
@Table(name = "rights", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "unit_id"})})
public class RightsBean {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "unit_id")
    @Expose
    UnitBean unit;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @Expose
    UserBean user;

    @Enumerated(EnumType.STRING)
    @Expose
    RoleEnum role;

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

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
