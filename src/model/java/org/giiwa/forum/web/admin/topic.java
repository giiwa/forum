package org.giiwa.forum.web.admin;

import java.util.regex.Pattern;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.giiwa.forum.bean.Circling;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

import com.mongodb.BasicDBObject;

public class topic extends Model {

  @Path(login = true, access = "access.forum.admin")
  public void onGet() {

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    BasicDBObject q = new BasicDBObject();
    long cid = this.getLong("cid");
    if (cid > 0) {
      Circling c = Circling.load(cid);
      this.set("c", c);
      q.append("cid", cid);
    }
    String name = this.getString("name");

    if (!X.isEmpty(name) && path == null) {
      Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
      q.append("name", pattern);
      this.set("name", name);
    }
    Beans<Circling> bs = Circling.load(q, new BasicDBObject("created", -1), s, n);
    this.set(bs, s, n);

    this.show("/admin/topic.index.html");

  }

}
