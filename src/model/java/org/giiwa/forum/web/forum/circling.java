package org.giiwa.forum.web.forum;

import java.util.List;
import java.util.regex.Pattern;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.giiwa.core.base.Html;
import org.giiwa.core.bean.Bean.V;
import org.giiwa.forum.bean.Circling;
import org.giiwa.forum.bean.Log;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;
import org.jsoup.nodes.Element;

import com.mongodb.BasicDBObject;

import net.sf.json.JSONObject;

public class circling extends Model {

  @Path(login = true, access = "access.forum.admin")
  public void onGet() {
    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    long uid = this.getLong("uid", login.getId());
    User u = User.loadById(uid);
    this.set("u", u);

    BasicDBObject q = new BasicDBObject("owner", uid);
    String name = this.getString("name");

    if (!X.isEmpty(name) && path == null) {
      Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
      q.append("name", pattern);
      this.set("name", name);
    }
    Beans<Circling> bs = Circling.load(q, new BasicDBObject("updated", -1), s, n);
    this.set(bs, s, n);
    this.query.path("/forum/circling");

    this.show("/forum/circling.index.html");
  }

  @Path(path = "delete", login = true, access = "access.forum.admin")
  public void delete() {
    String id = this.getString("id");
    Circling.delete(id);
    JSONObject jo = new JSONObject();
    jo.put(X.STATE, 200);
    this.response(jo);
  }

  @Path(path = "deny", login = true, access = "access.forum.admin")
  public void _deny() {
    String cid = this.getString("cid");
    long uid = this.getLong("uid");

    Circling c = Circling.load(cid);
    JSONObject jo = new JSONObject();
    if (c.getOwner() == login.getId()) {
      Log.create(V.create("data", "forbidden").set("cid", cid).set("uid", uid));
      jo.put(X.STATE, 200);
      jo.put(X.MESSAGE, lang.get("save.success"));
    } else {
      jo.put(X.STATE, 201);
      jo.put(X.MESSAGE, lang.get("access.deny"));
    }
    this.response(jo);
  }

  @Path(path = "create", login = true, access = "access.corum.admin")
  public void create() {

    if (method.isPost()) {
      JSONObject jo = this.getJSON();
      V v = V.create().copy(jo, "name");
      String memo = this.getHtml("memo");
      v.set("memo", memo);
      // Html h = Html.create(memo);
      // List<Element> list = h.get("img");

      String photo = this.getString("photo");
      v.set("photo", photo);

      v.set("public", X.isSame("on", this.getString("public")) ? "yes" : "no");
      v.set("right_view", X.isSame("on", this.getString("right_view")) ? "yes" : "no");
      v.set("right_post", X.isSame("on", this.getString("right_post")) ? "yes" : "no");
      v.set("owner", login.getId());

      String id = Circling.create(v);

      this.set(X.MESSAGE, lang.get("create.success"));
      onGet();

      return;
    }

    this.show("/forum/circling.create.html");
  }

  @Path(path = "edit", login = true, access = "access.forum.admin")
  public void edit() {
    String id = this.getString("id");
    if (method.isPost()) {
      JSONObject jo = this.getJSON();
      V v = V.create().copy(jo, "name");

      Circling.update(id, v);

      this.set(X.MESSAGE, lang.get("save.success"));
      onGet();
      return;
    }

    Circling d = Circling.load(id);
    this.set(d.getJSON());
    this.set("id", id);
    this.set("c", d);
    this.show("/admin/circling.edit.html");
  }

}
