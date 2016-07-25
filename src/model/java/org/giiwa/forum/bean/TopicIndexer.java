package org.giiwa.forum.bean;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.giiwa.core.bean.Bean.V;
import org.giiwa.framework.web.Model;
import org.giiwa.core.bean.X;
import org.giiwa.tinyse.se.SE.Indexer;

import com.mongodb.BasicDBObject;

public class TopicIndexer implements Indexer {

  @Override
  public void bad(Object id, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Topic.update(X.toLong(id, -1), V.create(node + "_timestamp", flag).set(node + "_state", "bad"));
  }

  @Override
  public void done(Object id, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Topic.update(X.toLong(id, -1), V.create(node + "_timestamp", flag).set(node + "_state", "done"));
  }

  @Override
  public Document load(Object id) {
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
  public Object next(long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Topic c = Topic.load(new BasicDBObject(node + "_timestamp", new BasicDBObject("$ne", flag)),
        new BasicDBObject(X._ID, 1));
    return c == null ? null : c.getId();
  }

}
