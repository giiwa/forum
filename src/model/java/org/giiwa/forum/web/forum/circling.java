package org.giiwa.forum.web.forum;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.giiwa.core.bean.Bean.V;
import org.giiwa.forum.bean.Circling;
import org.giiwa.forum.bean.Follower;
import org.giiwa.forum.bean.Log;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;
import org.giiwa.tinyse.se.SE;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.sf.json.JSONObject;

public class circling extends Model {

  @Path(login = true)
  public void onGet() {

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    long uid = this.getLong("uid", login.getId());
    User u = User.loadById(uid);
    this.set("u", u);

    String name = this.getString("q");
    Beans<Circling> bs = null;
    if (!X.isEmpty(name) && path == null) {
      /**
       * searching
       */
      bs = new Beans<Circling>();
      Query q1 = SE.parse(name, new String[] { "name", "nickname", "memo" });
      TopDocs docs = SE.search("circling", q1);
      ScoreDoc[] dd = docs.scoreDocs;
      bs.setTotal(docs.totalHits);
      List<Circling> list = new ArrayList<Circling>();
      bs.setList(list);

      int min = s + n;
      int i = s;
      while (i < min && i < dd.length) {

        ScoreDoc d = dd[i];
        Long id = (Long) SE.get(d.doc);
        if (id != null) {
          Circling e = Circling.load((long) id);
          list.add(e);
        }
        i++;
        if (list.size() >= n) {
          break;
        }
      }

      this.set("q", name);
    } else {
      BasicDBObject q = new BasicDBObject("owner", uid);
      bs = Circling.load(q, new BasicDBObject("updated", -1), s, n);
    }
    
    this.set(bs, s, n);
    this.query.path("/forum/circling");

    this.show("/forum/circling.index.html");
  }

  @Path(path = "delete", login = true)
  public void delete() {
    long id = this.getLong("id");
    Circling.delete(id);
    JSONObject jo = new JSONObject();
    jo.put(X.STATE, 200);
    this.response(jo);
  }

  @Path(path = "deny", login = true)
  public void _deny() {
    long cid = this.getLong("cid");
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

  @Path(path = "create", login = true)
  public void create() {

    if (method.isPost()) {
      JSONObject jo = this.getJSON();
      V v = V.create().copy(jo, "name");
      String memo = this.getHtml("memo");
      v.set("memo", memo);

      String photo = this.getString("photo");
      v.set("photo", photo);

      v.set("access", this.getString("access"));
      v.set("user_state", this.getString("user_state"));
      v.set("owner", login.getId());

      long id = Circling.create(v);

      Follower.create(login.getId(), id, V.create("state", "owner"));

      this.set(X.MESSAGE, lang.get("create.success"));

      this.redirect("/forum/circling");

      return;
    }

    this.show("/forum/circling.create.html");
  }

  @Path(path = "user", login = true)
  public void user() {
    String cid = this.getString("cid");
    String state = this.getString("state");

    this.show("/forum/circling.user.html");

  }

  @Path(path = "edit", login = true)
  public void edit() {
    long cid = this.getLong("cid");
    Circling c = Circling.load(cid);
    Follower f = c.getFollower(login);
    if (!X.isSame("owner", f.getState())) {
      deny();
      return;
    }
    if (method.isPost()) {
      JSONObject jo = this.getJSON();
      V v = V.create().copy(jo, "name");

      String memo = this.getHtml("memo");
      v.set("memo", memo);

      String photo = this.getString("photo");
      v.set("photo", photo);

      v.set("access", this.getString("access"));
      v.set("user_state", this.getString("user_state"));

      Circling.update(cid, v);

      Circling.repair(cid);

      this.redirect("/forum/circling");
      return;
    }

    this.set(c.getJSON());
    this.set("cid", cid);
    this.set("c", c);
    this.show("/forum/circling.edit.html");
  }

}
