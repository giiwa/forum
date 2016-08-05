package org.giiwa.forum.web.forum;

import java.util.ArrayList;
import java.util.List;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.X;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.giiwa.forum.bean.Circling;
import org.giiwa.forum.bean.Follower;
import org.giiwa.forum.bean.Log;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;
import org.giiwa.tinyse.se.SE;

import net.sf.json.JSONArray;
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
    Beans<Circling> bs = new Beans<Circling>();
    if (!X.isEmpty(name) && X.isEmpty(path)) {
      /**
       * searching
       */
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
        long id = X.toLong(SE.get(d.doc), -1);
        if (id > -1) {
          Circling e = Circling.load(id);
          // SE.highlight();
          String s1 = SE.highlight(d.doc, "name", q1, null);
          if (s1 != null) {
            e.set("name", s1);
          }

          s1 = SE.highlight(d.doc, "memo", q1, null);
          if (s1 != null) {
            e.set("memo", s1);
          }

          s1 = SE.highlight(d.doc, "nickname", q1, null);
          if (s1 != null) {
            e.set("owner_nickname", s1);
          }

          list.add(e);
        }
        i++;
        if (list.size() >= n) {
          break;
        }
      }

      this.set("q", name);
    } else {
      W q = W.create("uid", uid);
      Beans<Follower> b1 = Follower.load(q.sort("updated", -1), s, n);
      if (b1 != null) {
        bs.setTotal(b1.getTotal());
        if (b1.getList() != null) {
          List<Circling> l1 = new ArrayList<Circling>();
          bs.setList(l1);
          for (Follower f1 : b1.getList()) {
            l1.add(f1.getCircling_obj());
          }
        }
      }
    }

    this.set(bs, s, n);
    this.query.path("/forum/circling");

    this.show("/forum/circling.index.html");
  }

  @Path(path = "get", login = true)
  public void get() {

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    long uid = this.getLong("uid", login.getId());
    User u = User.loadById(uid);
    this.set("u", u);

    JSONObject jo = new JSONObject();

    String name = this.getString("q");
    if (!X.isEmpty(name)) {
      /**
       * searching
       */
      Query q1 = SE.parse(name, new String[] { "name", "nickname", "memo" });
      TopDocs docs = SE.search("circling", q1);
      ScoreDoc[] dd = docs.scoreDocs;

      jo.put("tptal", docs.totalHits);
      JSONArray arr = new JSONArray();

      int min = s + n;
      int i = s;
      while (i < min && i < dd.length) {

        ScoreDoc d = dd[i];
        long id = X.toLong(SE.get(d.doc), -1);
        if (id > -1) {
          Circling e = Circling.load(id);
          // SE.highlight();
          String s1 = SE.highlight(d.doc, "name", q1, null);
          if (s1 != null) {
            e.set("name", s1);
          }

          s1 = SE.highlight(d.doc, "memo", q1, null);
          if (s1 != null) {
            e.set("memo", s1);
          }

          s1 = SE.highlight(d.doc, "nickname", q1, null);
          if (s1 != null) {
            e.set("owner_nickname", s1);
          }

          arr.add(e.getJSON());
        }
        i++;
        if (arr.size() >= n) {
          break;
        }
      }
      jo.put("list", arr);
      jo.put("name", name);
      jo.put("s", s);
      jo.put("n", n);
      jo.put("hasmore", arr.size() >= n);
      jo.put(X.STATE, 200);
      jo.put(X.MESSAGE, "ok");

    } else {
      W q = W.create("uid", uid);
      Beans<Follower> b1 = Follower.load(q.sort("updated", -1), s, n);
      if (b1 != null) {
        jo.put("total", Follower.count(q));
        if (b1.getList() != null) {
          JSONArray arr = new JSONArray();
          for (Follower f1 : b1.getList()) {
            arr.add(f1.getCircling_obj().getJSON());
          }
          jo.put("list", arr);
          jo.put("hasmore", arr.size() >= n);
        } else {
          jo.put("hasmore", false);
        }
      } else {
        jo.put("hasmore", false);
      }
      jo.put("s", s);
      jo.put("n", n);
      jo.put("name", name);
      jo.put(X.STATE, 200);
      jo.put(X.MESSAGE, "ok");

    }

    this.response(jo);
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

      Follower.create(login.getId(), id, V.create("state", "owner").set("name", login.getNickname()));

      this.set(X.MESSAGE, lang.get("create.success"));

      this.redirect("/forum/circling");

      return;
    }

    this.show("/forum/circling.create.html");
  }

  @Path(path = "user", login = true)
  public void user() {
    long cid = this.getLong("cid");
    this.set("cid", cid);
    String state = this.getString("state");

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    Follower f = Follower.load(W.create("cid", cid).and("uid", login.getId()));

    if (f != null && X.isSame("owner", f.getState())) {
      W q = W.create("cid", cid);
      if (!X.isEmpty(state)) {
        q.and("state", state);
        this.set("state", state);
      }
      String name = this.getString("name");
      if (!X.isEmpty(name)) {
        q.and("name", name, W.OP_LIKE);
        this.set("name", name);
      }
      Beans<Follower> bs = Follower.load(q, s, n);

      this.set(bs, s, n);
    }

    this.show("/forum/circling.user.html");

  }

  @Path(path = "follow", login = true)
  public void follow() {
    long cid = this.getLong("cid");
    Circling c = Circling.load(cid);

    JSONObject jo = new JSONObject();
    if (!X.isEmpty(this.getString("follow"))) {
      Follower.create(login.getId(), cid, V.create("state", c.getUser_state()).set("name", login.getNickname()));
    } else if (!X.isEmpty(this.getString("delete"))) {
      Follower f = Follower.load(W.create("cid", cid).and("uid", login.getId()));
      if (f != null && X.isSame("owner", f.getState())) {
        long uid = this.getLong("delete");
        Follower.delete(W.create("uid", uid).and("cid", cid));
      }
    } else if (!X.isEmpty(this.getString("state"))) {
      Follower f = Follower.load(W.create("cid", cid).and("uid", login.getId()));
      if (f != null && X.isSame("owner", f.getState())) {
        String state = this.getString("state");
        String id = this.getString("id");
        Follower.update(W.create(X.ID, id), V.create("state", state));
      }
    }

    Circling.repair(cid);

    jo.put(X.STATE, 200);
    jo.put(X.MESSAGE, "ok");
    this.response(jo);
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
      v.set("se_timestamp", 0);

      Circling.update(cid, v);

      if (X.isSame("accepted", this.getString("user_state"))) {
        /**
         * auto accept all pending that create before
         */
        Follower.update(W.create("cid", cid).and("state", "pending"), V.create("state", "accepted"));
      }

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
