package org.kyantra.core.rule;

import com.google.gson.annotations.Expose;
import org.kyantra.core.sns.SnsBean;
import org.kyantra.core.actuator.ActuatorBean;
import org.kyantra.core.thing.ThingBean;

import javax.persistence.*;

@Entity
@Table(name = "rule")//, uniqueConstraints = @UniqueConstraint(columnNames={"data", "topic"}))
public class RuleBean {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Expose
    @Column(name = "`name`", unique = true)//, unique = true)
    String name;

    @Expose
    @Column(name = "`description`", length = 50)
    String description;

    //TODO: To have an association with the user who created this
//    @Expose
//    @Column(name="created_by")
//    String createdBy;

    @Expose
    @Column(name = "`data`")
    String data;

    @Expose
    @Column(name = "`condition`")
    String condition;

    @Expose
    @Column(name = "`type`", length = 25)
    String type;

    @Expose
    @OneToOne(mappedBy = "parentRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private SnsBean snsAction;

    @Expose
    @OneToOne(mappedBy = "parentRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private ActuatorBean actuatorAction;

    @ManyToOne(fetch = FetchType.EAGER)
    private ThingBean parentThing;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }

    public ThingBean getParentThing() {
        return parentThing;
    }

    public void setParentThing(ThingBean parentThing) {
        this.parentThing = parentThing;
    }

    public SnsBean getSnsAction() {
        return snsAction;
    }

    public ActuatorBean getActuatorAction() {
        return actuatorAction;
    }

    public SnsBean addSNSAction(SnsBean snsAction) {
        //TODO: Perform checks for presence of other actions
        //For simplicity one rule has one action only; other fields are null
        snsAction.setParentRule(this);
        this.snsAction = snsAction;
        return snsAction;
    }

    public void removeSNSAction() {
        if(this.snsAction != null) {
            snsAction.setParentRule(null);
            this.snsAction = null;
        }
    }

    public ActuatorBean addActuatorAction(ActuatorBean actuatorAction) {
        //TODO: Perform checks for presence of other actions
        //For simplicity one rule has one action only; other fields are null
        actuatorAction.setParentRule(this);
        this.actuatorAction = actuatorAction;
        return actuatorAction;
    }

    public void removeActuatorAction() {
        if(this.actuatorAction != null) {
            actuatorAction.setParentRule(null);
            this.actuatorAction = null;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
