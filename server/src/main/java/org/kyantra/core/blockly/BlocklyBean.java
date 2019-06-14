package org.kyantra.core.blockly;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.NaturalId;
import org.kyantra.core.thing.ThingBean;

import javax.persistence.*;

@Entity
@Table(name = "blockly")
public class BlocklyBean {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Expose
    @NaturalId
    @Column(name = "`blockId`")
    Integer blockId;

    @Lob
    @Expose
    @Column(name = "`xml`")
    String xml;

    @ManyToOne(fetch = FetchType.EAGER)
    private ThingBean parentThing;

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public ThingBean getParentThing() {
        return parentThing;
    }

    public void setParentThing(ThingBean parentThing) {
        this.parentThing = parentThing;
    }
}
