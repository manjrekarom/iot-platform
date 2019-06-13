package org.kyantra.core.auth;

import com.google.gson.annotations.Expose;
import org.kyantra.core.user.UserBean;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "session")
public class SessionBean {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Expose
    @Column(name = "token", unique = true)
    String token;

    @Expose
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    UserBean user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
