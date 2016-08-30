package org.giiwa.forum.bean;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.X;
import org.giiwa.framework.web.Model;
import org.giiwa.tinyse.se.SE.Indexer;

public class CirclingIndexer implements Indexer {

  @Override
  public void bad(Object id, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Circling.update(X.toLong(id, -1), V.create("index_flag", flag).set("index_state", "bad").set("updated", V.ignore));
  }

  @Override
  public void done(Object id, long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Circling.update(X.toLong(id, -1), V.create("index_flag", flag).set("index_state", "done").set("updated", V.ignore));
  }

  @Override
  public Document load(Object id) {
    // TODO Auto-generated method stub
    Circling c = Circling.load(X.toLong(id, -1));
    if (c != null) {
      Document d = new Document();
      if (!X.isEmpty(c.getName()))
        d.add(new TextField("name", c.getName(), Store.YES));
      if (c.getOwner_obj() != null && !X.isEmpty(c.getOwner_obj().getNickname()))
        d.add(new TextField("nickname", c.getOwner_obj().getName(), Store.YES));
      if (!X.isEmpty(c.getMemo()))
        d.add(new TextField("memo", c.getMemo(), Store.YES));
      return d;
    }
    return null;
  }

  @Override
  public Object next(long flag) {
    // TODO Auto-generated method stub
    String node = Model.node();
    Circling c = Circling.load(W.create().and("index_flag", flag, W.OP_LT).sort(X.ID, 1));
    return c == null ? null : c.getId();
  }

}
