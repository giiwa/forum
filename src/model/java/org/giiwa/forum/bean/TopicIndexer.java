package org.giiwa.forum.bean;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.giiwa.framework.web.Model;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.X;
import org.giiwa.tinyse.se.SE.Indexer;

public class TopicIndexer implements Indexer {

  @Override
  public void bad(String engine, Object id, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Topic.update(X.toLong(id, -1), V.create("index_flag", flag).set("index_state", "bad").ignore("updated"));
  }

  @Override
  public void done(String engine, Object id, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Topic.update(X.toLong(id, -1), V.create("index_flag", flag).set("index_state", "done").ignore("updated"));
  }

  @Override
  public Document load(String engine, Object id) {
    // TODO Auto-generated method stub
    Topic c = Topic.load(X.toLong(id, -1));
    if (c != null) {
      Document d = new Document();
      if (!X.isEmpty(c.getTitle()))
        d.add(new TextField("title", c.getTitle(), Store.YES));
      if (c.getOwner_obj() != null && !X.isEmpty(c.getOwner_obj().getNickname()))
        d.add(new TextField("nickname", c.getOwner_obj().getName(), Store.YES));
      if (!X.isEmpty(c.getContent()))
        d.add(new TextField("content", c.getContent(), Store.YES));

      d.add(new LongField("circling", c.getCid(), Store.YES));

      return d;
    }
    return null;
  }

  @Override
  public Object next(String engine, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Topic c = Topic.load(W.create().and("index_flag", flag, W.OP.neq).sort(X.ID, 1));
    return c == null ? null : c.getId();
  }

}
