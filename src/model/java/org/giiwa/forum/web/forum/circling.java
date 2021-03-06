package org.giiwa.forum.web.forum;

import java.util.ArrayList;
import java.util.List;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.X;
import org.giiwa.core.conf.Global;
import org.giiwa.core.json.JSON;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.giiwa.forum.bean.Circling;
import org.giiwa.forum.bean.Follower;
import org.giiwa.forum.bean.Log;
import org.giiwa.forum.bean.Topic;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;
import org.giiwa.tinyse.se.SE;

public class circling extends Model {

  public void onGet() {

    int s = this.getInt("s");
    int n = this.getInt("n", 10, "number.per.page");

    this.set("me", getUser());
    long uid = this.getLong("uid", login == null ? -1 : login.getId());

    User u = User.loadById(uid);
    this.set("u", u);

    /**
     * load my circlings
     */
    if (u != null) {
      W q = W.create("uid", uid);
      Beans<Follower> b1 = Follower.load(q.sort("updated", -1), 0, 20);
      if (b1 != null) {
        if (b1.getList() != null) {
          List<Circling> l1 = new ArrayList<Circling>();
          for (Follower f1 : b1.getList()) {
            Circling c = f1.getCircling_obj();
            if (c.getInt("deleted") != 1)
              l1.add(c);
          }
          this.set("mycirclings", l1);
        }
      }
    }

    /**
     * load hot circlings
     */
    {
      W q = W.create().and("access", "private", W.OP.neq).and("deleted", 1, W.OP.neq);

      Beans<Circling> b1 = Circling.load(q.sort("updated", -1), 0, 20);
      if (b1 != null) {
        this.set("hotcirclings", b1.getList());
      }
    }

    if (u != null) {

      W w1 = W.create();
      W q = W.create("uid", uid).and(W.create("state", "owner").or("state", "accepted"));
      int s1 = 0;
      Beans<Follower> b1 = Follower.load(q.sort("updated", -1), s1, 100);
      while (b1 != null && b1.getList() != null && b1.getList().size() > 0) {
        for (Follower f1 : b1.getList()) {
          w1.or("cid", f1.getCid());
        }
        s1 += b1.getList().size();
        if (b1.getList().size() < 100) {
          break;
        } else {
          b1 = Follower.load(q.sort("updated", -1), s1, 100);
        }
      }

      // load all topics
      Beans<Topic> b2 = Topic.load(W.create().and(w1).and("parent", 0).sort("updated", -1), s, n);
      this.set(b2, s, n);

    } else {
      // load public topic
      Beans<Circling> b1 = Circling
          .load(W.create().and("access", "private", W.OP.neq).and("deleted", 1, W.OP.neq).sort("updated", -1), 0, 100);
      W w1 = W.create();
      if (b1 != null && b1.getList() != null) {
        for (Circling c1 : b1.getList()) {
          w1.or("cid", c1.getId());
        }
      }
      Beans<Topic> b2 = Topic.load(W.create().or(w1).and("parent", 0).sort("updated", -1), s, n);
      this.set(b2, s, n);

    }

    this.query.path("/forum/circling");

    this.show("/forum/circling.index.html");

  }

  @Path(path = "search")
  public void search() {

    String q1 = this.getString("q");
    if (X.isEmpty(q1)) {
      this.redirect("/forum/circling");
      return;
    }

    this.set("me", this.getUser());
    this.set("u", this.getUser());

    /**
     * load my circlings
     */
    if (login != null) {
      W q = W.create("uid", login.getId());
      Beans<Follower> b1 = Follower.load(q.sort("updated", -1), 0, 20);
      if (b1 != null) {
        if (b1.getList() != null) {
          List<Circling> l1 = new ArrayList<Circling>();
          for (Follower f1 : b1.getList()) {
            Circling c = f1.getCircling_obj();
            if (c.getInt("deleted") != 1)
              l1.add(c);
          }
          this.set("mycirclings", l1);
        }
      }
    }

    /**
     * load hot circlings
     */
    {
      W q = W.create().and("access", "private", W.OP.neq);

      Beans<Circling> b1 = Circling.load(q.sort("updated", -1).and("deleted", 1, W.OP.neq), 0, 20);
      if (b1 != null) {
        this.set("hotcirclings", b1.getList());
      }
    }

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    this.set("q", q1);

    Beans<Circling> bs = new Beans<Circling>();
    Query q2 = SE.parse(q1, new String[] { "name", "nickname", "memo" });
    TopDocs docs = SE.search("circling", q2);
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
        if (e.getInt("deleted") != 1) {
          // SE.highlight();
          String s1 = SE.highlight(d.doc, "name", q2, null);
          if (s1 != null) {
            e.set("name", s1);
          }

          s1 = SE.highlight(d.doc, "memo", q2, null);
          if (s1 != null) {
            e.set("memo", s1);
          }

          s1 = SE.highlight(d.doc, "nickname", q2, null);
          if (s1 != null) {
            e.set("owner_nickname", s1);
          }

          list.add(e);
        }
      }
      i++;
      if (list.size() >= n) {
        break;
      }
    }

    this.set(bs, s, n);

    this.query.path("/forum/circling/search");
    this.show("/forum/circling.search.html");

  }

  @Path(path = "get", login = true)
  public void get() {

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    long uid = this.getLong("uid", login.getId());
    User u = User.loadById(uid);
    this.set("u", u);

    JSON jo = new JSON();
    int row = 0;
    String name = this.getString("q");
    if (!X.isEmpty(name)) {
      /**
       * searching
       */
      Query q1 = SE.parse(name, new String[] { "name", "nickname", "memo" });
      TopDocs docs = SE.search("circling", q1);
      ScoreDoc[] dd = docs.scoreDocs;

      jo.put("tptal", docs.totalHits);
      List<JSON> arr = new ArrayList<JSON>();

      int min = s + n;
      int i = s;
      while (i < min && i < dd.length) {

        ScoreDoc d = dd[i];
        long id = X.toLong(SE.get(d.doc), -1);
        if (id > -1) {
          Circling e = Circling.load(id);
          if (e.getInt("deleted") != 1) {
            // SE.highlight();
            String s1 = SE.highlight(d.doc, "name", q1, null);
            if (s1 != null) {
              e.set("name", s1);
            }

            s1 = SE.highlight(d.doc, "memo", q1, null);
            if (s1 != null) {
              e.set("memo", s1);
            }

            row = _refine(e, row);
            arr.add(e.getJSON());
          }
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
          List<JSON> arr = new ArrayList<JSON>();
          for (Follower f1 : b1.getList()) {
            Circling c1 = f1.getCircling_obj();
            if (c1.getInt("deleted") != 1) {
              row = _refine(c1, row);
              arr.add(c1.getJSON());
            }
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

  private int _refine(Circling e, int row) {
    e.put("photo", Global.getString("forum.image.server", "") + e.get("photo"));
    User u = e.getOwner_obj();
    e.put("nickname", u.getNickname() == null ? u.getName() : u.getNickname());
    e.set("updated", lang.past(e.getLong("updated")));
    e.set("created", lang.format(e.getLong("created"), "yy-MM-dd HH:mm"));
    e.set("row", row);
    return row + 1;
  }

  @Path(path = "delete", login = true)
  public void delete() {
    long id = this.getLong("id");
    Circling.delete(id);
    JSON jo = new JSON();
    jo.put(X.STATE, 200);
    this.response(jo);
  }

  @Path(path = "deny", login = true)
  public void _deny() {
    long cid = this.getLong("cid");
    long uid = this.getLong("uid");

    Circling c = Circling.load(cid);
    JSON jo = new JSON();
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
      JSON jo = this.getJSON();
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
        q.and("name", name, W.OP.like);
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

    JSON jo = new JSON();
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
      JSON jo = this.getJSON();
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
