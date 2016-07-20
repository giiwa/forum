package org.giiwa.forum.bean;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.giiwa.core.bean.Bean.V;
import org.giiwa.core.bean.X;
import org.giiwa.tinyse.se.SE.Indexer;

import com.mongodb.BasicDBObject;

public class CirclingIndexer implements Indexer {

  @Override
  public void bad(Object id, long flag) {
    // TODO Auto-generated method stub
    Circling.update(X.toLong(id, -1), V.create("se_timestamp", flag).set("se_state", "bad"));
  }

  @Override
  public void done(Object id, long flag) {
    // TODO Auto-generated method stub
    Circling.update(X.toLong(id, -1), V.create("se_timestamp", flag).set("se_state", "done"));
  }

  @Override
  public Document load(Object id) {
    // TODO Auto-generated method stub
    Circling c = Circling.load(X.toLong(id, -1));
    if (c != null) {
      Document d = new Document();
      if (!X.isEmpty(c.getName()))
        d.add(new TextField("name", c.getName(), Store.NO));
      if (c.getOwner_obj() != null && !X.isEmpty(c.getOwner_obj().getNickname()))
        d.add(new TextField("owner", c.getOwner_obj().getName(), Store.NO));
      if (!X.isEmpty(c.getMemo()))
        d.add(new TextField("memo", c.getMemo(), Store.NO));
      return d;
    }
    return null;
  }

  @Override
  public Object next(long flag) {
    // TODO Auto-generated method stub
    Circling c = Circling.load(new BasicDBObject("se_timestamp", new BasicDBObject("$ne", flag)),
        new BasicDBObject(X._ID, 1));
    return c == null ? null : c.getId();
  }

}
