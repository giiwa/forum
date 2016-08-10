package org.giiwa.forum.web.admin;

import java.util.regex.Pattern;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.conf.Global;
import org.giiwa.core.bean.X;
import org.giiwa.forum.bean.Circling;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

public class circling extends Model {

  @Path(login = true, access = "access.forum.admin")
  public void onGet() {
    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    W q = W.create();
    String name = this.getString("name");

    if (!X.isEmpty(name) && path == null) {
      q.and("name", name, W.OP_LIKE);
      this.set("name", name);
    }
    Beans<Circling> bs = Circling.load(q.sort("created", -1), s, n);
    this.set(bs, s, n);

    this.show("/admin/circling.index.html");

  }

  @Path(path = "setting", login = true, access = "access.config.admin")
  public void setting() {
    if (method.isPost()) {
      Global.setConfig("forum.image.server", this.getString("forum_image_server"));
    }

    this.show("/admin/circling.setting.html");
  }

  @Path(path = "update", login = true, access = "access.forum.admin")
  public void update() {
    long id = this.getLong("id");
    if (this.getString("state") != null) {
      int i = this.getInt("state");
      Circling.update(id, V.create("state", i));
      Circling f = Circling.load(id);
      this.set("f", f);
      this.show("/admin/circling.item.html");
      return;
    } else if (this.getString("deleted") != null) {
      int i = this.getInt("deleted");
      Circling.update(id, V.create("deleted", i));
      Circling f = Circling.load(id);
      this.set("f", f);
      this.show("/admin/circling.item.html");
    } else if (this.getString("repair") != null) {
      Circling.repair(id);
      Circling f = Circling.load(id);
      this.set("f", f);
      this.show("/admin/circling.item.html");
    }
  }

}
