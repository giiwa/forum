package org.giiwa.forum.web.admin;

import java.util.regex.Pattern;

import org.apache.lucene.search.Query;
import org.giiwa.core.bean.Bean.V;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.giiwa.forum.bean.Circling;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;
import org.giiwa.tinyse.se.SE;

import com.mongodb.BasicDBObject;

public class circling extends Model {

  @Path(login = true, access = "access.forum.admin")
  public void onGet() {
    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    BasicDBObject q = new BasicDBObject();
    String name = this.getString("q");

    Beans<Circling> bs = null;
    if (!X.isEmpty(name) && path == null) {
      Query q1 = SE.parse(name, new String[] { "name", "nickname", "memo" });
      SE.search("circling", q1, 100);
      
      Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
      q.append("name", pattern);
      this.set("q", name);
    } else {
      bs = Circling.load(q, new BasicDBObject("created", -1), s, n);
    }
    this.set(bs, s, n);

    this.show("/admin/circling.index.html");

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
