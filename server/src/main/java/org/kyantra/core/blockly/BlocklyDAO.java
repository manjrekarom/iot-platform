package org.kyantra.core.blockly;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.kyantra.common.BaseDAO;

import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlocklyDAO extends BaseDAO<BlocklyBean> {
    static BlocklyDAO instance = new BlocklyDAO();
    public static BlocklyDAO getInstance(){ return instance; }

    public BlocklyBean add(BlocklyBean currentBlockly) {
        Session session = getService().getSessionFactory().openSession();
        session.beginTransaction();
        session.save(currentBlockly);
        session.getTransaction().commit();
        session.close();
        return currentBlockly;
    }

    @Override
    public BlocklyBean update(Integer id, BlocklyBean blocklyBean) {
        return null;
    }

    @Override
    public void delete(BlocklyBean blocklyBean) {

    }

    public BlocklyBean get(Integer id) {
        Session session = getService().getSessionFactory().openSession();
        BlocklyBean blocklyBean = session.get(BlocklyBean.class,id);
        session.close();
        return blocklyBean;
    }

    public void delete(Integer id) {
        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        BlocklyBean blockly = session.get(BlocklyBean.class, id);
        session.delete(blockly);
        tx.commit();
        session.close();
    }

    public BlocklyBean update(int id, int blockId, String xml) {
        if(id <= 0)
            return null;
        Session session = getService().getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        BlocklyBean blockly = session.get(BlocklyBean.class, id);
        blockly.setBlockId(blockId);
        blockly.setXml(xml);
        tx.commit();
        session.close();
        return blockly;
    }

    /*public BlocklyBean getByBlockIdAndType(int blockId, String blockType) {
        Session session = getService().getSessionFactory().openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<BlocklyBean> criteria = builder.createQuery(BlocklyBean.class);

        Root<BlocklyBean> root = criteria.from(BlocklyBean.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("blockId"), blockId).and(builder.equal(root.get("blockType"), blockType)));

        Query query = session.createQuery(criteria);
        BlocklyBean blocklyBean = (BlocklyBean) query.getSingleResult();

        session.close();
        return blocklyBean;
    }*/
    public Set<BlocklyBean> getByThingId(int thingId) {
        Session session = getService().getSessionFactory().openSession();
        String ql = "from BlocklyBean where parentThing_Id="+thingId;
        Query query = session.createQuery(ql);
        List<BlocklyBean> list = query.getResultList();
        session.close();
        return new HashSet<>(list);
    }

    public BlocklyBean getByBlockId(int blockId) {
        Session session = getService().getSessionFactory().openSession();
        BlocklyBean blocklyBean = session.byNaturalId(BlocklyBean.class)
                   .using("blockId",blockId)
                   .load();
        session.close();
        return blocklyBean;
    }
}
