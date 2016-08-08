package org.giiwa.forum.web.forum;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.giiwa.core.base.Html;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.X;
import org.giiwa.core.task.Task;
import org.giiwa.forum.bean.Circling;
import org.giiwa.forum.bean.Expose;
import org.giiwa.forum.bean.Follower;
import org.giiwa.forum.bean.Log;
import org.giiwa.forum.bean.Topic;
import org.giiwa.forum.bean.UserHelper;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;
import org.giiwa.tinyse.se.SE;
import org.jsoup.nodes.Element;

import net.sf.json.JSONObject;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class topic extends Model {

  public void onGet() {
    long cid = this.getLong("cid");
    Circling c = Circling.load(cid);
    this.set("cid", cid);
    this.set("c", c);

    login = getUser();
    this.set("me", login);
    Follower f = c.getFollower(login);
    if (c.isPrivate() && (f == null || !f.getPost())) {
      log.warn("deny to access the circling, user=" + login + ", circling=" + cid);
      this.deny("/forum", null);
      return;
    }

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    Beans<Topic> bs = null;
    String name = this.getString("name");
    if (!X.isEmpty(name) && X.isEmpty(path)) {
      bs = new Beans<Topic>();

      /**
       * searching
       */
      Query q1 = SE.parse(name, new String[] { "title", "nickname", "content" });
      TopDocs docs = SE.search("topic", q1);
      ScoreDoc[] dd = docs.scoreDocs;
      bs.setTotal(docs.totalHits);
      List<Topic> list = new ArrayList<Topic>();
      bs.setList(list);

      int min = s + n;
      int i = s;
      while (i < min && i < dd.length) {

        ScoreDoc d = dd[i];
        long id = X.toLong(SE.get(d.doc), -1);
        if (id > -1) {
          Topic e = Topic.load(id);
          // SE.highlight();
          String s1 = SE.highlight(d.doc, "title", q1, null);
          if (s1 != null) {
            e.set("title", s1);
          }

          list.add(e);
        }
        i++;
        if (list.size() >= n) {
          break;
        }
      }

      this.set("name", name);
    } else {
      bs = Topic.load(W.create("cid", cid).and("parent", 0).sort("top", -1).sort("updated", -1), s, n);
    }

    this.set(bs, s, n);
    this.query.path("/forum/topic");

    /**
     * get recommends
     */
    Beans<Circling> bs1 = Circling.load(login.getId(), W.create().sort("updated", -1), 0, 20);
    this.set("recommends", bs1 == null ? null : bs1.getList());

    this.show("/forum/topic.index.html");
  }

  @Path(path = "create", login = true)
  public void create() {
    final long cid = this.getLong("cid");
    this.set("cid", cid);
    Circling c = Circling.load(cid);
    Follower f1 = c.getFollower(login);
    if (!"public".equals(c.getAccess()) && (f1 == null || !f1.getPost())) {
      this.deny("/forum", null);
      return;
    }

    if (method.isPost()) {
      V v = V.create("cid", cid);
      v.set("title", this.getString("title"));

      /**
       * remove the "FFF" background, that may blink eyes
       */
      String content = this.getHtml("content").replaceAll("background-color:#FFFFFF;", "");
      v.set("content", content);
      List<Element> list = Html.create(content).getTags("img");
      int p = 0;
      if (list != null && list.size() > 0) {
        for (Element e : list) {
          String src = e.attr("src");
          if (src != null && src.startsWith("/repo")) {
            p = 1;
            break;
          }
        }
      }
      v.set("photo", p);

      v.set("owner", login.getId());
      v.set("parent", 0).set("top", 0);
      Topic.create(v);
      new Task() {

        @Override
        public void onExecute() {
          UserHelper.count(login.getId());

          Circling.repair(cid);
        }

      }.schedule(10);

      this.redirect("/forum/topic?cid=" + cid);
      return;
    }
    this.show("/forum/topic.create.html");
  }

  @Path(path = "update", login = true)
  public void update() {
    long cid = this.getLong("cid");
    Circling c = Circling.load(cid);
    long id = this.getLong("id");
    Topic t = Topic.load(id);
    if (c.getOwner() == login.getId()) {
      V v = V.create();
      if (this.getString("deleted") != null) {
        v.set("deleted", this.getInt("deleted"));
      }
      if (this.getString("up") != null) {
        if (Log.create(V.create("data", "up/down").set("tid", id).set("sid", this.sid()))) {
          v.set("up", t.getUp() + 1);
        }
      }
      if (this.getString("down") != null) {
        if (Log.create(V.create("data", "up/down").set("tid", id).set("sid", this.sid()))) {
          v.set("down", t.getDown() + 1);
        }
      }
      if (this.getString("top") != null) {
        v.set("top", this.getInt("top"));
      }
      if (this.getString("cream") != null) {
        v.set("cream", this.getInt("cream"));
      }

      Topic.update(id, v);
    }

    t = Topic.load(id);
    Topic p = t.getParent_obj();
    if (p == null) {
      /**
       * get recommends
       */
      Beans<Topic> bs1 = Topic
          .load(W.create("cid", t.getCid()).and("parent", 0).and(X.ID, id, W.OP_NEQ).sort("updated", -1), 0, 20);
      this.set("recommends", bs1 == null ? null : bs1.getList());
      this.set("t", t);
    } else {
      this.set("t", p);
    }

    this.set("f", t);
    this.set("c", c);
    this.show("/forum/topic.content.html");
  }

  @Path(path = "reply", login = true)
  public void reply() {

    long id = this.getLong("id");
    long refer = this.getLong("refer");

    Topic t = Topic.load(id);
    Topic last = t.getLast();

    Circling c = t.getCircling();
    Follower f1 = c.getFollower(login);
    if (!"public".equals(c.getAccess()) && (f1 == null || !f1.getPost())) {
      this.deny("/forum", null);
      return;
    }

    /**
     * remove the "FFF" background, that may blink eyes
     */
    String content = this.getHtml("content").replaceAll("background-color:#FFFFFF;", "");
    if (last == null || last.getOwner() != login.getId() || !X.isSame(content, last.getContent())) {
      V v = V.create("parent", id);
      v.set("cid", t.getCid());
      v.set("content", content);
      v.set("floor", last == null ? 1 : last.getFloor() + 1);
      v.set("owner", login.getId());
      if (!X.isEmpty(refer)) {
        v.set("refer", refer);
      }
      Topic.create(v);
      Topic.update(id, V.create("replies", t.getReplies() + 1));

      // TODO, remove later
      Topic.load(id).repair();

      new Task() {

        @Override
        public void onExecute() {
          UserHelper.count(login.getId());
        }

      }.schedule(10);

    }

    this.redirect("/forum/topic/detail?id=" + id);
  }

  @Path(path = "edit", login = true)
  public void edit() {
    long id = this.getLong("id");
    Topic t = Topic.load(id);

    Circling c = t.getCircling();
    Follower f = c.getFollower(login);
    if (c.isPrivate() && (f == null || !f.getPost())) {
      log.warn("deny to access the circling, user=" + login + ", circling=" + c.getId());
      this.deny();
      return;
    }

    if (method.isPost()) {
      V v = V.create();
      v.set("title", this.getString("title"));
      String content = this.getHtml("content").replaceAll("background-color:#FFFFFF;", "");
      v.set("content", content);
      List<Element> list = Html.create(content).getTags("img");
      int p = 0;
      if (list != null && list.size() > 0) {
        for (Element e : list) {
          String src = e.attr("src");
          if (src != null && src.startsWith("/repo")) {
            p = 1;
            break;
          }
        }
      }
      v.set("photo", p);

      Topic.update(id, v);

      this.redirect("/forum/topic/detail?id=" + id);
      return;
    }
    this.set("t", t);
    this.set("c", t.getCircling());
    this.set("id", t.getId());
    this.set("cid", t.getCid());

    this.show("/forum/topic.edit.html");
  }

  @Path(path = "expose", login = true)
  public void expose() {
    JSONObject jo = new JSONObject();
    long id = this.getLong("id");

    Topic t = Topic.load(id);
    Circling c = t.getCircling();
    Expose.create(V.create("cid", t.getCid()).set("owner", c.getOwner()).set("tid", id).set("reporter", login.getId())
        .set("state", 0));
    jo.put(X.STATE, 200);
    jo.put(X.MESSAGE, lang.get("topic.expose.success"));
    this.response(jo);
  }

  @Path(path = "detail")
  public void detail() {
    long id = this.getLong("id");
    Topic t = Topic.load(id);
    this.set("t", t);

    Circling c = t.getCircling();
    this.set("c", c);
    this.set("id", t.getId());
    this.set("cid", t.getCid());

    login = getUser();
    this.set("me", login);
    Follower f = c.getFollower(login);
    if (c.isPrivate() && (f == null || !f.getPost())) {
      log.warn("deny to access the circling, user=" + login + ", circling=" + c.getId());
      this.deny("/forum", null);
      return;
    }

    if (Log.create(V.create("topic_id", id).set("sid", sid()))) {
      t.update(V.create("reads", t.getReads() + 1).set("updated", t.getLong("updated")));
    }

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");
    Beans<Topic> bs = Topic.load(W.create("parent", t.getId()).sort("created", 1), s, n);

    this.set(bs, s, n);
    this.query.path("/forum/topic/detail");

    /**
     * get recommends
     */
    Beans<Topic> bs1 = Topic
        .load(W.create("cid", t.getCid()).and("parent", 0).and(X.ID, id, W.OP_NEQ).sort("updated", -1), 0, 20);
    this.set("recommends", bs1 == null ? null : bs1.getList());
    this.show("/forum/topic.detail.html");
  }

  public String toHtml(Topic r) {
    StringBuilder sb = new StringBuilder("<blockquote>");
    Topic r1 = r.getRefer();
    if (r1 != null) {
      sb.append(toHtml(r1));
    }
    sb.append("<div class='block'><div class='user'>");

    if (!X.isEmpty(r.getOwner_obj().get("photo"))) {
      sb.append("<img src=").append(r.getOwner_obj().get("photo")).append(" class='user-sm'>");
    } else {
      sb.append("<span class='icon-user'></span>");
    }
    sb.append(r.getOwner_obj().getNickname()).append(":</div>");

    if (r.getDeleted() == 1) {
      sb.append("<div class='del icon icon-warning'>");
      sb.append(lang.get("topic.was.deleted"));
      sb.append("</div>");
    } else {
      sb.append("<div class='content'>").append(r.getContent()).append("</div>");
    }

    sb.append("</div>");
    sb.append("</blockquote>");
    return sb.toString();
  }

  public String flags(Topic t) {
    StringBuilder sb = new StringBuilder();
    int f1 = 0;
    if (t.getInt("cream") == 1) {
      // cream topic
      sb.append("<img src='/images/flags/cream.png'/>");
      f1++;
    }
    if (t.getInt("top") == 1) {
      sb.append("<img src='/images/flags/top.png'/>");
      f1++;
    }
    if (t.replysInDays(1) > 10) {
      sb.append("<img src='/images/flags/hot.png'/>");
      f1++;
    }
    if (t.getInt("photo") == 1) {
      sb.append("<img src='/images/flags/photo.png'/>");
      f1++;
    }

    return "<span class='flags flag-" + f1 + "'>" + sb.toString() + "</span>";
  }
}
