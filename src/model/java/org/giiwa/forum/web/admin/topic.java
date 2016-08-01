package org.giiwa.forum.web.admin;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.X;
import org.giiwa.forum.bean.Circling;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

public class topic extends Model {

  @Path(login = true, access = "access.forum.admin")
  public void onGet() {

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    W q = W.create();
    long cid = this.getLong("cid");
    if (cid > 0) {
      Circling c = Circling.load(cid);
      this.set("c", c);
      q.and("cid", cid);
    }
    String name = this.getString("name");

    if (!X.isEmpty(name) && path == null) {
      q.and("name", name, W.OP_LIKE);
      this.set("name", name);
    }
    Beans<Circling> bs = Circling.load(q.sort("created", -1), s, n);
    this.set(bs, s, n);

    this.show("/admin/topic.index.html");

  }

}
