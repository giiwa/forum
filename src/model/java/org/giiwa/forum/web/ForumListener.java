package org.giiwa.forum.web;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.giiwa.forum.bean.CirclingIndexer;
import org.giiwa.forum.bean.TopicIndexer;
import org.giiwa.framework.web.IListener;
import org.giiwa.framework.web.Module;
import org.giiwa.tinyse.se.SE;

public class ForumListener implements IListener {

  static Log log = LogFactory.getLog(ForumListener.class);

  @Override
  public void onStart(Configuration conf, Module m) {
    // TODO Auto-generated method stub
    log.info("forum is starting ...");

    SE.init(conf);
    SE.register("circling", new CirclingIndexer());
    SE.register("topic", new TopicIndexer());

  }

  @Override
  public void onStop() {
    // TODO Auto-generated method stub

  }

  @Override
  public void uninstall(Configuration conf, Module m) {
    // TODO Auto-generated method stub

  }

  @Override
  public void upgrade(Configuration conf, Module m) {
    // TODO Auto-generated method stub

  }

}
