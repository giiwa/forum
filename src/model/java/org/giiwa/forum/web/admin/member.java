package org.giiwa.forum.web.admin;

import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

public class member extends Model {
  @Path(login = true, access = "access.forum.admin")
  public void onGet() {
    String cid = this.getString("cid");
    this.show("/admin/member.index.html");
  }

}
