package org.giiwa.forum.web.admin;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.giiwa.core.json.JSON;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.forum.bean.Expose;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

public class expose extends Model {

  @Path(login = true, access = "access.forum.admin")
  public void onGet() {
    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    Beans<Expose> bs = Expose.load(W.create().sort("created", -1), s, n);

    this.set(bs, s, n);
    this.show("/admin/expose.index.html");

  }

  @Path(path = "delete", login = true, access = "access.forum.admin")
  public void delete() {
    JSON jo = new JSON();
    String id = this.getString("id");
    Expose.delete(id);
    jo.put(X.STATE, 200);
    this.response(jo);

  }

}
