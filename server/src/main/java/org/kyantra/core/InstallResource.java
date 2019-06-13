package org.kyantra.core;

import org.glassfish.jersey.server.mvc.Template;
import org.hibernate.Session;
import org.kyantra.core.auth.RightsBean;
import org.kyantra.common.RoleEnum;
import org.kyantra.core.auth.SessionBean;
import org.kyantra.core.thing.ThingBean;
import org.kyantra.core.unit.UnitBean;
import org.kyantra.core.user.UserBean;
import org.kyantra.core.auth.RightsDAO;
import org.kyantra.core.thing.ThingDAO;
import org.kyantra.core.unit.UnitDAO;
import org.kyantra.core.user.UserDAO;
import org.kyantra.common.BaseResource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Siddhesh Prabhugaonkar on 21-11-2017.
 */
@Path("install")
public class InstallResource extends BaseResource {

    InstallResource(@Context SecurityContext sc,
                  @Context HttpServletRequest request) {
        super(sc, request);
    }

    @GET
    @Template(name = "/index.ftl") // TODO: 21-11-2017
    public String install() throws URISyntaxException {
        final Map<String, Object> map = new HashMap<>();
        map.put("active","home");

        List<UserBean> users = UserDAO.getInstance().list(1,10);
        List<UnitBean> units = UnitDAO.getInstance().list(1,10);
        if (users.size() == 0 && units.size() == 0){
            UserBean user1 = new UserBean();
            user1.setName("Super Admin");
            user1.setEmail("admin@e-yantra.org");
            user1.setPassword("1234567");
            UserDAO.getInstance().add(user1);

            UserBean user2 = new UserBean();
            user2.setName("Test User");
            user2.setEmail("test@e-yantra.org");
            user2.setPassword("123456");
            UserDAO.getInstance().add(user2);

            UnitBean unit1 = new UnitBean();
            unit1.setUnitName("My Main Unit");
            unit1.setDescription("sample description");
            unit1.setPhoto("http://p0.static.bookstruck.in.s3.amazonaws.com/images/b5b8f6d9aa9049df91f9b5d45418b073.png\n");
            UnitDAO.getInstance().add(unit1);

            UnitBean unit2 = new UnitBean();
            unit2.setUnitName("Subunit 1");
            unit2.setDescription("subunit");
            unit2.setParent(unit1);
            UnitDAO.getInstance().add(unit2);

            UnitBean unit3 = new UnitBean();
            unit3.setUnitName("Bearer");
            unit3.setDescription("Bearer");
            unit3.setParent(unit1);
            UnitDAO.getInstance().add(unit3);

            ThingBean thing = new ThingBean();
            thing.setName("Thing 1");
            thing.setDescription("");
            thing.setParentUnit(unit1);
            ThingDAO.getInstance().add(thing);

            RightsBean rights = new RightsBean();
            rights.setUser(user1);
            rights.setUnit(unit1);
            rights.setRole(RoleEnum.ALL);
            RightsDAO.getInstance().add(rights);

            SessionBean session1 = new SessionBean();
            session1.setUser(user1);
            session1.setToken("e046b896-40ed-4088-bdb7-e15360e5977b");

            SessionBean session2 = new SessionBean();
            session2.setUser(user1);
            session2.setToken("c6c1529e-c35b-49aa-9232-3cf53350551f");

            SessionBean session3 = new SessionBean();
            session3.setUser(user1);
            session3.setToken("6916c097-74a7-4f99-82bf-2bf8271550c9");

            Session session = getSession();
            session.save(session1);
            session.save(session2);
            session.save(session3);
            session.close();
        }

        return "Database seeded";
    }
}
