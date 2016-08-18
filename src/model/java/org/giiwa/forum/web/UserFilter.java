package org.giiwa.forum.web;

import org.giiwa.core.bean.Helper.V;
import org.giiwa.forum.bean.IP;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.IFilter;
import org.giiwa.framework.web.Model;

public class UserFilter implements IFilter {

  @Override
  public boolean after(Model m) {
    User u = m.getUser();
    if (u != null && u.getString("ipfrom") == null) {
      IP.Address a = IP.get(m.getRemoteHost());
      if (a != null) {
        V v = V.create("ipfrom", a.lo + " " + a.lc);
        User.update(u.getId(), v);
      }
    }

    return true;
  }

  @Override
  public boolean before(Model m) {
    return true;
  }

}
